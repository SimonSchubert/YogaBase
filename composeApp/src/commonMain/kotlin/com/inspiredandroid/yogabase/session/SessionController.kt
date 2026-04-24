package com.inspiredandroid.yogabase.session

import androidx.navigation.NavController
import com.inspiredandroid.yogabase.audio.AudioPlayer
import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.Pose
import com.inspiredandroid.yogabase.data.SessionCategory
import com.inspiredandroid.yogabase.data.YogaRepository
import com.inspiredandroid.yogabase.navigation.Finish
import com.inspiredandroid.yogabase.navigation.MainMenu
import com.inspiredandroid.yogabase.storage.UserStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import yogabase.composeapp.generated.resources.Res
import kotlin.random.Random

@OptIn(ExperimentalResourceApi::class)
class SessionController(
    private val repo: YogaRepository,
    val storage: UserStorage,
    private val audio: AudioPlayer,
    private val navController: NavController,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow<SessionUiState?>(null)
    val state: StateFlow<SessionUiState?> = _state.asStateFlow()

    private var runJob: Job? = null
    private var paused: Boolean = false
    private var voice: Boolean = storage.voiceEnabled()
    private var skipIntroRequested: Boolean = false
    private var currentDifficulty: Difficulty = Difficulty.INTERMEDIATE

    fun startSession(category: SessionCategory, difficulty: Difficulty) {
        cancel()
        paused = false
        skipIntroRequested = false
        voice = storage.voiceEnabled()
        currentDifficulty = difficulty
        val poses = repo.posesForCategory(category)
        _state.value = SessionUiState(
            category = category,
            poses = poses,
            difficulty = difficulty,
            phase = SessionPhase.Idle,
            paused = false,
            voiceEnabled = voice,
            progress = 0f,
            streakAfter = storage.getStreak(),
        )
        runJob = scope.launch { runSession(category, poses) }
    }

    fun setDifficulty(difficulty: Difficulty) {
        currentDifficulty = difficulty
        _state.update { it?.copy(difficulty = difficulty) }
    }

    private suspend fun runSession(
        category: SessionCategory,
        poses: List<Pose>,
    ) {
        if (poses.isEmpty()) {
            _state.update { it?.copy(phase = SessionPhase.Finished) }
            return
        }

        // --- Intro ---
        if (voice) playRes(INTRO_AUDIO.random(Random.Default))
        var introRemaining = INTRO_SECONDS
        _state.update { it?.copy(phase = SessionPhase.Intro(introRemaining)) }
        while (introRemaining > 0 && !skipIntroRequested) {
            delay(1_000)
            if (paused) continue
            introRemaining--
            _state.update { it?.copy(phase = SessionPhase.Intro(introRemaining)) }
        }

        // --- Poses ---
        // Lock in final difficulty chosen during intro.
        val difficulty = currentDifficulty
        val totalDurationSeconds = poses.sumOf { (it.baseTime * difficulty.multiplier).toInt().coerceAtLeast(1) }
        var elapsedSeconds = 0
        for ((index, pose) in poses.withIndex()) {
            val total = (pose.baseTime * difficulty.multiplier).toInt().coerceAtLeast(1)
            var remaining = total
            var sideSwitched = false
            if (voice) playRes("files/audio/${pose.id}.mp3")
            _state.update {
                it?.copy(
                    phase = SessionPhase.Holding(
                        poseIndex = index,
                        totalPoses = poses.size,
                        secondsRemaining = remaining,
                        totalSeconds = total,
                        sideSwitched = false,
                    ),
                    progress = elapsedSeconds.toFloat() / totalDurationSeconds.coerceAtLeast(1),
                )
            }

            while (remaining > 0) {
                delay(1_000)
                if (paused) continue
                remaining--
                elapsedSeconds++
                if (pose.hasTwoSides && !sideSwitched && remaining <= total / 2) {
                    sideSwitched = true
                    if (voice) playRes(SWITCH_SIDE_AUDIO)
                }
                _state.update {
                    it?.copy(
                        phase = SessionPhase.Holding(
                            poseIndex = index,
                            totalPoses = poses.size,
                            secondsRemaining = remaining,
                            totalSeconds = total,
                            sideSwitched = sideSwitched,
                        ),
                        progress = elapsedSeconds.toFloat() / totalDurationSeconds.coerceAtLeast(1),
                    )
                }
            }
            // 1s gap between poses
            delay(1_000)
        }

        // --- Finish ---
        if (voice) playRes(CONGRATS_AUDIO)
        val result = storage.recordSessionCompleted(category.id)
        _state.update {
            it?.copy(
                phase = SessionPhase.Finished,
                progress = 1f,
                streakAfter = result.newStreak,
            )
        }
        navController.navigate(Finish(category.id)) {
            popUpTo(MainMenu) { inclusive = false }
        }
    }

    fun togglePause() {
        paused = !paused
        if (paused) audio.pause() else audio.resume()
        _state.update { it?.copy(paused = paused) }
    }

    fun skipIntro() {
        skipIntroRequested = true
    }

    fun toggleVoice() {
        voice = !voice
        storage.setVoiceEnabled(voice)
        if (!voice) audio.stop()
        _state.update { it?.copy(voiceEnabled = voice) }
    }

    fun cancel() {
        runJob?.cancel()
        runJob = null
        audio.stop()
        _state.value = null
    }

    fun dispose() {
        cancel()
        scope.cancel()
        audio.release()
    }

    private suspend fun playRes(path: String) {
        try {
            audio.play(Res.readBytes(path), loop = false)
        } catch (_: Exception) {
            // Missing audio — silently skip.
        }
    }

    companion object {
        const val INTRO_SECONDS = 10
        val INTRO_AUDIO = listOf(
            "files/audio/start_01.mp3",
            "files/audio/start_02.mp3",
            "files/audio/start_03.mp3",
        )
        const val SWITCH_SIDE_AUDIO = "files/audio/switch_side.mp3"
        const val CONGRATS_AUDIO = "files/audio/congratulations.mp3"
    }
}

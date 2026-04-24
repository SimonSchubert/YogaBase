package com.inspiredandroid.yogabase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.Pose
import com.inspiredandroid.yogabase.data.SessionCategory
import com.inspiredandroid.yogabase.session.SessionPhase
import com.inspiredandroid.yogabase.session.SessionUiState
import com.inspiredandroid.yogabase.ui.components.BackIcon
import com.inspiredandroid.yogabase.ui.components.DifficultySelector
import com.inspiredandroid.yogabase.ui.components.PoseImage
import com.inspiredandroid.yogabase.ui.components.PosePreviewStrip
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.back
import yogabase.composeapp.generated.resources.cancel
import yogabase.composeapp.generated.resources.get_ready
import yogabase.composeapp.generated.resources.pause
import yogabase.composeapp.generated.resources.quit
import yogabase.composeapp.generated.resources.quit_session_message
import yogabase.composeapp.generated.resources.quit_session_title
import yogabase.composeapp.generated.resources.resume
import yogabase.composeapp.generated.resources.skip_intro
import yogabase.composeapp.generated.resources.timer_get_ready
import yogabase.composeapp.generated.resources.timer_hold
import yogabase.composeapp.generated.resources.timer_next_soon
import yogabase.composeapp.generated.resources.total_duration
import yogabase.composeapp.generated.resources.voice_off
import yogabase.composeapp.generated.resources.voice_on

private const val INTRO_PLACEHOLDER_POSE_ID = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    category: SessionCategory,
    poses: List<Pose>,
    state: SessionUiState?,
    selectedDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    onSkipIntro: () -> Unit,
    onTogglePause: () -> Unit,
    onToggleVoice: () -> Unit,
    onBack: () -> Unit,
    onFinished: () -> Unit,
) {
    val phase = state?.phase ?: SessionPhase.Idle
    val voiceEnabled = state?.voiceEnabled ?: true

    LaunchedEffect(phase) {
        if (phase is SessionPhase.Finished) onFinished()
    }

    var showQuitDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = category.categoryName,
                    maxLines = 1,
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (phase is SessionPhase.Intro) onBack() else showQuitDialog = true
                }) {
                    Icon(
                        imageVector = BackIcon,
                        contentDescription = stringResource(Res.string.back),
                    )
                }
            },
            actions = {
                IconButton(onClick = onToggleVoice) {
                    Icon(
                        imageVector = if (voiceEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = stringResource(
                            if (voiceEnabled) Res.string.voice_on else Res.string.voice_off,
                        ),
                    )
                }
            },
        )

        when (val current = phase) {
            SessionPhase.Idle -> Box(modifier = Modifier.fillMaxSize())

            is SessionPhase.Intro -> IntroBody(
                poses = poses,
                selectedDifficulty = selectedDifficulty,
                onDifficultyChange = onDifficultyChange,
                secondsRemaining = current.secondsRemaining,
                onSkip = onSkipIntro,
            )

            is SessionPhase.Holding -> {
                val nonNullState = state ?: return@Column
                HoldingBody(
                    poses = poses,
                    state = nonNullState,
                    phase = current,
                    onTogglePause = onTogglePause,
                )
            }

            SessionPhase.Finished -> {
                // navigation will take over; show a placeholder
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "…")
                }
            }
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text(stringResource(Res.string.quit_session_title)) },
            text = { Text(stringResource(Res.string.quit_session_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    onBack()
                }) {
                    Text(stringResource(Res.string.quit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun IntroBody(
    poses: List<Pose>,
    selectedDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    secondsRemaining: Int,
    onSkip: () -> Unit,
) {
    val totalSeconds = poses.sumOf {
        (it.baseTime * selectedDifficulty.multiplier).toInt().coerceAtLeast(1)
    }
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth > maxHeight) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PoseImage(
                    poseId = INTRO_PLACEHOLDER_POSE_ID,
                    modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                )
                Spacer(Modifier.width(24.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    IntroInfo(
                        secondsRemaining = secondsRemaining,
                        selectedDifficulty = selectedDifficulty,
                        onDifficultyChange = onDifficultyChange,
                        minutes = minutes,
                        seconds = seconds,
                        onSkip = onSkip,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                PoseImage(
                    poseId = INTRO_PLACEHOLDER_POSE_ID,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                )
                Spacer(Modifier.height(16.dp))
                IntroInfo(
                    secondsRemaining = secondsRemaining,
                    selectedDifficulty = selectedDifficulty,
                    onDifficultyChange = onDifficultyChange,
                    minutes = minutes,
                    seconds = seconds,
                    onSkip = onSkip,
                )
            }
        }
    }
}

@Composable
private fun IntroInfo(
    secondsRemaining: Int,
    selectedDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    minutes: Int,
    seconds: Int,
    onSkip: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.get_ready),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = formatTime(secondsRemaining),
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = stringResource(Res.string.timer_get_ready),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(24.dp))
    DifficultySelector(
        selected = selectedDifficulty,
        onSelected = onDifficultyChange,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.total_duration, minutes, seconds),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(24.dp))
    FilledTonalButton(onClick = onSkip) {
        Text(stringResource(Res.string.skip_intro))
    }
}

@Composable
private fun HoldingBody(
    poses: List<Pose>,
    state: SessionUiState,
    phase: SessionPhase.Holding,
    onTogglePause: () -> Unit,
) {
    val pose = poses.getOrNull(phase.poseIndex) ?: return
    val label = when {
        phase.secondsRemaining <= 3 -> stringResource(Res.string.timer_next_soon)
        else -> stringResource(Res.string.timer_hold)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(6.dp),
        )
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (maxWidth > maxHeight) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PoseImage(
                        poseId = pose.id,
                        mirrored = phase.sideSwitched,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                        contentDescription = pose.englishName,
                    )
                    Spacer(Modifier.width(24.dp))
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        HoldingInfo(
                            pose = pose,
                            secondsRemaining = phase.secondsRemaining,
                            label = label,
                            poses = poses,
                            currentIndex = phase.poseIndex,
                            paused = state.paused,
                            onTogglePause = onTogglePause,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PoseImage(
                        poseId = pose.id,
                        mirrored = phase.sideSwitched,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).aspectRatio(1f),
                        contentDescription = pose.englishName,
                    )
                    Spacer(Modifier.height(8.dp))
                    HoldingInfo(
                        pose = pose,
                        secondsRemaining = phase.secondsRemaining,
                        label = label,
                        poses = poses,
                        currentIndex = phase.poseIndex,
                        paused = state.paused,
                        onTogglePause = onTogglePause,
                    )
                }
            }
        }
    }
}

@Composable
private fun HoldingInfo(
    pose: Pose,
    secondsRemaining: Int,
    label: String,
    poses: List<Pose>,
    currentIndex: Int,
    paused: Boolean,
    onTogglePause: () -> Unit,
) {
    Text(
        text = pose.englishName,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = formatTime(secondsRemaining),
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    PosePreviewStrip(poses = poses, currentIndex = currentIndex)
    Spacer(Modifier.height(16.dp))
    FilledTonalButton(
        onClick = onTogglePause,
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
    ) {
        Icon(
            imageVector = if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
            contentDescription = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(
                if (paused) Res.string.resume else Res.string.pause,
            ),
        )
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    val mm = m.toString().padStart(2, '0')
    val ss = s.toString().padStart(2, '0')
    return "$mm:$ss"
}

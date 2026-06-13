package com.inspiredandroid.yogabase.breathing

import com.inspiredandroid.yogabase.data.BreathingStepDuration
import com.inspiredandroid.yogabase.data.BreathingTechnique
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

class BreathingController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow<BreathingUiState?>(null)
    val state: StateFlow<BreathingUiState?> = _state.asStateFlow()

    private var runJob: Job? = null
    private var paused = false

    fun start(technique: BreathingTechnique) {
        cancel()
        paused = false
        _state.value = BreathingUiState(
            technique = technique,
            phase = BreathingPhase.Idle,
            paused = false,
        )
        runJob = scope.launch { runSession(technique) }
    }

    fun togglePause() {
        paused = !paused
        _state.update { it?.copy(paused = paused) }
    }

    fun cancel() {
        runJob?.cancel()
        runJob = null
        paused = false
        _state.value = null
    }

    fun dispose() {
        cancel()
        scope.cancel()
    }

    private suspend fun runSession(technique: BreathingTechnique) {
        val steps = technique.steps()
        if (steps.isEmpty()) {
            _state.update { it?.copy(phase = BreathingPhase.Finished) }
            return
        }

        for (cycle in 0 until technique.cycles) {
            for (step in steps) {
                runStep(
                    step = step,
                    cycleIndex = cycle + 1,
                    totalCycles = technique.cycles,
                )
            }
        }

        _state.update { it?.copy(phase = BreathingPhase.Finished, paused = false) }
    }

    private suspend fun runStep(
        step: BreathingStepDuration,
        cycleIndex: Int,
        totalCycles: Int,
    ) {
        val totalMs = step.seconds * 1000L
        var elapsedMs = 0L

        while (elapsedMs < totalMs) {
            while (paused) delay(TICK_MS)

            elapsedMs = (elapsedMs + TICK_MS).coerceAtMost(totalMs)
            val progress = elapsedMs.toFloat() / totalMs
            val remainingMs = totalMs - elapsedMs
            val secondsRemaining = ((remainingMs + 999) / 1000).toInt().coerceAtLeast(1)

            _state.update {
                it?.copy(
                    phase = BreathingPhase.Active(
                        step = step.step,
                        stepProgress = progress,
                        secondsRemaining = secondsRemaining,
                        secondsTotal = step.seconds,
                        cycleIndex = cycleIndex,
                        totalCycles = totalCycles,
                    ),
                    paused = paused,
                )
            }
            delay(TICK_MS)
        }
    }

    companion object {
        private const val TICK_MS = 50L
    }
}

package com.inspiredandroid.yogabase.breathing

import com.inspiredandroid.yogabase.data.BreathingStep

sealed class BreathingPhase {
    data object Idle : BreathingPhase()

    data class Active(
        val step: BreathingStep,
        val stepProgress: Float,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val cycleIndex: Int,
        val totalCycles: Int,
    ) : BreathingPhase()

    data object Finished : BreathingPhase()
}

package com.inspiredandroid.yogabase.breathing

import com.inspiredandroid.yogabase.data.BreathingTechnique

data class BreathingUiState(
    val technique: BreathingTechnique,
    val phase: BreathingPhase,
    val paused: Boolean,
)

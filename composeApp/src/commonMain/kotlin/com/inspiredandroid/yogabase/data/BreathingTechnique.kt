package com.inspiredandroid.yogabase.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BreathingTechnique(
    val id: String,
    val name: String,
    val description: String,
    val benefit: String,
    @SerialName("inhale_seconds") val inhaleSeconds: Int,
    @SerialName("hold_in_seconds") val holdInSeconds: Int,
    @SerialName("exhale_seconds") val exhaleSeconds: Int,
    @SerialName("hold_out_seconds") val holdOutSeconds: Int,
    val cycles: Int,
) {
    val cycleDurationSeconds: Int
        get() = inhaleSeconds + holdInSeconds + exhaleSeconds + holdOutSeconds

    val totalDurationSeconds: Int
        get() = cycleDurationSeconds * cycles

    fun steps(): List<BreathingStepDuration> = buildList {
        if (inhaleSeconds > 0) add(BreathingStepDuration(BreathingStep.Inhale, inhaleSeconds))
        if (holdInSeconds > 0) add(BreathingStepDuration(BreathingStep.HoldIn, holdInSeconds))
        if (exhaleSeconds > 0) add(BreathingStepDuration(BreathingStep.Exhale, exhaleSeconds))
        if (holdOutSeconds > 0) add(BreathingStepDuration(BreathingStep.HoldOut, holdOutSeconds))
    }
}

enum class BreathingStep {
    Inhale,
    HoldIn,
    Exhale,
    HoldOut,
}

data class BreathingStepDuration(
    val step: BreathingStep,
    val seconds: Int,
)

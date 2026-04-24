package com.inspiredandroid.yogabase.session

sealed interface SessionPhase {
    data object Idle : SessionPhase

    data class Intro(val secondsRemaining: Int) : SessionPhase

    data class Holding(
        val poseIndex: Int,
        val totalPoses: Int,
        val secondsRemaining: Int,
        val totalSeconds: Int,
        val sideSwitched: Boolean,
    ) : SessionPhase

    data object Finished : SessionPhase
}

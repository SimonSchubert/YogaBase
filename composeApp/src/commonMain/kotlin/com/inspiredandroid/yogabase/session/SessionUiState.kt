package com.inspiredandroid.yogabase.session

import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.Pose
import com.inspiredandroid.yogabase.data.SessionCategory

data class SessionUiState(
    val category: SessionCategory,
    val poses: List<Pose>,
    val difficulty: Difficulty,
    val phase: SessionPhase,
    val paused: Boolean,
    val voiceEnabled: Boolean,
    val progress: Float,
    val streakAfter: Int,
)

package com.inspiredandroid.yogabase.data

enum class Difficulty(val multiplier: Float) {
    BEGINNER(0.5f),
    INTERMEDIATE(1.0f),
    ADVANCED(2.0f),
    ;

    companion object {
        fun fromString(s: String): Difficulty = when (s.lowercase()) {
            "beginner" -> BEGINNER
            "intermediate" -> INTERMEDIATE
            "advanced" -> ADVANCED
            else -> BEGINNER
        }
    }
}

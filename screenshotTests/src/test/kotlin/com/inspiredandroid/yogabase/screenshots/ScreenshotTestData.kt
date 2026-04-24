package com.inspiredandroid.yogabase.screenshots

import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.Pose
import com.inspiredandroid.yogabase.data.SessionCategory
import com.inspiredandroid.yogabase.session.SessionPhase
import com.inspiredandroid.yogabase.session.SessionUiState

val samplePoses: List<Pose> = listOf(
    pose(11, "Child", "Balasana", baseTime = 45),
    pose(4, "Bridge", "Setu Bandha Sarvangasana", baseTime = 60, difficulty = "Beginner"),
    pose(7, "Cat", "Marjaryasana", baseTime = 30, difficulty = "Beginner"),
    pose(10, "Corpse", "Savasana", baseTime = 60, difficulty = "Beginner"),
    pose(35, "Seated Forward Bend", "Paschimottanasana", baseTime = 45, difficulty = "Beginner"),
    pose(5, "Butterfly", "Baddha Konasana", baseTime = 45, difficulty = "Beginner"),
    pose(12, "Cobra", "Bhujangasana", baseTime = 30),
    pose(15, "Downward-Facing Dog", "Adho Mukha Svanasana", baseTime = 45),
    pose(23, "Mountain", "Tadasana", baseTime = 30, difficulty = "Beginner"),
    pose(38, "Standing Forward Bend", "Uttanasana", baseTime = 30),
    pose(41, "Tree", "Vrksasana", baseTime = 45, hasTwoSides = true),
    pose(43, "Warrior I", "Virabhadrasana I", baseTime = 45, hasTwoSides = true),
    pose(44, "Warrior II", "Virabhadrasana II", baseTime = 45, hasTwoSides = true),
    pose(45, "Warrior III", "Virabhadrasana III", baseTime = 40, hasTwoSides = true, difficulty = "Advanced"),
    pose(42, "Triangle", "Trikonasana", baseTime = 45, hasTwoSides = true),
    pose(26, "Pigeon", "Eka Pada Rajakapotasana", baseTime = 60, hasTwoSides = true, difficulty = "Advanced"),
    pose(8, "Chair", "Utkatasana", baseTime = 30),
    pose(9, "Crescent Lunge", "Anjaneyasana", baseTime = 45, hasTwoSides = true),
)

val sampleCategories: List<SessionCategory> = listOf(
    SessionCategory(
        id = 1,
        categoryName = "Gentle Beginner Flow",
        categoryDescription = "A soothing sequence for beginners to ease into yoga with foundational poses and breath focus.",
        poses = listOf(4, 7, 10, 11, 35),
        difficulty = "Beginner",
    ),
    SessionCategory(
        id = 2,
        categoryName = "Morning Wake-Up",
        categoryDescription = "A beginner-friendly sequence to energize and stretch the body for the day ahead.",
        poses = listOf(5, 12, 15, 38, 41, 43, 45),
        difficulty = "Beginner",
    ),
    SessionCategory(
        id = 10,
        categoryName = "Warrior Strength",
        categoryDescription = "Build power and balance through a warrior-focused flow.",
        poses = listOf(23, 43, 44, 42, 41, 11),
        difficulty = "Intermediate",
    ),
    SessionCategory(
        id = 20,
        categoryName = "Deep Hip Opener",
        categoryDescription = "An advanced sequence for opening the hips with sustained holds.",
        poses = listOf(26, 9, 45, 42, 5, 10),
        difficulty = "Advanced",
    ),
)

val practiceCategory: SessionCategory = sampleCategories[1]
val practicePoses: List<Pose> =
    practiceCategory.poses.mapNotNull { id -> samplePoses.firstOrNull { it.id == id } }

fun introState(secondsRemaining: Int = 7): SessionUiState = SessionUiState(
    category = practiceCategory,
    poses = practicePoses,
    difficulty = Difficulty.INTERMEDIATE,
    phase = SessionPhase.Intro(secondsRemaining),
    paused = false,
    voiceEnabled = true,
    progress = 0f,
    streakAfter = 5,
)

fun holdingState(
    poseIndex: Int = 2,
    secondsRemaining: Int = 28,
    totalSeconds: Int = 45,
    sideSwitched: Boolean = false,
    progress: Float = 0.38f,
): SessionUiState = SessionUiState(
    category = practiceCategory,
    poses = practicePoses,
    difficulty = Difficulty.INTERMEDIATE,
    phase = SessionPhase.Holding(
        poseIndex = poseIndex,
        totalPoses = practicePoses.size,
        secondsRemaining = secondsRemaining,
        totalSeconds = totalSeconds,
        sideSwitched = sideSwitched,
    ),
    paused = false,
    voiceEnabled = true,
    progress = progress,
    streakAfter = 5,
)

private fun pose(
    id: Int,
    englishName: String,
    sanskrit: String,
    baseTime: Int = 45,
    difficulty: String = "Intermediate",
    hasTwoSides: Boolean = false,
): Pose = Pose(
    id = id,
    englishName = englishName,
    sanskritNameAdapted = sanskrit,
    sanskritName = sanskrit,
    translationName = "",
    poseDescription = "",
    poseBenefits = "",
    difficulty = difficulty,
    baseTime = baseTime,
    hasTwoSides = hasTwoSides,
)

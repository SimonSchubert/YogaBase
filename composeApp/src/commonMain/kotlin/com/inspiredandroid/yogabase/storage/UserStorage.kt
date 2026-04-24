package com.inspiredandroid.yogabase.storage

import com.russhwolf.settings.Settings
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.title_adept
import yogabase.composeapp.generated.resources.title_devotee
import yogabase.composeapp.generated.resources.title_master
import yogabase.composeapp.generated.resources.title_novice
import yogabase.composeapp.generated.resources.title_practitioner
import yogabase.composeapp.generated.resources.title_teacher
import kotlin.time.Clock

class UserStorage(private val settings: Settings = Settings()) {

    fun completedCategoryIds(): Set<Int> = settings.getStringOrNull(KEY_COMPLETED_CATEGORIES)
        ?.split(",")
        ?.mapNotNull { it.toIntOrNull() }
        ?.toSet()
        .orEmpty()

    fun markCategoryCompleted(id: Int) {
        val current = completedCategoryIds().toMutableSet()
        current += id
        settings.putString(KEY_COMPLETED_CATEGORIES, current.joinToString(","))
    }

    fun getStreak(): Int = settings.getInt(KEY_STREAK, 0)

    fun getLastCompletedDate(): LocalDate? = settings.getStringOrNull(KEY_LAST_COMPLETED_DATE)?.let {
        runCatching { LocalDate.parse(it) }.getOrNull()
    }

    fun voiceEnabled(): Boolean = settings.getBoolean(KEY_VOICE_ENABLED, true)
    fun setVoiceEnabled(enabled: Boolean) = settings.putBoolean(KEY_VOICE_ENABLED, enabled)

    fun getTotalXp(): Int = settings.getInt(KEY_TOTAL_XP, 0)

    fun getLevel(): Int = levelForXp(getTotalXp())

    private fun addXp(amount: Int): LevelChange? {
        if (amount <= 0) return null
        val before = getTotalXp()
        val after = before + amount
        settings.putInt(KEY_TOTAL_XP, after)
        val oldLevel = levelForXp(before)
        val newLevel = levelForXp(after)
        return if (newLevel > oldLevel) {
            LevelChange(oldLevel, newLevel, before, after)
        } else {
            null
        }
    }

    /**
     * Records a session completion for today. Awards XP only for the first completion per day
     * (subsequent same-day sessions still mark the category as completed but grant 0 XP).
     */
    fun recordSessionCompleted(
        categoryId: Int,
        today: LocalDate = todayLocal(),
    ): SessionCompletionResult {
        val last = getLastCompletedDate()
        val alreadyCompletedToday = last == today
        val newStreak = when {
            last == null -> 1
            alreadyCompletedToday -> getStreak().coerceAtLeast(1)
            last == today.minus(1, DateTimeUnit.DAY) -> getStreak() + 1
            else -> 1
        }
        settings.putInt(KEY_STREAK, newStreak)
        settings.putString(KEY_LAST_COMPLETED_DATE, today.toString())
        markCategoryCompleted(categoryId)

        val xpAwarded = if (alreadyCompletedToday) 0 else SESSION_COMPLETION_XP
        val levelChange = addXp(xpAwarded)
        return SessionCompletionResult(newStreak, xpAwarded, levelChange)
    }

    /**
     * Returns the streak if it's still valid (last completion was today or yesterday),
     * otherwise zeroes it out and returns 0.
     */
    fun refreshStreakOnLaunch(today: LocalDate = todayLocal()): Int {
        val last = getLastCompletedDate()
        return when {
            last == null -> {
                settings.putInt(KEY_STREAK, 0)
                0
            }
            last == today || last == today.minus(1, DateTimeUnit.DAY) -> getStreak()
            else -> {
                settings.putInt(KEY_STREAK, 0)
                0
            }
        }
    }

    fun incrementAndGetTotalAppOpens(): Int {
        val next = settings.getInt(KEY_APP_OPENS, 0) + 1
        settings.putInt(KEY_APP_OPENS, next)
        return next
    }

    data class LevelChange(
        val oldLevel: Int,
        val newLevel: Int,
        val totalXpBefore: Int,
        val totalXpAfter: Int,
    ) {
        val isMilestone: Boolean
            get() = (oldLevel + 1..newLevel).any { isMilestoneLevel(it) }
    }

    data class SessionCompletionResult(
        val newStreak: Int,
        val xpGained: Int,
        val levelChange: LevelChange?,
    )

    data class MilestoneTitle(val level: Int, val titleRes: StringResource)

    companion object {
        const val KEY_COMPLETED_CATEGORIES = "completed_categories"
        const val KEY_STREAK = "streak"
        const val KEY_LAST_COMPLETED_DATE = "last_completed_date"
        const val KEY_VOICE_ENABLED = "voice_enabled"
        const val KEY_APP_OPENS = "app_opens"
        const val KEY_TOTAL_XP = "total_xp"

        const val SESSION_COMPLETION_XP = 50

        fun levelForXp(xp: Int): Int {
            if (xp <= 0) return 1
            var level = 1
            while (xpThresholdForLevel(level + 1) <= xp) level++
            return level
        }

        fun xpThresholdForLevel(level: Int): Int {
            val n = (level - 1).coerceAtLeast(0)
            return 50 * n * n
        }

        fun xpSpanForLevel(level: Int): Int = xpThresholdForLevel(level + 1) - xpThresholdForLevel(level)

        fun xpIntoLevel(xp: Int): Int = (xp - xpThresholdForLevel(levelForXp(xp))).coerceAtLeast(0)

        val MILESTONE_TITLES: List<MilestoneTitle> = listOf(
            MilestoneTitle(1, Res.string.title_novice),
            MilestoneTitle(5, Res.string.title_practitioner),
            MilestoneTitle(10, Res.string.title_adept),
            MilestoneTitle(20, Res.string.title_devotee),
            MilestoneTitle(30, Res.string.title_teacher),
            MilestoneTitle(50, Res.string.title_master),
        )

        fun currentTitleRes(level: Int): StringResource = MILESTONE_TITLES.lastOrNull { it.level <= level }?.titleRes ?: Res.string.title_novice

        fun isMilestoneLevel(level: Int): Boolean = MILESTONE_TITLES.any { it.level == level }
    }
}

internal fun todayLocal(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

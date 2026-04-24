package com.inspiredandroid.yogabase.screenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.yogabase.ui.screens.FinishScreen
import com.inspiredandroid.yogabase.ui.screens.MainMenuScreen
import com.inspiredandroid.yogabase.ui.screens.PracticeScreen
import com.inspiredandroid.yogabase.ui.theme.YogaBaseTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_9A.copy(softButtons = false),
        showSystemUi = true,
        maxPercentDifference = 0.1,
    )

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    private fun snap(content: @Composable () -> Unit) {
        paparazzi.unsafeUpdateConfig(theme = "android:Theme.Material.Light.NoActionBar")
        paparazzi.snapshot {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                YogaBaseTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp, bottom = 24.dp),
                    ) {
                        content()
                    }
                }
            }
        }
    }

    @Test
    fun mainMenu() {
        snap {
            MainMenuScreen(
                streak = 5,
                totalXp = 320,
                categories = sampleCategories,
                poses = samplePoses,
                completedCategoryIds = setOf(1),
                onCategoryClick = {},
            )
        }
    }

    @Test
    fun practiceIntro() {
        snap {
            PracticeScreen(
                category = practiceCategory,
                poses = practicePoses,
                state = introState(),
                selectedDifficulty = com.inspiredandroid.yogabase.data.Difficulty.INTERMEDIATE,
                onDifficultyChange = {},
                onSkipIntro = {},
                onTogglePause = {},
                onToggleVoice = {},
                onBack = {},
                onFinished = {},
            )
        }
    }

    @Test
    fun practiceHolding() {
        snap {
            PracticeScreen(
                category = practiceCategory,
                poses = practicePoses,
                state = holdingState(poseIndex = 2, secondsRemaining = 28, totalSeconds = 45, progress = 0.38f),
                selectedDifficulty = com.inspiredandroid.yogabase.data.Difficulty.INTERMEDIATE,
                onDifficultyChange = {},
                onSkipIntro = {},
                onTogglePause = {},
                onToggleVoice = {},
                onBack = {},
                onFinished = {},
            )
        }
    }

    @Test
    fun practiceHoldingMirrored() {
        snap {
            PracticeScreen(
                category = practiceCategory,
                poses = practicePoses,
                state = holdingState(
                    poseIndex = 5,
                    secondsRemaining = 18,
                    totalSeconds = 45,
                    sideSwitched = true,
                    progress = 0.62f,
                ),
                selectedDifficulty = com.inspiredandroid.yogabase.data.Difficulty.INTERMEDIATE,
                onDifficultyChange = {},
                onSkipIntro = {},
                onTogglePause = {},
                onToggleVoice = {},
                onBack = {},
                onFinished = {},
            )
        }
    }

    @Test
    fun finish() {
        snap {
            FinishScreen(
                category = practiceCategory,
                streak = 6,
                xpGained = 50,
                totalXpAfter = 800,
                onRestart = {},
                onBackToMenu = {},
            )
        }
    }
}

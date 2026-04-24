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
import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.ui.screens.FinishScreen
import com.inspiredandroid.yogabase.ui.screens.MainMenuScreen
import com.inspiredandroid.yogabase.ui.screens.PracticeScreen
import com.inspiredandroid.yogabase.ui.theme.YogaBaseTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale

@RunWith(Parameterized::class)
class TabletStoreScreenshotTest(
    private val locale: String,
    private val playStoreLocale: String,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}")
        fun locales() = listOf(
            arrayOf("en", "en-US"),
        )
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_TABLET.copy(softButtons = false),
        showSystemUi = true,
        useDeviceResolution = true,
    )

    private lateinit var originalLocale: Locale

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale(locale))

        paparazzi.unsafeUpdateConfig(
            deviceConfig = DeviceConfig.PIXEL_TABLET.copy(
                softButtons = false,
                locale = locale,
            ),
        )
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    private fun snap(
        name: String,
        content: @Composable () -> Unit,
    ) {
        paparazzi.unsafeUpdateConfig(theme = "android:Theme.Material.Light.NoActionBar")
        paparazzi.snapshot(name = "tablet_${playStoreLocale}_$name") {
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
        snap("01") {
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
        snap("02") {
            PracticeScreen(
                category = practiceCategory,
                poses = practicePoses,
                state = introState(),
                selectedDifficulty = Difficulty.INTERMEDIATE,
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
        snap("03") {
            PracticeScreen(
                category = practiceCategory,
                poses = practicePoses,
                state = holdingState(poseIndex = 2, secondsRemaining = 28, totalSeconds = 45, progress = 0.38f),
                selectedDifficulty = Difficulty.INTERMEDIATE,
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
        snap("04") {
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

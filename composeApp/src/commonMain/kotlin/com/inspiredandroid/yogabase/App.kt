@file:OptIn(ExperimentalComposeUiApi::class)

package com.inspiredandroid.yogabase

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.inspiredandroid.yogabase.audio.rememberAudioPlayer
import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.YogaRepository
import com.inspiredandroid.yogabase.navigation.AppNavHost
import com.inspiredandroid.yogabase.navigation.Finish
import com.inspiredandroid.yogabase.navigation.MainMenu
import com.inspiredandroid.yogabase.navigation.Practice
import com.inspiredandroid.yogabase.session.SessionController
import com.inspiredandroid.yogabase.storage.UserStorage
import com.inspiredandroid.yogabase.ui.screens.FinishScreen
import com.inspiredandroid.yogabase.ui.screens.MainMenuScreen
import com.inspiredandroid.yogabase.ui.screens.PracticeScreen
import com.inspiredandroid.yogabase.ui.theme.DarkColorScheme
import com.inspiredandroid.yogabase.ui.theme.LightColorScheme
import com.inspiredandroid.yogabase.ui.theme.YogaBaseTheme

@Composable
fun App(colorScheme: ColorScheme? = null) {
    val resolvedScheme = colorScheme
        ?: if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    val repository = remember { YogaRepository() }
    val storage = remember { UserStorage() }
    val audioPlayer = rememberAudioPlayer()
    val navController = rememberNavController()
    val controller = remember(navController) {
        SessionController(repository, storage, audioPlayer, navController)
    }

    DisposableEffect(controller) {
        onDispose { controller.dispose() }
    }

    var loaded by rememberSaveable { mutableStateOf(false) }
    var streak by remember { mutableStateOf(0) }
    var totalXp by remember { mutableStateOf(0) }
    var completedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedDifficulty by rememberSaveable { mutableStateOf(Difficulty.INTERMEDIATE) }
    var lastXpGained by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        repository.loadAll()
        streak = storage.refreshStreakOnLaunch()
        totalXp = storage.getTotalXp()
        completedIds = storage.completedCategoryIds()
        loaded = true
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    val state = controller.state.value
                    if (state != null && !state.paused) controller.togglePause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    YogaBaseTheme(colorScheme = resolvedScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                if (!loaded) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    return@Box
                }

                AppNavHost(
                    navController = navController,
                    startDestination = MainMenu,
                ) {
                    composable<MainMenu> {
                        MainMenuScreen(
                            streak = streak,
                            totalXp = totalXp,
                            categories = repository.categories(),
                            poses = repository.poses(),
                            completedCategoryIds = completedIds,
                            onCategoryClick = { category ->
                                controller.startSession(category, selectedDifficulty)
                                navController.navigate(Practice(category.id))
                            },
                        )
                    }

                    composable<Practice> { backStackEntry ->
                        val route: Practice = backStackEntry.toRoute()
                        val category = repository.categoryById(route.categoryId) ?: return@composable
                        val poses = repository.posesForCategory(category)
                        val state by controller.state.collectAsState()

                        PracticeScreen(
                            category = category,
                            poses = poses,
                            state = state,
                            selectedDifficulty = selectedDifficulty,
                            onDifficultyChange = {
                                selectedDifficulty = it
                                controller.setDifficulty(it)
                            },
                            onSkipIntro = { controller.skipIntro() },
                            onTogglePause = { controller.togglePause() },
                            onToggleVoice = { controller.toggleVoice() },
                            onBack = {
                                controller.cancel()
                                navController.popBackStack()
                            },
                            onFinished = {
                                // SessionController handles navigation; refresh local state here.
                                val newTotalXp = storage.getTotalXp()
                                lastXpGained = (newTotalXp - totalXp).coerceAtLeast(0)
                                streak = storage.getStreak()
                                totalXp = newTotalXp
                                completedIds = storage.completedCategoryIds()
                            },
                        )
                    }

                    composable<Finish> { backStackEntry ->
                        val route: Finish = backStackEntry.toRoute()
                        val category = repository.categoryById(route.categoryId) ?: return@composable
                        FinishScreen(
                            category = category,
                            streak = streak,
                            xpGained = lastXpGained,
                            totalXpAfter = totalXp,
                            onRestart = {
                                controller.startSession(category, selectedDifficulty)
                                navController.navigate(Practice(category.id)) {
                                    popUpTo(MainMenu) { inclusive = false }
                                }
                            },
                            onBackToMenu = {
                                controller.cancel()
                                navController.popBackStack(MainMenu, inclusive = false)
                            },
                        )
                    }
                }
            }
        }
    }
}

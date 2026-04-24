package com.inspiredandroid.yogabase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.SessionCategory
import com.inspiredandroid.yogabase.storage.UserStorage
import com.inspiredandroid.yogabase.ui.components.LevelUpBanner
import com.inspiredandroid.yogabase.ui.components.XpGainedChip
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.back_to_menu
import yogabase.composeapp.generated.resources.great_job
import yogabase.composeapp.generated.resources.restart_session
import yogabase.composeapp.generated.resources.streak_days
import yogabase.composeapp.generated.resources.streak_header

@Composable
fun FinishScreen(
    category: SessionCategory,
    streak: Int,
    xpGained: Int,
    totalXpAfter: Int,
    onRestart: () -> Unit,
    onBackToMenu: () -> Unit,
) {
    val levelAfter = UserStorage.levelForXp(totalXpAfter)
    val levelBefore = UserStorage.levelForXp(totalXpAfter - xpGained)
    val levelChange = if (levelAfter > levelBefore) {
        UserStorage.LevelChange(
            oldLevel = levelBefore,
            newLevel = levelAfter,
            totalXpBefore = totalXpAfter - xpGained,
            totalXpAfter = totalXpAfter,
        )
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.great_job),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = category.categoryName,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.streak_header),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = pluralStringResource(Res.plurals.streak_days, streak, streak),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (xpGained > 0) {
            Spacer(Modifier.height(16.dp))
            XpGainedChip(xpGained = xpGained)
        }
        if (levelChange != null) {
            Spacer(Modifier.height(16.dp))
            LevelUpBanner(levelChange = levelChange)
        }
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.restart_session))
        }
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(
            onClick = onBackToMenu,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.back_to_menu))
        }
    }
}

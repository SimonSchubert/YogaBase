package com.inspiredandroid.yogabase.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.storage.UserStorage
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.level_label
import yogabase.composeapp.generated.resources.level_up_title
import yogabase.composeapp.generated.resources.xp_gained

@Composable
fun XpGainedChip(
    xpGained: Int,
    modifier: Modifier = Modifier,
) {
    if (xpGained <= 0) return
    val inInspection = LocalInspectionMode.current
    val alpha = remember { Animatable(if (inInspection) 1f else 0f) }
    val offset = remember { Animatable(if (inInspection) 0f else 12f) }
    LaunchedEffect(xpGained) {
        if (!inInspection) {
            alpha.animateTo(1f, tween(400, easing = LinearOutSlowInEasing))
        }
    }
    LaunchedEffect(xpGained) {
        if (!inInspection) {
            offset.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        }
    }

    Card(
        modifier = modifier
            .alpha(alpha.value)
            .offset(y = offset.value.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.xp_gained, xpGained),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun LevelUpBanner(
    levelChange: UserStorage.LevelChange,
    modifier: Modifier = Modifier,
) {
    val inInspection = LocalInspectionMode.current
    val scale = remember { Animatable(if (inInspection) 1f else 0.6f) }
    val alpha = remember { Animatable(if (inInspection) 1f else 0f) }
    LaunchedEffect(levelChange) {
        if (!inInspection) {
            alpha.animateTo(1f, tween(250))
        }
    }
    LaunchedEffect(levelChange) {
        if (!inInspection) {
            scale.animateTo(1.15f, tween(260, easing = FastOutSlowInEasing))
            scale.animateTo(1f, tween(220, easing = FastOutSlowInEasing))
        }
    }

    val titleRes = UserStorage.currentTitleRes(levelChange.newLevel)
    val showMilestoneTitle = levelChange.isMilestone

    Card(
        modifier = modifier
            .alpha(alpha.value)
            .scale(scale.value),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.level_up_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.level_label, levelChange.newLevel),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            if (showMilestoneTitle) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

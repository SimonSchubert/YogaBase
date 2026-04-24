package com.inspiredandroid.yogabase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.Difficulty
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.difficulty_balanced
import yogabase.composeapp.generated.resources.difficulty_endurance
import yogabase.composeapp.generated.resources.difficulty_swift

@Composable
fun DifficultySelector(
    selected: Difficulty,
    onSelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DiffButton(Difficulty.BEGINNER, stringResource(Res.string.difficulty_swift), selected, onSelected, Modifier.weight(1f))
        DiffButton(Difficulty.INTERMEDIATE, stringResource(Res.string.difficulty_balanced), selected, onSelected, Modifier.weight(1f))
        DiffButton(Difficulty.ADVANCED, stringResource(Res.string.difficulty_endurance), selected, onSelected, Modifier.weight(1f))
    }
}

@Composable
private fun DiffButton(
    target: Difficulty,
    label: String,
    selected: Difficulty,
    onSelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = selected == target
    FilledTonalButton(
        onClick = { onSelected(target) },
        modifier = modifier,
        colors = if (isSelected) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            ButtonDefaults.filledTonalButtonColors()
        },
    ) {
        Text(label)
    }
}

package com.inspiredandroid.yogabase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.BreathingTechnique
import com.inspiredandroid.yogabase.ui.components.BackIcon
import com.inspiredandroid.yogabase.ui.components.breathing.BreathingTechniqueCard
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.back
import yogabase.composeapp.generated.resources.breathing_menu_subtitle
import yogabase.composeapp.generated.resources.breathing_menu_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingMenuScreen(
    techniques: List<BreathingTechnique>,
    onTechniqueClick: (BreathingTechnique) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            TopAppBar(
                title = { Text(stringResource(Res.string.breathing_menu_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = BackIcon,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
            )
        }

        item {
            Text(
                text = stringResource(Res.string.breathing_menu_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        items(techniques, key = { it.id }) { technique ->
            BreathingTechniqueCard(
                technique = technique,
                onClick = { onTechniqueClick(technique) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

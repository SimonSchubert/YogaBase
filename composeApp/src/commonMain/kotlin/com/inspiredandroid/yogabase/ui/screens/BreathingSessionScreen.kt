package com.inspiredandroid.yogabase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.breathing.BreathingPhase
import com.inspiredandroid.yogabase.breathing.BreathingUiState
import com.inspiredandroid.yogabase.data.BreathingStep
import com.inspiredandroid.yogabase.data.BreathingTechnique
import com.inspiredandroid.yogabase.ui.components.BackIcon
import com.inspiredandroid.yogabase.ui.components.breathing.BreathingCircle
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.back
import yogabase.composeapp.generated.resources.breathing_complete
import yogabase.composeapp.generated.resources.breathing_complete_message
import yogabase.composeapp.generated.resources.breathing_cycle_progress
import yogabase.composeapp.generated.resources.breathing_done
import yogabase.composeapp.generated.resources.breathing_step_exhale
import yogabase.composeapp.generated.resources.breathing_step_hold
import yogabase.composeapp.generated.resources.breathing_step_hold_out
import yogabase.composeapp.generated.resources.breathing_step_inhale
import yogabase.composeapp.generated.resources.breathing_step_top_off
import yogabase.composeapp.generated.resources.cancel
import yogabase.composeapp.generated.resources.pause
import yogabase.composeapp.generated.resources.quit
import yogabase.composeapp.generated.resources.quit_breathing_message
import yogabase.composeapp.generated.resources.quit_breathing_title
import yogabase.composeapp.generated.resources.resume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingSessionScreen(
    technique: BreathingTechnique,
    state: BreathingUiState?,
    onTogglePause: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val phase = state?.phase ?: BreathingPhase.Idle
    val paused = state?.paused ?: false
    var showQuitDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = technique.name,
                    maxLines = 1,
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (phase is BreathingPhase.Finished) onBack() else showQuitDialog = true
                }) {
                    Icon(
                        imageVector = BackIcon,
                        contentDescription = stringResource(Res.string.back),
                    )
                }
            },
        )

        when (phase) {
            BreathingPhase.Finished -> FinishedContent(onBack = onBack)
            else -> ActiveContent(
                technique = technique,
                phase = phase,
                paused = paused,
                onTogglePause = onTogglePause,
            )
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text(stringResource(Res.string.quit_breathing_title)) },
            text = { Text(stringResource(Res.string.quit_breathing_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    onBack()
                }) {
                    Text(stringResource(Res.string.quit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ActiveContent(
    technique: BreathingTechnique,
    phase: BreathingPhase,
    paused: Boolean,
    onTogglePause: () -> Unit,
) {
    val activePhase = phase as? BreathingPhase.Active
    val sessionProgress = sessionProgress(technique, phase)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(
            progress = { sessionProgress },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))

        if (activePhase != null) {
            Text(
                text = stringResource(
                    Res.string.breathing_cycle_progress,
                    activePhase.cycleIndex,
                    activePhase.totalCycles,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stepLabel(activePhase?.step, technique.id),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        if (activePhase != null) {
            Text(
                text = activePhase.secondsRemaining.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            BreathingCircle(phase = phase)
        }

        Spacer(Modifier.weight(1f))

        FilledTonalIconButton(
            onClick = onTogglePause,
            modifier = Modifier.size(64.dp),
        ) {
            Icon(
                imageVector = if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = stringResource(
                    if (paused) Res.string.resume else Res.string.pause,
                ),
                modifier = Modifier.size(32.dp),
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun FinishedContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.breathing_complete),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.breathing_complete_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onBack) {
            Text(stringResource(Res.string.breathing_done))
        }
    }
}

@Composable
private fun stepLabel(step: BreathingStep?, techniqueId: String): String = when (step) {
    BreathingStep.Inhale -> stringResource(Res.string.breathing_step_inhale)
    BreathingStep.HoldIn -> {
        if (techniqueId == "physiological_sigh") {
            stringResource(Res.string.breathing_step_top_off)
        } else {
            stringResource(Res.string.breathing_step_hold)
        }
    }
    BreathingStep.Exhale -> stringResource(Res.string.breathing_step_exhale)
    BreathingStep.HoldOut -> stringResource(Res.string.breathing_step_hold_out)
    null -> ""
}

private fun sessionProgress(technique: BreathingTechnique, phase: BreathingPhase): Float {
    if (phase !is BreathingPhase.Active) return 0f
    val completedCycles = phase.cycleIndex - 1
    val steps = technique.steps()
    val stepIndex = steps.indexOfFirst { it.step == phase.step }.coerceAtLeast(0)
    val stepOffset = steps.take(stepIndex).sumOf { it.seconds }
    val withinCycle = stepOffset + phase.stepProgress * phase.secondsTotal
    val cycleFraction = withinCycle / technique.cycleDurationSeconds.coerceAtLeast(1)
    return ((completedCycles + cycleFraction) / phase.totalCycles).coerceIn(0f, 1f)
}

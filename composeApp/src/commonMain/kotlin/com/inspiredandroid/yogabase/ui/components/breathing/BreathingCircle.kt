package com.inspiredandroid.yogabase.ui.components.breathing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.breathing.BreathingPhase
import com.inspiredandroid.yogabase.data.BreathingStep

private const val MIN_SCALE = 0.35f
private const val MAX_SCALE = 1f

@Composable
fun BreathingCircle(
    phase: BreathingPhase,
    modifier: Modifier = Modifier,
) {
    val scale = when (phase) {
        is BreathingPhase.Active -> scaleForStep(phase.step, phase.stepProgress)
        BreathingPhase.Finished -> MIN_SCALE
        BreathingPhase.Idle -> MIN_SCALE
    }

    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer
    val onContainer = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)

    Canvas(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .aspectRatio(1f),
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.minDimension / 2f * 0.85f
        val radius = baseRadius * scale

        drawCircle(
            color = onContainer,
            radius = baseRadius,
            center = center,
        )

        drawCircle(
            color = container,
            radius = radius,
            center = center,
        )

        drawCircle(
            color = primary,
            radius = radius,
            center = center,
            style = Stroke(width = 3.dp.toPx()),
        )
    }
}

private fun scaleForStep(step: BreathingStep, progress: Float): Float {
    val fraction = progress.coerceIn(0f, 1f)
    return when (step) {
        BreathingStep.Inhale -> lerp(MIN_SCALE, MAX_SCALE, fraction)
        BreathingStep.HoldIn -> MAX_SCALE
        BreathingStep.Exhale -> lerp(MAX_SCALE, MIN_SCALE, fraction)
        BreathingStep.HoldOut -> MIN_SCALE
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float = start + (end - start) * fraction

package com.inspiredandroid.yogabase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.Pose
import com.inspiredandroid.yogabase.data.SessionCategory
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.difficulty_advanced
import yogabase.composeapp.generated.resources.difficulty_beginner
import yogabase.composeapp.generated.resources.difficulty_intermediate
import yogabase.composeapp.generated.resources.pose_count

@Composable
fun CategoryCard(
    category: SessionCategory,
    poses: List<Pose>,
    completed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val difficulty = Difficulty.fromString(category.difficulty)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DifficultyBadge(difficulty)
                if (completed) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = category.categoryDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                poses.take(5).forEachIndexed { index, pose ->
                    if (index > 0) Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    ) {
                        PoseImage(
                            poseId = pose.id,
                            modifier = Modifier.size(48.dp),
                            contentDescription = pose.englishName,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = pluralStringResource(Res.plurals.pose_count, poses.size, poses.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: Difficulty) {
    val (label, color) = when (difficulty) {
        Difficulty.BEGINNER -> stringResource(Res.string.difficulty_beginner) to MaterialTheme.colorScheme.primary
        Difficulty.INTERMEDIATE -> stringResource(Res.string.difficulty_intermediate) to MaterialTheme.colorScheme.secondary
        Difficulty.ADVANCED -> stringResource(Res.string.difficulty_advanced) to MaterialTheme.colorScheme.error
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

package com.inspiredandroid.yogabase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.Difficulty
import com.inspiredandroid.yogabase.data.Pose
import com.inspiredandroid.yogabase.data.SessionCategory
import com.inspiredandroid.yogabase.ui.components.CategoryCard
import com.inspiredandroid.yogabase.ui.components.PlayerLevelCard
import com.inspiredandroid.yogabase.ui.components.PoseCard
import com.inspiredandroid.yogabase.ui.components.StreakHeader
import org.jetbrains.compose.resources.stringResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.section_all_poses
import yogabase.composeapp.generated.resources.section_sessions

@Composable
fun MainMenuScreen(
    streak: Int,
    totalXp: Int,
    categories: List<SessionCategory>,
    poses: List<Pose>,
    completedCategoryIds: Set<Int>,
    onCategoryClick: (SessionCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val posesById = poses.associateBy { it.id }
    val sortedCategories = categories.sortedBy { Difficulty.fromString(it.difficulty).ordinal }
    val sortedPoses = poses.sortedBy { Difficulty.fromString(it.difficulty).ordinal }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (streak > 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                StreakHeader(streak = streak)
            }
        }

        if (totalXp > 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PlayerLevelCard(totalXp = totalXp)
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(Res.string.section_sessions),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        items(sortedCategories, key = { it.id }) { category ->
            val categoryPoses = category.poses.mapNotNull { posesById[it] }
            CategoryCard(
                category = category,
                poses = categoryPoses,
                completed = completedCategoryIds.contains(category.id),
                onClick = { onCategoryClick(category) },
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.section_all_poses),
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .height(360.dp)
                    .layout { measurable, constraints ->
                        val inset = 16.dp.roundToPx()
                        val placeable = measurable.measure(
                            constraints.copy(
                                minWidth = constraints.minWidth + inset * 2,
                                maxWidth = constraints.maxWidth + inset * 2,
                            ),
                        )
                        layout(constraints.maxWidth, placeable.height) {
                            placeable.place(-inset, 0)
                        }
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(sortedPoses, key = { it.id }) { pose ->
                    PoseCard(
                        pose = pose,
                        modifier = Modifier.width(150.dp),
                    )
                }
            }
        }
    }
}

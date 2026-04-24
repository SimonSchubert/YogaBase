package com.inspiredandroid.yogabase.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.inspiredandroid.yogabase.data.Pose

@Composable
fun PosePreviewStrip(
    poses: List<Pose>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    val listState = rememberLazyListState()
    LaunchedEffect(currentIndex) {
        if (currentIndex in poses.indices) {
            listState.animateScrollToItem(currentIndex)
        }
    }
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(poses, key = { _, pose -> pose.id }) { index, pose ->
            val isActive = index == currentIndex
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (isActive) {
                            Modifier.border(
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                RoundedCornerShape(8.dp),
                            )
                        } else {
                            Modifier
                        },
                    ),
            ) {
                PoseImage(
                    poseId = pose.id,
                    modifier = Modifier.size(56.dp),
                    contentDescription = pose.englishName,
                )
            }
        }
    }
}

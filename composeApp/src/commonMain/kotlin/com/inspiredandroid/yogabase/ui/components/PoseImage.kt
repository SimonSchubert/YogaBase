package com.inspiredandroid.yogabase.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import yogabase.composeapp.generated.resources.Res
import yogabase.composeapp.generated.resources.pose_1
import yogabase.composeapp.generated.resources.pose_10
import yogabase.composeapp.generated.resources.pose_11
import yogabase.composeapp.generated.resources.pose_12
import yogabase.composeapp.generated.resources.pose_13
import yogabase.composeapp.generated.resources.pose_14
import yogabase.composeapp.generated.resources.pose_15
import yogabase.composeapp.generated.resources.pose_16
import yogabase.composeapp.generated.resources.pose_17
import yogabase.composeapp.generated.resources.pose_18
import yogabase.composeapp.generated.resources.pose_19
import yogabase.composeapp.generated.resources.pose_2
import yogabase.composeapp.generated.resources.pose_20
import yogabase.composeapp.generated.resources.pose_21
import yogabase.composeapp.generated.resources.pose_22
import yogabase.composeapp.generated.resources.pose_23
import yogabase.composeapp.generated.resources.pose_24
import yogabase.composeapp.generated.resources.pose_25
import yogabase.composeapp.generated.resources.pose_26
import yogabase.composeapp.generated.resources.pose_27
import yogabase.composeapp.generated.resources.pose_28
import yogabase.composeapp.generated.resources.pose_29
import yogabase.composeapp.generated.resources.pose_3
import yogabase.composeapp.generated.resources.pose_30
import yogabase.composeapp.generated.resources.pose_31
import yogabase.composeapp.generated.resources.pose_32
import yogabase.composeapp.generated.resources.pose_33
import yogabase.composeapp.generated.resources.pose_34
import yogabase.composeapp.generated.resources.pose_35
import yogabase.composeapp.generated.resources.pose_36
import yogabase.composeapp.generated.resources.pose_37
import yogabase.composeapp.generated.resources.pose_38
import yogabase.composeapp.generated.resources.pose_39
import yogabase.composeapp.generated.resources.pose_4
import yogabase.composeapp.generated.resources.pose_40
import yogabase.composeapp.generated.resources.pose_41
import yogabase.composeapp.generated.resources.pose_42
import yogabase.composeapp.generated.resources.pose_43
import yogabase.composeapp.generated.resources.pose_44
import yogabase.composeapp.generated.resources.pose_45
import yogabase.composeapp.generated.resources.pose_46
import yogabase.composeapp.generated.resources.pose_47
import yogabase.composeapp.generated.resources.pose_48
import yogabase.composeapp.generated.resources.pose_5
import yogabase.composeapp.generated.resources.pose_6
import yogabase.composeapp.generated.resources.pose_7
import yogabase.composeapp.generated.resources.pose_8
import yogabase.composeapp.generated.resources.pose_9

fun poseDrawable(id: Int): DrawableResource = when (id) {
    1 -> Res.drawable.pose_1
    2 -> Res.drawable.pose_2
    3 -> Res.drawable.pose_3
    4 -> Res.drawable.pose_4
    5 -> Res.drawable.pose_5
    6 -> Res.drawable.pose_6
    7 -> Res.drawable.pose_7
    8 -> Res.drawable.pose_8
    9 -> Res.drawable.pose_9
    10 -> Res.drawable.pose_10
    11 -> Res.drawable.pose_11
    12 -> Res.drawable.pose_12
    13 -> Res.drawable.pose_13
    14 -> Res.drawable.pose_14
    15 -> Res.drawable.pose_15
    16 -> Res.drawable.pose_16
    17 -> Res.drawable.pose_17
    18 -> Res.drawable.pose_18
    19 -> Res.drawable.pose_19
    20 -> Res.drawable.pose_20
    21 -> Res.drawable.pose_21
    22 -> Res.drawable.pose_22
    23 -> Res.drawable.pose_23
    24 -> Res.drawable.pose_24
    25 -> Res.drawable.pose_25
    26 -> Res.drawable.pose_26
    27 -> Res.drawable.pose_27
    28 -> Res.drawable.pose_28
    29 -> Res.drawable.pose_29
    30 -> Res.drawable.pose_30
    31 -> Res.drawable.pose_31
    32 -> Res.drawable.pose_32
    33 -> Res.drawable.pose_33
    34 -> Res.drawable.pose_34
    35 -> Res.drawable.pose_35
    36 -> Res.drawable.pose_36
    37 -> Res.drawable.pose_37
    38 -> Res.drawable.pose_38
    39 -> Res.drawable.pose_39
    40 -> Res.drawable.pose_40
    41 -> Res.drawable.pose_41
    42 -> Res.drawable.pose_42
    43 -> Res.drawable.pose_43
    44 -> Res.drawable.pose_44
    45 -> Res.drawable.pose_45
    46 -> Res.drawable.pose_46
    47 -> Res.drawable.pose_47
    48 -> Res.drawable.pose_48
    else -> Res.drawable.pose_1
}

@Composable
fun PoseImage(
    poseId: Int,
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    contentDescription: String? = null,
) {
    val scaleX by animateFloatAsState(
        targetValue = if (mirrored) -1f else 1f,
        animationSpec = tween(500),
        label = "pose-mirror",
    )
    Image(
        painter = painterResource(poseDrawable(poseId)),
        contentDescription = contentDescription,
        modifier = modifier.graphicsLayer(scaleX = scaleX),
    )
}

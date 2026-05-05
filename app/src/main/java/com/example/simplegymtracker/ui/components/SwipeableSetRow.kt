package com.example.simplegymtracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.simplegymtracker.ui.theme.SemanticError
import com.example.simplegymtracker.ui.theme.SemanticSuccess
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 0.3f

@Composable
fun SwipeableSetRow(
    onSwipeComplete: () -> Unit,
    onSwipeDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable (isComplete: Boolean, animateComplete: () -> Unit) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isComplete by remember { mutableStateOf(false) }
    var animateTrigger by remember { mutableStateOf(0) }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "swipeOffset"
    )

    val scale by animateFloatAsState(
        targetValue = if (animateTrigger > 0) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "scale"
    )

    val completeColor by animateColorAsState(
        targetValue = if (offsetX > 0) SemanticSuccess else Color.Transparent,
        label = "completeColor"
    )

    val deleteColor by animateColorAsState(
        targetValue = if (offsetX < 0) SemanticError else Color.Transparent,
        label = "deleteColor"
    )

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(completeColor),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .scale(if (offsetX > 0) scale else 1f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(deleteColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .scale(if (offsetX < 0) scale else 1f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .pointerInput(enabled) {
                    if (enabled) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > SWIPE_THRESHOLD -> {
                                        isComplete = true
                                        animateTrigger++
                                        onSwipeComplete()
                                    }
                                    offsetX < -SWIPE_THRESHOLD -> {
                                        onSwipeDelete()
                                    }
                                    else -> offsetX = 0f
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX = (offsetX + dragAmount / 1000f).coerceIn(-0.8f, 0.8f)
                            }
                        )
                    }
                }
                .padding(horizontal = 1.dp)
        ) {
            content(isComplete) {
                animateTrigger++
            }
        }
    }
}
package com.example.simplegymtracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplegymtracker.ui.theme.ElectricBlue
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NumberPicker(
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    steps: Float = 0.5f,
    label: String = "",
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val itemHeightPx = with(density) { 48.dp.toPx() }
    val visibleItems = 5

    var rawOffset by remember(value) {
        val stepsInRange = ((value - range.start) / steps).roundToInt()
        mutableFloatStateOf(-stepsInRange * itemHeightPx)
    }

    val animatedOffset by animateFloatAsState(
        targetValue = rawOffset,
        animationSpec = tween(durationMillis = 100),
        label = "offset"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(48.dp * visibleItems)
                .pointerInput(range, steps) {
                    detectVerticalDragGestures(
                        onDragEnd = { },
                        onVerticalDrag = { _, dragAmount ->
                            rawOffset += dragAmount
                            val maxOffset = 0f
                            val minOffset = -(range.endInclusive - range.start) / steps * itemHeightPx
                            rawOffset = rawOffset.coerceIn(minOffset, maxOffset)

                            val stepsOffset = (rawOffset / itemHeightPx).roundToInt()
                            val newValue = (range.start + (-stepsOffset) * steps).coerceIn(range.start, range.endInclusive)
                            if (newValue != value) {
                                onValueChange(newValue)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val stepsCount = ((range.endInclusive - range.start) / steps).roundToInt() + 1
            val centerIndex = stepsCount / 2

            for (i in -visibleItems..visibleItems) {
                val stepsOffset = (animatedOffset / itemHeightPx).roundToInt()
                val itemIndex = centerIndex + stepsOffset + i

                if (itemIndex in 0 until stepsCount) {
                    val itemValue = range.start + itemIndex * steps
                    val distanceFromCenter = abs(i)
                    val alpha = when {
                        distanceFromCenter == 0 -> 1f
                        distanceFromCenter == 1 -> 0.6f
                        else -> 0.3f
                    }
                    val scale = when {
                        distanceFromCenter == 0 -> 1.2f
                        distanceFromCenter == 1 -> 1.0f
                        else -> 0.8f
                    }
                    val yOffset = (animatedOffset.roundToInt() % itemHeightPx.roundToInt()) + i * itemHeightPx.roundToInt()

                    Text(
                        text = if (steps >= 1) {
                            itemValue.roundToInt().toString()
                        } else {
                            String.format("%.1f", itemValue)
                        },
                        fontSize = (20 * scale).sp,
                        fontWeight = if (distanceFromCenter == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (distanceFromCenter == 0) ElectricBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset { IntOffset(0, yOffset) }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        Text(
            text = if (steps >= 1) "reps" else "kg",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun IntNumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String = "",
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val itemHeightPx = with(density) { 48.dp.toPx() }
    val visibleItems = 5

    var rawOffset by remember(value) {
        mutableFloatStateOf(-(value - range.first) * itemHeightPx)
    }

    val animatedOffset by animateFloatAsState(
        targetValue = rawOffset,
        animationSpec = tween(durationMillis = 100),
        label = "offset"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(48.dp * visibleItems)
                .pointerInput(range) {
                    detectVerticalDragGestures(
                        onDragEnd = { },
                        onVerticalDrag = { _, dragAmount ->
                            rawOffset += dragAmount
                            val maxOffset = 0f
                            val minOffset = -(range.last - range.first) * itemHeightPx
                            rawOffset = rawOffset.coerceIn(minOffset, maxOffset)

                            val stepsOffset = (rawOffset / itemHeightPx).roundToInt()
                            val newValue = (range.first + (-stepsOffset)).coerceIn(range.first, range.last)
                            if (newValue != value) {
                                onValueChange(newValue)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val rangeSize = range.last - range.first + 1
            val centerIndex = rangeSize / 2

            for (i in -visibleItems..visibleItems) {
                val stepsOffset = (animatedOffset / itemHeightPx).roundToInt()
                val itemIndex = centerIndex + stepsOffset + i

                if (itemIndex in 0 until rangeSize) {
                    val itemValue = range.first + itemIndex
                    val distanceFromCenter = abs(i)
                    val alpha = when {
                        distanceFromCenter == 0 -> 1f
                        distanceFromCenter == 1 -> 0.6f
                        else -> 0.3f
                    }
                    val scale = when {
                        distanceFromCenter == 0 -> 1.2f
                        distanceFromCenter == 1 -> 1.0f
                        else -> 0.8f
                    }
                    val yOffset = (animatedOffset.roundToInt() % itemHeightPx.roundToInt()) + i * itemHeightPx.roundToInt()

                    Text(
                        text = itemValue.toString(),
                        fontSize = (20 * scale).sp,
                        fontWeight = if (distanceFromCenter == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (distanceFromCenter == 0) ElectricBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset { IntOffset(0, yOffset) }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
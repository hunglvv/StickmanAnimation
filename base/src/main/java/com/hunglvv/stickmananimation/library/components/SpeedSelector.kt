package com.hunglvv.stickmananimation.library.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val BarWidth = 1.dp
private val selectedHeight = 22.dp
private val normalHeight = 10.dp
private val evenHeight = 16.dp
private const val MinAlpha = .25f

@Stable
interface MagnifySliderState {
    val currentValue: Float
    val range: ClosedRange<Int>

    suspend fun snapTo(value: Float)
    suspend fun decayTo(velocity: Float, value: Float)
    suspend fun stop()
}

class MagnifySliderStateImpl(
    currentValue: Float,
    override val range: ClosedRange<Int>,
) : MagnifySliderState {
    private val floatRange = range.start.toFloat()..range.endInclusive.toFloat()
    private val animatable = Animatable(currentValue)
    private val decayAnimationSpec = FloatTweenSpec(
        200, 0, LinearEasing
    )

    override val currentValue: Float
        get() = animatable.value

    override suspend fun stop() {
        animatable.stop()
    }

    override suspend fun snapTo(value: Float) {
        animatable.snapTo(value.coerceIn(floatRange))
    }

    override suspend fun decayTo(velocity: Float, value: Float) {
        val target = value.roundToInt().coerceIn(range).toFloat()
        animatable.animateTo(
            targetValue = target,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MagnifySliderStateImpl

        if (range != other.range) return false
        if (floatRange != other.floatRange) return false
        if (animatable != other.animatable) return false
        return decayAnimationSpec == other.decayAnimationSpec
    }

    override fun hashCode(): Int {
        var result = range.hashCode()
        result = 31 * result + floatRange.hashCode()
        result = 31 * result + animatable.hashCode()
        result = 31 * result + decayAnimationSpec.hashCode()
        return result
    }

    companion object {
        val Saver = Saver<MagnifySliderStateImpl, List<Any>>(
            save = { listOf(it.currentValue, it.range.start, it.range.endInclusive) },
            restore = {
                MagnifySliderStateImpl(
                    currentValue = it[0] as Float,
                    range = (it[1] as Int)..(it[2] as Int)
                )
            }
        )
    }
}

@Composable
fun rememberMagnifySliderState(
    currentValue: Float = 10f,
    range: ClosedRange<Int> = 10..500,
): MagnifySliderState {
    val state = rememberSaveable(saver = MagnifySliderStateImpl.Saver) {
        MagnifySliderStateImpl(currentValue, range)
    }
    LaunchedEffect(key1 = Unit) {
        state.snapTo(state.currentValue.roundToInt().toFloat())
    }
    return state
}

@Composable
fun MagnifySlider(
    modifier: Modifier = Modifier,
    state: MagnifySliderState = rememberMagnifySliderState(),
    numSegments: Int = 40,
    barColor: Color = Color.White,
    selectedColor: Color,
    polygon: @Composable () -> Unit,
    currentValueLabel: @Composable (Int) -> Unit = { value -> Text(value.toString()) },
    indicatorLabel: @Composable (Int) -> Unit = { value -> Text(value.toString()) },
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = modifier.drag(
            state,
            numSegments
        ) { onValueChange(state.currentValue.roundToInt()) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        currentValueLabel(state.currentValue.roundToInt())
        Spacer(modifier = Modifier.size(6.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            val segmentWidth = maxWidth / numSegments
            val segmentWidthPx = constraints.maxWidth.toFloat() / numSegments.toFloat()
//            val halfSegments = (numSegments + 1) / 2
//            val start = (state.currentValue - halfSegments).toInt()
//                .coerceAtLeast(state.range.start)
//            val end = (state.currentValue + halfSegments).toInt()
//                .coerceAtMost(state.range.endInclusive)
//            val maxOffset = constraints.maxWidth / 2f
            for (i in state.range.start..state.range.endInclusive) {
                val offsetX = (i - state.currentValue) * segmentWidthPx
                // indicator at center is at 1f, indicators at edges are at 0.25f
//                val alpha = 1f - (1f - MinAlpha) * (offsetX / maxOffset).absoluteValue
                Column(
                    modifier = Modifier
                        .width(segmentWidth)
                        .graphicsLayer(
//                            alpha = alpha,
                            translationX = offsetX
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(BarWidth)
                            .height(if (i % 10 == 0) evenHeight else normalHeight)
                            .background(barColor)
                    )
                    indicatorLabel(i)
                }
            }
            Box(
                modifier = Modifier
                    .width(BarWidth)
                    .height(selectedHeight)
                    .background(selectedColor)
            )
        }
        polygon()
    }
}

private fun Modifier.drag(
    state: MagnifySliderState,
    numSegments: Int,
    onValueChange: () -> Unit
) = pointerInput(Unit) {
    val decay = splineBasedDecay<Float>(this)
    val segmentWidthPx = size.width / numSegments
    coroutineScope {
        while (true) {
            val pointerId = awaitPointerEventScope { awaitFirstDown().id }
            state.stop()
            val tracker = VelocityTracker()
            awaitPointerEventScope {
                horizontalDrag(pointerId) { change ->
                    val horizontalDragOffset =
                        state.currentValue - change.positionChange().x / segmentWidthPx
                    launch {
                        onValueChange()
                        state.snapTo(horizontalDragOffset)
                    }
                    tracker.addPosition(change.uptimeMillis, change.position)
                    if (change.positionChange() != Offset.Zero) change.consume()
                }
            }
            val velocity = tracker.calculateVelocity().x / (numSegments)
            val targetValue = decay.calculateTargetValue(state.currentValue, -velocity)
            launch {
                state.decayTo(velocity, targetValue)
            }
        }
    }
}



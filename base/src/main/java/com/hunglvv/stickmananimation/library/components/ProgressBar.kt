package com.hunglvv.stickmananimation.library.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color,
    trackColor: Color,
    strokeWidth: Dp,
    overlay: @Composable () -> Unit = {}
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }
    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier
                .progressSemantics(progress)
        ) {
            // Start at 12 o'clock
            val startAngle = 270f
            val sweep = progress * 360f
            drawDeterminateCircularIndicator(startAngle, 360f, trackColor, stroke)
            drawCircularIndicator(
                startAngle = startAngle,
                sweep = sweep,
                color = color,
                stroke = stroke
            )
        }

        overlay()
    }
}

private fun DrawScope.drawDeterminateCircularIndicator(
    startAngle: Float,
    sweep: Float,
    trackColor: Color,
    stroke: Stroke
) = drawCircularTrackIndicator(startAngle, sweep, trackColor, stroke)

private fun DrawScope.drawCircularTrackIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    rotate(degrees = -90f) {
        drawArc(
            color,
            startAngle = startAngle + 90,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}

@Preview
@Composable
fun PreviewProgressBar() {
    CircularProgressBar(
        progress = 0.50f,
        color = Color.Yellow,
        trackColor = Color.White.copy(alpha = 0.15f),
        strokeWidth = 4.34.dp,
        modifier = Modifier.size(85.dp)
    ) {

    }
}
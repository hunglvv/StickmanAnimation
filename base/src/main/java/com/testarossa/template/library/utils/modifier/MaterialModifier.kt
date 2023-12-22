package com.testarossa.template.library.utils.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.circleLayout() =
    layout { measurable, constraints ->
        // Measure the composable
        val placeable = measurable.measure(constraints)

        //get the current max dimension to assign width=height
        val currentHeight = placeable.height
        val currentWidth = placeable.width
        val newDiameter = maxOf(currentHeight, currentWidth)

        //assign the dimension and the center position
        layout(newDiameter, newDiameter) {
            // Where the composable gets placed
            placeable.placeRelative(
                (newDiameter - currentWidth) / 2,
                (newDiameter - currentHeight) / 2
            )
        }
    }


fun Modifier.advancedShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 360.dp,
    blurRadius: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.5.dp,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint().apply {
                style = PaintingStyle.Stroke
                strokeWidth = 1.dp.toPx()
            }

            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()

            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)

fun Modifier.advancedSwitchShadow(
    shadow: Shadow = Shadow(
        Color.Black.copy(alpha = 0.5f),
        Offset(0f, 2f),
        6f
    ),
    shape: Shape = CircleShape
) = this.then(
    ShadowModifier(shadow, shape)
)


private class ShadowModifier(
    val shadow: Shadow,
    val shape: Shape
) : DrawModifier {

    override fun ContentDrawScope.draw() {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = shadow.color
                asFrameworkPaint().apply {
                    maskFilter = BlurMaskFilter(
                        convertRadiusToSigma(shadow.blurRadius),
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
            }
            shape.createOutline(
                size, layoutDirection, this
            ).let { outline ->
                canvas.drawWithOffset(shadow.offset) {
                    when (outline) {
                        is Outline.Rectangle -> {
                            drawRect(outline.rect, paint)
                        }

                        is Outline.Rounded -> {
                            drawPath(
                                Path().apply { addRoundRect(outline.roundRect) },
                                paint
                            )
                        }

                        is Outline.Generic -> {
                            drawPath(outline.path, paint)
                        }
                    }
                }
            }
        }
        drawContent()
    }

    private fun convertRadiusToSigma(
        radius: Float,
        enable: Boolean = true
    ): Float = if (enable) {
        (radius * 0.57735 + 0.5).toFloat()
    } else {
        radius
    }

    private fun Canvas.drawWithOffset(
        offset: Offset,
        block: Canvas.() -> Unit
    ) {
        save()
        translate(offset.x, offset.y)
        block()
        restore()
    }
}
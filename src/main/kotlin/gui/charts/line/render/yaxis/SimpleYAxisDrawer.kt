package gui.charts.line.render.yaxis

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gui.AppTheme
import gui.charts.LabelFormatter
import org.jetbrains.skia.Font
import org.jetbrains.skia.TextLine
import kotlin.math.abs
import kotlin.math.roundToInt


val NICE_NUMBERS = listOf(1.0, 2.0, 2.5, 4.0, 5.0).map { x ->
    listOf(1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1.0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6).map { y -> x * y }
}.flatten()

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2022/3/10 20:50
 * @Version 1.0
 * @Description TO-DO
 */

class SimpleYAxisDrawer(
    val labelTextSize: TextUnit = 12.sp,
    val labelTextColor: Color = AppTheme.colorScheme.onSurface,
    val drawLabelEvery: Int = 1,
    val labelValueFormatter: LabelFormatter = { value -> "%.1f".format(value) },
    val axisLineThickness: Dp = 1.dp,
    val axisLineColor: Color = AppTheme.colorScheme.onSurface,
) : IYAxisDrawer {
    private val mAxisLinePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = axisLineColor
            style = PaintingStyle.Stroke
        }
    }

    private val mTextPaint by lazy {
        org.jetbrains.skia.Paint().apply {
            isAntiAlias = true
            color = labelTextColor.toArgb()
        }
    }

    private val mTextFont by lazy {
        Font()
    }

    override fun drawAxisLine(drawScope: DrawScope, canvas: Canvas, drawableArea: Rect) =
        with(drawScope) {
            val lineThickness = axisLineThickness.toPx()
            val x = drawableArea.right - lineThickness / 2F
            canvas.drawLine(
                p1 = Offset(x = x, y = drawableArea.top),
                p2 = Offset(x = x, y = drawableArea.bottom),
                paint = mAxisLinePaint.apply { strokeWidth = lineThickness })
        }

    override fun drawAxisLabels(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        minValue: Float,
        maxValue: Float
    ) {
        with(drawScope) {
            val labelPaint = mTextPaint

            val labelFont = mTextFont.apply {
                size = labelTextSize.toPx()
            }

            val minLabelHeight = labelTextSize.toPx() * drawLabelEvery.toFloat() * 2
            val totalHeight = drawableArea.height
            val labelCount = (drawableArea.height / minLabelHeight).roundToInt().coerceAtLeast(2)

            // Make them nice numbers
            val spacing = NICE_NUMBERS.mapIndexed { index, it ->
                Pair(index, abs(it - (maxValue - minValue) / labelCount) )
            }.minBy { (_, it) -> it }
            val realMinValue = (minValue / NICE_NUMBERS[spacing.first]).roundToInt() * NICE_NUMBERS[spacing.first]

            for (i in 0..labelCount) {
                val value = realMinValue + i * NICE_NUMBERS[spacing.first]
                val label = labelValueFormatter(value.toFloat())
                val textLine = TextLine.make(label, labelFont)

                val x = drawableArea.right - axisLineThickness.toPx() - textLine.width - labelTextSize.toPx() / 2
                val y = drawableArea.bottom - i * (totalHeight / labelCount) - textLine.height / 2F  // todo do it legitly
                canvas.nativeCanvas.drawTextLine(textLine, x, y, labelPaint)
            }
        }
    }
}
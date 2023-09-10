package gui.charts.line.render.xaxis

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
 * @Created at 2022/3/10 20:45
 * @Version 1.0
 * @Description TO-DO
 */

class SimpleXAxisDrawer(
    val labelTextSize: TextUnit = 12.sp,
    val labelTextColor: Color = AppTheme.colorScheme.onSurface,
    val drawLabelEvery: Int = 1,// draw label text every $drawLabelEvery, like 1, 2, 3 and so on.
    val axisLineThickness: Dp = 1.dp,
    val axisLineColor: Color = AppTheme.colorScheme.onSurface,
    val labelValueFormatter: LabelFormatter = { value -> "%.1f".format(value) },
) : IXAxisDrawer {
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
        org.jetbrains.skia.Font()
    }

    override fun requireHeight(drawScope: DrawScope): Float = with(drawScope) {
        1.5F * (labelTextSize.toPx() + axisLineThickness.toPx())
    }

    override fun drawXAxisLine(drawScope: DrawScope, canvas: Canvas, drawableArea: Rect) {
        with(drawScope) {
            val lineThickness = axisLineThickness.toPx()
            val y = drawableArea.top + lineThickness / 2F

            canvas.drawLine(
                p1 = Offset(x = drawableArea.left, y = y),
                p2 = Offset(x = drawableArea.right, y = y),
                paint = mAxisLinePaint.apply { strokeWidth = lineThickness })
        }
    }

    override fun drawXAxisLabels(
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

            val minLabelWidth = labelTextSize.toPx() * drawLabelEvery.toFloat() * 5
            val totalWidth = drawableArea.width
            val labelCount = (drawableArea.width / minLabelWidth).roundToInt().coerceAtLeast(2)

            // Make them nice numbers
            val spacing = gui.charts.line.render.yaxis.NICE_NUMBERS.mapIndexed { index, it ->
                Pair(index, abs(it - (maxValue - minValue) / labelCount) )
            }.minBy { (_, it) -> it }
            val realMinValue = (minValue / NICE_NUMBERS[spacing.first]).roundToInt() * NICE_NUMBERS[spacing.first]

            for (i in 0..labelCount) {
                val value = realMinValue + i * NICE_NUMBERS[spacing.first]
                val label = labelValueFormatter(value.toFloat())
                val textLine = TextLine.make(label, labelFont)

                val x = drawableArea.left + i * (totalWidth / labelCount)
                val y = drawableArea.bottom

                canvas.nativeCanvas.drawTextLine(textLine, x, y, labelPaint)
            }
        }
    }
}
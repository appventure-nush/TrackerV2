package gui.charts.line

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2022/3/10 20:27
 * @Version 1.0
 * @Description TO-DO
 */

data class LineChartData(
    val points: List<Point>,
    val padBy: Float = 0F, // percentage we pad yValue by
    val startAtZero: Boolean = false
) {
    init {
        require(padBy in 0F..100F) {
            "padBy must be between 0F and 100F, included"
        }
    }

    private val xMinMaxValues: Pair<Float, Float>
        get() {
            val minValue = points.minOf { it.x }
            val maxValue = points.maxOf { it.x }
            return minValue to maxValue
        }

    private val yMinMaxValues: Pair<Float, Float>
        get() {
            val minValue = points.minOf { it.y }
            val maxValue = points.maxOf { it.y }
            return minValue to maxValue
        }

    internal val maxX: Float
        get() = xMinMaxValues.second + (xMinMaxValues.second - xMinMaxValues.first) * padBy / 100F
    internal val minX: Float
        get() = if (startAtZero) 0F else xMinMaxValues.first - (xMinMaxValues.second - xMinMaxValues.first) * padBy / 100F

    internal val maxY: Float
        get() = yMinMaxValues.second + (yMinMaxValues.second - yMinMaxValues.first) * padBy / 100F
    internal val minY: Float
        get() = if (startAtZero) 0F else yMinMaxValues.first - (yMinMaxValues.second - yMinMaxValues.first) * padBy / 100F

    internal val xRange: Float
        get() = maxX - minX
    internal val yRange: Float
        get() = maxY - minY

    class Point(val x: Float, val y: Float)
}
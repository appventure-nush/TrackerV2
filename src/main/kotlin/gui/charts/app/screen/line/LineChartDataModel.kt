package gui.charts.app.screen.line

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import gui.charts.line.LineChartData
import gui.charts.line.LineChartData.Point
import gui.charts.line.render.point.EmptyPointDrawer
import gui.charts.line.render.point.FilledCircularPointDrawer
import gui.charts.line.render.point.HollowCircularPointDrawer
import gui.charts.line.render.point.IPointDrawer
import kotlin.random.Random

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2022/3/10 21:16
 * @Version 1.0
 * @Description TO-DO
 */

class LineChartDataModel {
    var lineChartData by mutableStateOf(
        LineChartData(
            points = listOf(
                Point(randomYValue(), "Label 1"),
                Point(randomYValue(), "Label 2"),
                Point(randomYValue(), "Label 3"),
                Point(randomYValue(), "Label 4"),
                Point(randomYValue(), "Label 5"),
                Point(randomYValue(), "Label 6"),
                Point(randomYValue(), "Label 7")
            )
        )
    )

    var horizontalOffset by mutableStateOf(5F)
    var pointDrawerType by mutableStateOf(PointDrawerType.Hollow)
    val pointDrawer: IPointDrawer
        get() {
            return when (pointDrawerType) {
                PointDrawerType.None -> EmptyPointDrawer
                PointDrawerType.Filled -> FilledCircularPointDrawer()
                PointDrawerType.Hollow -> HollowCircularPointDrawer()
            }
        }


    private fun randomYValue(): Float = Random.Default.nextInt(45, 145).toFloat()
}
package gui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import backend.image_processing.postprocess.Postprocessor
import gui.charts.line.LineChart
import gui.charts.line.LineChartData
import gui.charts.line.render.SolidLineDrawer
import gui.charts.line.render.point.FilledCircularPointDrawer
import gui.charts.simpleChartAnimation

@Composable
fun DataDisplay(postprocessors: List<Postprocessor>) {
    LineChart(
        lineChartData = LineChartData(
            points = listOf(
                LineChartData.Point(0.0f, "Line 1"),
                LineChartData.Point(1.0f, "Line 2"),
                LineChartData.Point(2.0f, "Line 3"),
                LineChartData.Point(3.0f, "Line 4"),
                LineChartData.Point(4.0f, "Line 5"),
                LineChartData.Point(5.0f, "Line 6"),
                LineChartData.Point(6.0f, "Line 7")
            )
        ),
        // Optional properties.
        modifier = Modifier.fillMaxSize(),
        animation = simpleChartAnimation(),
        pointDrawer = FilledCircularPointDrawer(),
        lineDrawer = SolidLineDrawer(),
        horizontalOffset = 5f
    )
}

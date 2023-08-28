package gui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backend.image_processing.postprocess.Postprocessor
import gui.charts.line.LineChart
import gui.charts.line.LineChartData
import gui.charts.line.render.SolidLineDrawer
import gui.charts.line.render.point.FilledCircularPointDrawer


abstract class GraphData


data class ScatterPlotData(var xAxis: String = "", var yAxis: String = ""): GraphData() {
    val name = "Scatter Plot"
}


@Composable
fun ScatterPlotPane(scatterPlotData: ScatterPlotData, postprocessors: List<Postprocessor>) {
    val xAxis = remember { mutableStateOf(scatterPlotData.xAxis) }
    val yAxis = remember { mutableStateOf(scatterPlotData.yAxis) }

    val points = remember {
        mutableStateOf(
            listOf(
                LineChartData.Point(0.0f, 0.0f),
                LineChartData.Point(1.0f, 1.0f)
            )
        )
    }

    val options = postprocessors.map { it.entries.map { it2 -> Pair("$it.$it2", Pair(it, it2)) } }.flatten().toMap()

    try {
        val (postprocessor, entry) = options[xAxis.value]!!
        val (_, entry2) = options[yAxis.value]!!

        points.value = postprocessor.data!!.rows.map {
            LineChartData.Point(
                x = (it[entry] as Double).toFloat(),
                y = (it[entry2] as Double).toFloat()
            )
        }.toList().takeLast(500)
    } catch (_: NullPointerException) {}

    Card(
        elevation = 10.dp,
        modifier = Modifier.padding(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(10.dp).fillMaxWidth(), Arrangement.spacedBy(15.dp)) {
            Combobox(
                label = "x-axis",
                selectedItem = xAxis,
                items = options.keys.toList()
            ) {
                try {
                    scatterPlotData.xAxis = xAxis.value

                    val (postprocessor, entry) = options[xAxis.value]!!
                    val (_, entry2) = options[yAxis.value]!!

                    points.value = postprocessor.data!!.rows.map {
                        LineChartData.Point(
                            x = (it[entry] as Double).toFloat(),
                            y = (it[entry2] as Double).toFloat()
                        )
                    }.toList().takeLast(500)
                } catch (_: NullPointerException) {}
            }

            Combobox(
                label = "y-axis",
                selectedItem = yAxis,
                items = options.keys.toList(),
                onValueChanged = {
                    try {
                        scatterPlotData.yAxis = yAxis.value

                        val (postprocessor, entry) = options[xAxis.value]!!
                        val (_, entry2) = options[yAxis.value]!!

                        points.value = postprocessor.data!!.rows.map {
                            LineChartData.Point(
                                x = (it[entry] as Double).toFloat(),
                                y = (it[entry2] as Double).toFloat()
                            )
                        }.toList().takeLast(500)
                    } catch (_: NullPointerException) {}
                }
            )

            LineChart(
                lineChartData = LineChartData(
                    points = points.value
                ),
                animation = null, //simpleChartAnimation(),
                pointDrawer = FilledCircularPointDrawer(color=MaterialTheme.colors.secondaryVariant),
                lineDrawer = SolidLineDrawer(color=MaterialTheme.colors.secondary),
                modifier = Modifier.padding(15.dp).height(300.dp)
            )
        }
    }
}

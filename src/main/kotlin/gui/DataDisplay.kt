package gui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.image_processing.postprocess.Postprocessor
import gui.charts.line.LineChart
import gui.charts.line.LineChartData
import gui.charts.line.render.SolidLineDrawer
import gui.charts.line.render.point.FilledCircularPointDrawer


abstract class GraphData


data class ScatterPlotData(var xAxis: String = "", var yAxis: String = ""): GraphData() {
    val name = "Scatter Plot"
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScatterPlotPane(
    scatterPlotData: ScatterPlotData,
    postprocessors: List<Postprocessor>,
    onDelete: () -> Unit
) {
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

    val deleteDialog = remember { mutableStateOf(false) }

    try {
        val (postprocessor, entry) = options[xAxis.value]!!
        val (_, entry2) = options[yAxis.value]!!

        points.value = postprocessor.data!!.rows.map {
            LineChartData.Point(
                x = (it[entry] as Double).toFloat(),
                y = (it[entry2] as Double).toFloat()
            )
        }.toList().takeLast(500)
    } catch (e: Exception) {
        println(e)
    }

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
                } catch (e: Exception) {
                    println(e)
                }
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
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            )

            LineChart(
                lineChartData = LineChartData(
                    points=points.value.ifEmpty {
                        listOf(
                            LineChartData.Point(0.0f, 0.0f),
                            LineChartData.Point(1.0f, 1.0f)
                        )
                    }
                ),
                animation = null, //simpleChartAnimation(),
                pointDrawer = FilledCircularPointDrawer(color=MaterialTheme.colors.secondaryVariant),
                lineDrawer = SolidLineDrawer(color=MaterialTheme.colors.secondary),
                modifier = Modifier.padding(15.dp).height(300.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                // The delete button
                TooltipArea(
                    tooltip = {
                        Surface(
                            modifier = Modifier.shadow(4.dp),
                            color = Color(50, 50, 50, 255),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Deletes this node",
                                fontSize = 8.sp,
                                modifier = Modifier.padding(5.dp),
                                color = Color(255, 255, 255)
                            )
                        }
                    },
                    modifier = Modifier.padding(start = 0.dp),
                    delayMillis = 600
                ) {
                    IconButton(
                        onClick = { deleteDialog.value = true },
                        modifier = Modifier.size(23.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete, contentDescription = "",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    // Check if user would like to delete
    if (deleteDialog.value) {
        AlertDialog(
            title = { Text("Confirm deletion of graph?") },
            text = { Text("Would you like to delete this graph? There is no turning back.") },
            confirmButton = { TextButton({ deleteDialog.value = false; onDelete() }) { Text("Yes") } },
            dismissButton = { TextButton({ deleteDialog.value = false }) { Text("No") } },
            onDismissRequest = { deleteDialog.value = false },
            modifier = Modifier.size(300.dp, 200.dp).padding(10.dp)
        )
    }
}

package gui

//import com.github.ajalt.colormath.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.Video
import backend.image_processing.postprocess.*
import backend.image_processing.preprocess.*
import java.awt.FileDialog
import java.io.File
import kotlin.random.Random


data class Page(val name: String, val content: @Composable () -> Unit)

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun NodesPane(
    video: Video,
    graphs: MutableList<GraphData>,
    windowWidth: MutableState<Dp>,
    width: MutableState<Dp>,
    onUpdate: MutableState<Int>,
    syncing: MutableState<Boolean>
) {
    val preprocessor = video.preprocesser
    val postprocessors = video.postprocessors

    val preprocessingItems = listOf(BlurringNode(), MorphologicalNode(), ThresholdingNode(), CannyEdgeNode(), ColourRangeNode())
    val postprocessingItems = listOf(  // -1 is needed because Kotlin is being funny and refusing to compile
        EllipseFittingNode(-1),
        CircleFittingNode(-1),
        ContourFittingNode(-1)
    )
    val graphsList = listOf(ScatterPlotData())

    val expanded = remember { mutableStateOf(false) }

    val errorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    fun deleteNode(it: PreprocessingNode) = run {
        preprocessor.nodes.remove(it)
        onUpdate.value = Random.nextInt(100)
    }

    fun deleteNode(node: PostprocessingNode) = run {
        postprocessors.removeAll { it.node == node }
        onUpdate.value = Random.nextInt(100)
    }

    fun shift(it: Int, node: PreprocessingNode) = run {
        val index = preprocessor.nodes.indexOf(node)
        preprocessor.nodes.remove(node)

        if (index + it < 0) preprocessor.nodes.add(0, node)
        else if (index + it >= preprocessor.nodes.size + 1) preprocessor.nodes.add(preprocessor.nodes.size, node)
        else preprocessor.nodes.add(index + it, node)

        onUpdate.value = Random.nextInt(100)
    }

    fun saveData(it: Int) = run {
        try {
            val dialog = FileDialog(ComposeWindow(), "Save Data", FileDialog.SAVE)
            dialog.file = "*.csv"
            dialog.isVisible = true

            if (dialog.file != null)
                postprocessors[it].export(File(dialog.directory + "/" + dialog.file))
        } catch (exception: Exception) {
            errorDialog.value = true
            errorMessage.value = exception.toString()
        }
    }

    val selectedItem = remember { mutableStateOf(0) }
    val pages = listOf(
        Page("Preprocessing") {
            Box {
                onUpdate.value  // magic h0xs

                val state = rememberLazyListState()
                LazyColumn(modifier = Modifier.width(windowWidth.value - 95.dp - width.value).padding(end = 12.dp), state) {
                    items(preprocessor.nodes.size) {
                        Row(modifier = Modifier.animateItemPlacement()) {
                            when (val node = preprocessor.nodes[it]) {
                                is BlurringNode -> BlurringPane(node, { deleteNode(node) }, { j -> shift(j, node) })
                                is MorphologicalNode -> MorphologicalPane(node, { deleteNode(node) }, { j -> shift(j, node) })
                                is ThresholdingNode -> ThresholdingPane(node, { deleteNode(node) }, { j -> shift(j, node) })
                                is CannyEdgeNode -> CannyEdgePane(node, { deleteNode(node) }, { j -> shift(j, node) })
                                is ColourRangeNode -> ColorRangePane(node, { deleteNode(node) }, { j -> shift(j, node) })
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )

                Column(modifier = Modifier.align(Alignment.BottomEnd)) {
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        preprocessingItems.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(text = it.name, fontSize = 12.sp)
                                }, onClick = {
                                    expanded.value = false
                                    preprocessor.nodes.add(it.clone())
                                }
                            )
                        }
                    }
                }
            }
        },
        Page("Postprocessing") {
            Box {
                onUpdate.value  // magic h0xs

                val state = rememberLazyListState()

                LazyColumn(modifier = Modifier.width(windowWidth.value - 95.dp - width.value).padding(end = 12.dp), state) {
                    items(postprocessors.size) {
                        Row(modifier = Modifier.animateItemPlacement()) {
                            when (val node = postprocessors[it].node) {
                                is EllipseFittingNode -> EllipseFittingPane(
                                    node,
                                    { deleteNode(node) },
                                    { postprocessors[it].clear() },
                                    { saveData(it) }
                                )
                                is CircleFittingNode -> CircleFittingPane(
                                    node,
                                    { deleteNode(node) },
                                    { postprocessors[it].clear() },
                                    { saveData(it) }
                                )
                                is ContourFittingNode -> ContourFittingPane(
                                    node,
                                    { deleteNode(node) },
                                    { postprocessors[it].clear() },
                                    { saveData(it) }
                                )
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )

                Column(modifier = Modifier.align(Alignment.BottomEnd)) {
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        postprocessingItems.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(text = it.name, fontSize = 12.sp)
                                },
                                onClick = {
                                    expanded.value = false
                                    postprocessors.add(Postprocessor(it.clone()))
                                }
                            )
                        }
                    }
                }
            }
        },
        Page("Graphs") {
            Box {
                onUpdate.value  // magic h0xs

                val state = rememberLazyListState()

                LazyColumn(modifier = Modifier.width(windowWidth.value - 95.dp - width.value).padding(end = 12.dp), state) {
                    items(graphs.size) {
                        Row(modifier = Modifier.animateItemPlacement()) {
                            when (val graphData = graphs[it]) {
                                is ScatterPlotData -> ScatterPlotPane(graphData, postprocessors) {
                                    graphs.remove(graphData)
                                    onUpdate.value = Random.nextInt(100)
                                }
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )

                Column(modifier = Modifier.align(Alignment.BottomEnd)) {
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        graphsList.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(text = it.name, fontSize = 12.sp)
                                },
                                onClick = {
                                    expanded.value = false
                                    graphs.add(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    val icons = listOf(Icons.Filled.FilterAlt, Icons.Filled.SquareFoot, Icons.Filled.Analytics)
    Box {
        Row(
            modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            pages[selectedItem.value].content()

            NavigationRail(
                modifier = Modifier
            ) {
                pages.forEachIndexed { index, item ->
                    TooltipArea(
                        tooltip = {
                            Surface(
                                modifier = Modifier.shadow(4.dp),
                                color = Color(50, 50, 50, 255),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(5.dp),
                                    color = Color(255, 255, 255)
                                )
                            }
                        },
                        modifier = Modifier.padding(start = 0.dp),
                        delayMillis = 600
                    ) {
                        NavigationRailItem(
                            //label = { Text(item.name) },
                            icon = { Icon(icons[index], contentDescription = "") },
                            selected = selectedItem.value == index,
                            onClick = { selectedItem.value = index },
                            //alwaysShowLabel = false
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            FloatingActionButton(
                containerColor = AppTheme.colorScheme.surface,
                contentColor = AppTheme.colorScheme.onSurface,
                modifier = Modifier.width(55.dp).height(55.dp).padding(7.dp),
                onClick = {
                    syncing.value = !syncing.value
                },
            ) {
                Icon(if (syncing.value) Icons.Filled.Sync else Icons.Filled.SyncDisabled, "")
            }

            FloatingActionButton(
                containerColor = AppTheme.colorScheme.primary,
                contentColor = AppTheme.colorScheme.onPrimary,
                modifier = Modifier.width(55.dp).height(55.dp).padding(7.dp),
                onClick = {
                    expanded.value = true
                },
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    }

    AnimatedVisibility(
        visible = errorDialog.value
    ) {
        AlertDialog(
            title = { Text("Error") },
            text = { Text(errorMessage.value) },
            confirmButton = {
                TextButton({ errorDialog.value = false }) { Text("Ok") }
            },
            onDismissRequest = { errorDialog.value = false },
            modifier = Modifier.padding(10.dp)
        )
    }
}
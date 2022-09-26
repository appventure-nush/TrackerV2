package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.image_processing.preprocess.PreprocessingNode
import backend.image_processing.preprocess.Preprocessor
import backend.image_processing.preprocess.blurring.BlurringNode
import backend.image_processing.preprocess.edge_detection.CannyEdgeNode
import backend.image_processing.preprocess.masking.ThresholdingNode
import backend.image_processing.preprocess.morphological.MorphologicalNode


data class Page(val name: String, val content: @Composable () -> Unit)

@Preview
@Composable
fun NodesPane(preprocessor: Preprocessor) {
    val items = listOf(BlurringNode(), MorphologicalNode(), ThresholdingNode(), CannyEdgeNode())
    val expanded = remember { mutableStateOf(false) }

    fun deleteNode(it: PreprocessingNode) = run { preprocessor.nodes.remove(it) }
    fun shift(it: Int, node: PreprocessingNode) = run {
        val index = preprocessor.nodes.indexOf(node)
        preprocessor.nodes.remove(node)

        if (index + it < 0) preprocessor.nodes.add(0, node)
        else if (index + it >= preprocessor.nodes.size + 1) preprocessor.nodes.add(preprocessor.nodes.size, node)
        else preprocessor.nodes.add(index + it, node)
    }

    val selectedItem = remember { mutableStateOf(0) }
    val pages = listOf(
        Page("Preprocessing") {
            Box {
                val state = rememberLazyListState()

                LazyColumn(modifier = Modifier.width(250.dp).padding(end = 12.dp), state) {
                    // TODO Auto-refresh when node is deleted
                    items(preprocessor.nodes) {
                        when (it) {
                            is BlurringNode -> BlurringPane(it, { deleteNode(it) }, { j -> shift(j, it) })
                            is MorphologicalNode -> MorphologicalPane(it, { deleteNode(it) }, { j -> shift(j, it) })
                            is ThresholdingNode -> ThresholdingPane(it, { deleteNode(it) }, { j -> shift(j, it) })
                            is CannyEdgeNode -> CannyEdgePane(it, { deleteNode(it) }, { j -> shift(j, it) })
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
                        items.forEach {
                            DropdownMenuItem(onClick = {
                                expanded.value = false
                                preprocessor.nodes.add(it.clone())
                            }) {
                                Text(text = it.name, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        Page("Postprocessing") {}
    )

    val icons = listOf(Icons.Filled.FilterAlt, Icons.Filled.SquareFoot)
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

        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White,
            modifier = Modifier.width(55.dp).height(55.dp).padding(7.dp).align(Alignment.BottomEnd),
            onClick = {
                expanded.value = true
            },
        ) {
            Icon(Icons.Filled.Add, "")
        }
    }
}
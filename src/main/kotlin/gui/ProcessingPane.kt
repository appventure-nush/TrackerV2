package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.image_processing.Processing
import backend.image_processing.postprocess.fitting.CircleFittingNode
import backend.image_processing.postprocess.fitting.EllipseFittingNode
import backend.image_processing.preprocess.Blurring
import backend.image_processing.preprocess.BlurringNode
import backend.image_processing.preprocess.CannyEdgeNode
import backend.image_processing.preprocess.ThresholdingNode
import backend.image_processing.preprocess.Morphological
import backend.image_processing.preprocess.MorphologicalNode

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Preview
@Composable
fun ProcessingPane(
    node: Processing,
    postProcessing: Boolean = false,
    onDelete: () -> Unit,
    shift: (Int) -> Unit,
    clearData: () -> Unit,
    save: () -> Unit,
    options: @Composable () -> Unit
) {
    val helpDialog = remember { mutableStateOf(false) }
    val deleteDialog = remember { mutableStateOf(false) }

    val count = remember { mutableStateOf(0) }
    val collecting = remember { mutableStateOf(false) }

    // Store the pane in a card
    Card(
        elevation = 10.dp,
        modifier = Modifier.padding(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
            Box {
                Row(Modifier.padding(10.dp).fillMaxWidth(), Arrangement.spacedBy(5.dp)) {
                    // The title
                    Text(node.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    // The help tooltip
                    TooltipArea(
                        tooltip = {
                            Surface(
                                modifier = Modifier.shadow(4.dp),
                                color = Color(50, 50, 50, 255),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = node.help,
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(5.dp),
                                    color = Color(255, 255, 255)
                                )
                            }
                        },
                        modifier = Modifier.padding(start = 0.dp),
                        delayMillis = 600
                    ) {
                        // The help button
                        IconButton(onClick = { helpDialog.value = true }, modifier = Modifier.size(23.dp)) {
                            Icon(
                                painterResource("help_black_24dp.svg"), contentDescription = "",
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                }

                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    // Colourspace selector
                    TextButton(
                        onClick = {
                            count.value++
                            count.value %= node.inputColourspaces.size
                            node.inputColourspace = node.inputColourspaces[count.value]
                        }
                    ) {
                        Text(node.inputColourspaces[count.value].toString(), fontSize = 12.sp)
                    }
                }
            }

            options()

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (!postProcessing) {
                    // Shift up button
                    IconButton(onClick = { shift(-1) }, modifier = Modifier.size(23.dp)) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp, contentDescription = "",
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    // Shift down button
                    IconButton(onClick = { shift(1) }, modifier = Modifier.size(23.dp)) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown, contentDescription = "",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                } else {
                    // Collect data
                    IconButton(
                        onClick = { clearData() },
                        modifier = Modifier.size(23.dp)
                    ) {
                        Icon(
                            Icons.Filled.ClearAll,
                            contentDescription = "",
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    // Save data
                    IconButton(onClick = { save() }, modifier = Modifier.size(23.dp)) {
                        Icon(
                            Icons.Filled.Save, contentDescription = "",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // The delete button
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

    // Open the help dialog
    if (helpDialog.value) {
        AlertDialog(
            title = { Text("Help") },
            text = { Text(node.help) },
            confirmButton = {
                TextButton({ helpDialog.value = false }) { Text("Ok") }
            },
            onDismissRequest = { helpDialog.value = false },
            modifier = Modifier.size(300.dp, 250.dp).padding(10.dp)
        )
    }

    // Check if user would like to delete
    if (deleteDialog.value) {
        AlertDialog(
            title = { Text("Confirm deletion of node?") },
            text = { Text("Would you like to delete this node? There is no turning back.") },
            confirmButton = { TextButton({ deleteDialog.value = false; onDelete() }) { Text("Yes") } },
            dismissButton = { TextButton({ deleteDialog.value = false }) { Text("No") } },
            onDismissRequest = { deleteDialog.value = false },
            modifier = Modifier.size(300.dp, 200.dp).padding(10.dp)
        )
    }
}

@Preview
@Composable
fun BlurringPane(node: BlurringNode, onDelete: () -> Unit, shift: (Int) -> Unit) {
    val kernelSize = remember { mutableStateOf(node.kernelSize.toFloat()) }
    val blurType = remember { mutableStateOf(node.blurType) }

    ProcessingPane(node, false, onDelete, shift, {}, {}) {
        Column {
            // For adjusting kernel size
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Kernel Size: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = kernelSize.value,
                    valueRange = 3.0f .. 31.0f,
                    onValueChange = {
                        kernelSize.value = it
                        node.kernelSize = kernelSize.value.toInt() / 2 * 2 + 1
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    (kernelSize.value.toInt() / 2 * 2 + 1).toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Changing type of blurring
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Combobox(
                    "Blur Type", blurType,
                    listOf(Blurring.GAUSSIAN, Blurring.MEDIAN, Blurring.BOX_FILTER),
                    modifier = Modifier.height(53.dp),
                    onValueChanged = { node.blurType = blurType.value }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ThresholdingPane(node: ThresholdingNode, onDelete: () -> Unit, shift: (Int) -> Unit) {
    val thresholdRange = remember { mutableStateOf(node.minThreshold.toFloat() .. node.maxThreshold.toFloat()) }
    val binarise = remember { mutableStateOf(node.binarise) }

    ProcessingPane(node, false, onDelete, shift, {}, {}) {
        Column {
            /*
            // For adjusting minimum threshold
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Min Threshold: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = minThreshold.value,
                    valueRange = 0.0f .. 255.0f,
                    onValueChange = {
                        minThreshold.value = it
                        node.minThreshold = it.toDouble()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    minThreshold.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // For adjusting maximum threshold
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Max Threshold: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = maxThreshold.value,
                    valueRange = 0.0f .. 255.0f,
                    onValueChange = {
                        maxThreshold.value = it
                        node.maxThreshold = it.toDouble()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    maxThreshold.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
             */

            // Checkbox for binarisation
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Binarise",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Checkbox(
                    checked = binarise.value,
                    onCheckedChange = {
                        binarise.value = it
                        node.binarise = it
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colors.primary,
                        uncheckedColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        checkmarkColor = MaterialTheme.colors.surface,
                        disabledColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        disabledIndeterminateColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
                    )
                )
            }

            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Threshold Range: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                RangeSlider(
                    values = thresholdRange.value,
                    valueRange = 0.0f .. 255.0f,
                    onValueChange = {
                        thresholdRange.value = it
                        node.minThreshold = it.start.toDouble()
                        node.maxThreshold = it.endInclusive.toDouble()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun MorphologicalPane(node: MorphologicalNode, onDelete: () -> Unit, shift: (Int) -> Unit) {
    val iterations = remember { mutableStateOf(node.iterations.toFloat()) }
    val kernelSize = remember { mutableStateOf(node.kernelSize.toFloat()) }
    val operationType = remember { mutableStateOf(node.operationType) }

    ProcessingPane(node, false, onDelete, shift, {}, {}) {
        Column {
            // For adjusting kernel size
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Kernel Size: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = kernelSize.value,
                    valueRange = 3.0f .. 31.0f,
                    onValueChange = {
                        kernelSize.value = it
                        node.kernelSize = kernelSize.value.toInt() / 2 * 2 + 1
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    (kernelSize.value.toInt() / 2 * 2 + 1).toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // For adjusting iterations
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Iterations: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = iterations.value,
                    valueRange = 1.0f .. 20.0f,
                    onValueChange = {
                        iterations.value = it
                        node.iterations = iterations.value.toInt()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    iterations.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Changing type of morphological operation
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Combobox(
                    "Operation Type", operationType,
                    listOf(Morphological.ERODE, Morphological.DILATE),
                    modifier = Modifier.height(53.dp),
                    onValueChanged = { node.operationType = operationType.value }
                )
            }
        }
    }
}

@Preview
@Composable
fun CannyEdgePane(node: CannyEdgeNode, onDelete: () -> Unit, shift: (Int) -> Unit) {
    val kernelSize = remember { mutableStateOf(node.kernelSize.toFloat()) }
    val threshold = remember { mutableStateOf(node.threshold.toFloat()) }

    ProcessingPane(node, false, onDelete, shift, {}, {}) {
        Column {
            // For adjusting kernel size
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Kernel Size: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = kernelSize.value,
                    valueRange = 3.0f .. 7.0f,
                    onValueChange = {
                        kernelSize.value = it
                        node.kernelSize = kernelSize.value.toInt() / 2 * 2 + 1
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    (kernelSize.value.toInt() / 2 * 2 + 1).toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Changing threshold
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Threshold: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = threshold.value,
                    valueRange = 0.0f .. 600.0f,
                    onValueChange = {
                        threshold.value = it
                        node.threshold = threshold.value.toDouble()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    threshold.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview
@Composable
fun EllipseFittingPane(node: EllipseFittingNode, onDelete: () -> Unit, startCollecting: () -> Unit, save: () -> Unit) {
    ProcessingPane(node, true, onDelete, {}, startCollecting, save) {
        Column { }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun CircleFittingPane(node: CircleFittingNode, onDelete: () -> Unit, startCollecting: () -> Unit, save: () -> Unit) {
    val param1 = remember { mutableStateOf(node.param1.toFloat()) }
    val param2 = remember { mutableStateOf(node.param2.toFloat()) }
    val minDist = remember { mutableStateOf(node.param1.toFloat()) }
    val radiusRange = remember { mutableStateOf(node.minRadius.toFloat() .. node.maxRadius.toFloat()) }

    ProcessingPane(node, true, onDelete, {}, startCollecting, save) {
        Column {
            // Parameter 1 -> Controls edge detection
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Parameter 1: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = param1.value,
                    valueRange = 0.0f .. 600.0f,
                    onValueChange = {
                        param1.value = it
                        node.param1 = param1.value.toDouble()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    param1.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Parameter 2 -> Controls how circular an objects has to be a circle
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Parameter 2: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = param2.value,
                    valueRange = 0.0f .. 100.0f,
                    onValueChange = {
                        param2.value = it
                        node.param2 = param2.value.toDouble()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    param2.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Adjust minimum distance between circles
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Minimum Distance: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Slider(
                    value = minDist.value,
                    valueRange = 0.0f .. 200.0f,
                    onValueChange = {
                        minDist.value = it
                        node.minDist = minDist.value.toDouble()
                    },
                    modifier = Modifier.width(125.dp)
                )

                Text(
                    minDist.value.toInt().toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Changing range of radii
            Row(modifier = Modifier.padding(10.dp), Arrangement.spacedBy(5.dp)) {
                Text(
                    "Radius Range: ",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                RangeSlider(
                    values = radiusRange.value,
                    valueRange = 0.0f .. 100.0f,
                    onValueChange = {
                        radiusRange.value = it
                        node.minRadius = it.start.toInt()
                        node.maxRadius = it.endInclusive.toInt()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
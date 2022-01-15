package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import backend.image_processing.preprocess.blurring.Blurring
import backend.image_processing.preprocess.blurring.BlurringNode

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Preview
@Composable
fun ProcessingPane(node: Processing, onDelete: () -> Unit, options: @Composable () -> Unit) {
    val helpDialog = remember { mutableStateOf(false) }
    val deleteDialog = remember { mutableStateOf(false) }
    val count = remember { mutableStateOf(0) }

    // Store the pane in a card
    Card(elevation = 10.dp, modifier = Modifier.padding(10.dp), shape = RoundedCornerShape(10.dp)) {
        Column(Modifier.padding(10.dp).fillMaxSize(), Arrangement.spacedBy(5.dp)) {
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

            // The delete button
            /*
            IconButton(onClick = { deleteDialog.value = true },
                modifier = Modifier.size(23.dp).align(Alignment.End)) {
                Icon(
                    Icons.Filled.Delete, contentDescription = "",
                    tint = MaterialTheme.colors.primary
                )
            }
             */
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
            modifier = Modifier.size(300.dp, 200.dp).padding(10.dp)
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
fun BlurringPane(node: BlurringNode) {
    val kernelSize = remember { mutableStateOf(3.0f) }
    val blurType = remember { mutableStateOf(Blurring.GAUSSIAN) }

    ProcessingPane(node, {}) {
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

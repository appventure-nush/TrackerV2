import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import backend.Video
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.postprocess.fitting.EllipseFittingNode
import gui.NodesPane
import gui.VideoPlayer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.FileDialog

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val video = Video("video.mp4")
    video.hasNext()
    video.next().write("test.bmp")

    return application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        val width = remember { mutableStateOf(900.dp) }
        val windowWidth = remember { mutableStateOf(windowState.size.width) }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Tracker 2.0",
            state = windowState
        ) {
            LaunchedEffect(windowState) {
                snapshotFlow { windowState.size }
                    .onEach { windowWidth.value = it.width }
                    .launchIn(this)
            }

            MenuBar { // TODO Add actual functionality to menu bar
                Menu("File", mnemonic = 'F') {
                    Item("Open File", onClick = { }, shortcut = KeyShortcut(Key.O, ctrl = true))
                    Item("Save File", onClick = { }, shortcut = KeyShortcut(Key.S, ctrl = true))
                }

                Menu("Data") {
                    Item(
                        "Export Data",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Save Data", FileDialog.SAVE)
                            dialog.isVisible = true
                            //video.postprocessor!!.export(File(dialog.directory + "/" + dialog.file))
                        }
                    )
                }
            }

            MaterialTheme {
                Row(modifier = Modifier.padding(10.dp)) {
                    VideoPlayer(video, width)

                    Button(
                        modifier = Modifier.fillMaxHeight()
                            .width(1.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consumeAllChanges()
                                    width.value += dragAmount.x.toDp()
                                }
                            },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                        onClick = {}
                    ) {
                        Divider(
                            color = Color.Red,
                            modifier = Modifier.fillMaxHeight().width(1.dp)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        NodesPane(video, windowWidth, width)
                    }
                }
            }
        }
    }
}
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import backend.Video
import backend.image_processing.postprocess.PostprocessingNode
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.preprocess.Preprocessor
import gui.Axes
import gui.NodesPane
import gui.VideoPlayer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bytedeco.opencv.opencv_videoio.VideoCapture
import java.awt.FileDialog
import java.io.File
import kotlin.random.Random

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
fun main() {
    val video = Video("video0.mov")
    video.hasNext()
    video.next().write("test.bmp")

    return application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        val width = remember { mutableStateOf(900.dp) }
        val windowWidth = remember { mutableStateOf(windowState.size.width) }

        val onUpdate = remember { mutableStateOf(0) }

        val isAxesVisible = remember { mutableStateOf(false) }

        val originX = remember { mutableStateOf(0.0f) }
        val originY = remember { mutableStateOf(0.0f) }
        video.originX = originX
        video.originY = originY

        val aboutDialog = remember { mutableStateOf(false) }

        val syncing = remember { mutableStateOf(false) }

        val icon = painterResource("trackerv2.png")

        Window(
            onCloseRequest = ::exitApplication,
            title = "Tracker 2.0",
            state = windowState,
            icon = icon
        ) {
            LaunchedEffect(windowState) {
                snapshotFlow { windowState.size }
                    .onEach { windowWidth.value = it.width }
                    .launchIn(this)
            }

            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item(
                        "Open Video",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Open Configuration", FileDialog.LOAD)
                            dialog.file = "*.mp4;*.mov;*avi"
                            dialog.isVisible = true

                            if (dialog.file != null) {
                                syncing.value = false
                                video.videoCapture = VideoCapture(dialog.directory + "/" + dialog.file)
                                syncing.value = true
                            }
                        }
                    )
                    Separator()
                    Item(
                        "Open Configuration",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Open Configuration", FileDialog.LOAD)
                            dialog.file = "*.trk2"
                            dialog.isVisible = true

                            if (dialog.file != null) {
                                val text = File(dialog.directory + "/" + dialog.file).readText().split("\n")
                                val newPreprocessingNodes = Json.decodeFromString<Preprocessor>(text[0]).nodes
                                val newPostprocessingNodes = Json.decodeFromString<List<PostprocessingNode>>(text[1])

                                video.preprocesser.nodes.clear()
                                video.preprocesser.nodes.addAll(newPreprocessingNodes)

                                video.postprocessors.clear()
                                newPostprocessingNodes.forEach {
                                    video.postprocessors.add(Postprocessor(it))
                                }

                                onUpdate.value = Random.nextInt(100)
                            }
                        },
                        shortcut = KeyShortcut(Key.O, ctrl = true)
                    )
                    Item(
                        "Save Configuration",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Save Configuration", FileDialog.SAVE)
                            dialog.file = "*.trk2"
                            dialog.isVisible = true

                            if (dialog.file != null) {
                                val serialisedPreprocessor = Json.encodeToString(video.preprocesser)
                                val serialisedPostprocessor = Json.encodeToString(video.postprocessors.map { it.node })
                                File(dialog.directory + "/" + dialog.file).writeText(
                                    serialisedPreprocessor + "\n" + serialisedPostprocessor
                                )
                            }
                        },
                        shortcut = KeyShortcut(Key.S, ctrl = true)
                    )
                }
                Menu("View", mnemonic = 'V') {
                    Item(
                        "Toggle Axes Visibility",
                        onClick = {
                            isAxesVisible.value = !isAxesVisible.value
                        }
                    )
                }
                Menu("About", mnemonic = 'B') {
                    Item(
                        "About this",
                        onClick = {
                            aboutDialog.value = true
                        }
                    )
                }
            }

            MaterialTheme {
                Row(modifier = Modifier.padding(10.dp)) {
                    VideoPlayer(video, width, syncing)

                    Button(
                        modifier = Modifier.fillMaxHeight()
                            .width(1.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
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
                        NodesPane(video, windowWidth, width, onUpdate, syncing)
                    }
                }
            }

            if (isAxesVisible.value) Axes(originX, originY)

            if (aboutDialog.value) {
                AlertDialog(
                    title = { Text("About") },
                    text = {
                        Text("""
                        Tracker but better! This application is brought to you by AppVenture, the CS Interest Group
                        as well as your SYPT / IYPT alumni.
                        It was created and is maintained by Jed, with the help of Luc, Kabir and Prannaya.
                        """.trimIndent().replace("\n", " "))
                    },
                    confirmButton = {
                        TextButton({ aboutDialog.value = false }) { Text("Ok") }
                    },
                    onDismissRequest = { aboutDialog.value = false },
                    modifier = Modifier.size(300.dp, 300.dp).padding(10.dp)
                )
            }
        }
    }
}
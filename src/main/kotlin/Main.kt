import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import backend.Video
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.postprocess.fitting.CircleFittingNode
import backend.image_processing.postprocess.fitting.EllipseFittingNode
import gui.NodesPane
import gui.VideoPlayer
import java.awt.FileDialog
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val video = Video("video.mp4")
    video.hasNext()
    video.next().write("test.bmp")

    val preprocessor = video.preprocesser
    video.postprocessor = Postprocessor(EllipseFittingNode())

    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Tracker 2.0",
            state = rememberWindowState(width = 320.dp, height = 300.dp)
        ) {
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
                            video.postprocessor!!.export(File(dialog.directory + "/" + dialog.file))
                        }
                    )
                }
            }

            MaterialTheme {
                Row(modifier = Modifier.padding(10.dp)) {
                    VideoPlayer(video)

                    Column {
                        NodesPane(preprocessor)
                    }
                }
            }
        }
    }
}
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import backend.Image
import backend.Point
import backend.Video
import backend.image_processing.Circle
import backend.image_processing.Ellipse
import gui.NodesPane
import gui.VideoPlayer

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val video = Video("C:\\Users\\jedli\\OneDrive - NUS High School\\Documents\\Physics\\SYPT 2022\\" +
            "2. Rayleigh Disk\\Experimental Data\\Intensity Data 1\\IMG_3121.MOV")
    video.hasNext()
    video.next().write("test.bmp")

    val preprocessor = video.preprocesser
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
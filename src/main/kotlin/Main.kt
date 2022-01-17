import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import backend.Image
import backend.Video
import gui.VideoPlayer

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        MaterialTheme {
            VideoPlayer(Video("""C:\Users\jedli\Downloads\SYPT2022 Selections - Category B.mp4"""))
        }
    }
}
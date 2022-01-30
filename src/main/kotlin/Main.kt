import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import backend.Video
import gui.NodesPane
import gui.VideoPlayer

fun main() {
    val video = Video("C:\\Users\\jedli\\OneDrive - NUS High School\\Documents\\Physics\\SYPT 2022" +
            "\\16. Saving Honey\\Experimental Data\\IMG_1911.MOV")
    val preprocessor = video.preprocesser

    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Compose for Desktop",
            state = rememberWindowState(width = 320.dp, height = 300.dp)
        ) {
            MaterialTheme {
                Row(modifier = Modifier.padding(10.dp)) {
                    VideoPlayer(video)
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier.fillMaxHeight().width(1.dp)
                    )
                    NodesPane(preprocessor)
                }
            }
        }
    }
}
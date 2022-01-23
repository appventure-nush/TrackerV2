import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import backend.Image
import backend.Video
import backend.image_processing.preprocess.Preprocessor
import backend.image_processing.preprocess.blurring.BlurringNode
import backend.image_processing.preprocess.edge_detection.CannyEdgeNode
import backend.image_processing.preprocess.masking.ThresholdingNode
import gui.*

fun main() {
    val preprocessor = Preprocessor()

    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Compose for Desktop",
            state = rememberWindowState(width = 320.dp, height = 300.dp)
        ) {
            MaterialTheme {
                Column(modifier = Modifier.padding(10.dp)) {
                    NodesPane(preprocessor)
                }
            }
        }
    }
}
package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.Video
import java.io.File
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun VideoPlayer(video: Video) {
    val playVideo = remember { mutableStateOf(false) }
    val threadCreated = remember { mutableStateOf(false) }
    val imageBitmap = remember { mutableStateOf(loadImageBitmap(File("test.bmp").inputStream())) }

    Column(modifier = Modifier.width(950.dp).padding(10.dp), Arrangement.spacedBy(5.dp)) {
        Image(
            imageBitmap.value,
            contentDescription = ""
        )

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            // The play button
            IconButton(onClick = {
                // Switch between play and pause
                playVideo.value = !playVideo.value

                // Check if the thread has been created
                if (!threadCreated.value) {
                    threadCreated.value = true

                    // Create the thread
                    Thread {
                        var time: Duration
                        while (video.hasNext()) {
                            time = measureTime {
                                try {
                                    if (!playVideo.value) video.seek(video.currentFrame)

                                    val bytes = video.next().encode(".bmp")
                                    imageBitmap.value = loadImageBitmap(bytes.inputStream())
                                } catch (ignored: ConcurrentModificationException) {}
                            }
                        }
                    }.start()
                }
            }) {
                Icon(
                    if (playVideo.value) painterResource("pause_black_24dp.svg")
                    else painterResource("play_arrow_black_24dp.svg"),
                    contentDescription = "",
                    tint = if (playVideo.value) Color.Black else Color.Green
                )
            }

            // Show the frame number
            Text(video.currentFrame.toString(), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterVertically))

            // The slider to adjust the time-stamp
            Slider(
                value = video.currentFrame.toFloat(),
                valueRange = 0.0f..video.totalFrames.toFloat(),
                onValueChange = { video.seek(it.toInt()) }
            )
        }
    }
}

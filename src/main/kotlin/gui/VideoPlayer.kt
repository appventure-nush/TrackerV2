package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Preview
@Composable
fun VideoPlayer(video: Video) {
    val playVideo = remember { mutableStateOf(false) }
    val threadCreated = remember { mutableStateOf(false) }
    val imageBitmap = remember { mutableStateOf(loadImageBitmap(File("test.bmp").inputStream())) }

    val openFrameNumDialog = remember { mutableStateOf(false) }
    val frameNumberText = remember { mutableStateOf("") }

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
                        while (true) {
                            if (playVideo.value && video.hasNext()) {
                                try {
                                    // if (!playVideo.value) video.seek(video.currentFrame)

                                    val bytes = video.next().encode(".bmp")
                                    imageBitmap.value = loadImageBitmap(bytes.inputStream())
                                } catch (ignored: ConcurrentModificationException) {}
                            } else Thread.sleep(100)
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
            Text(
                video.currentFrame.toString(),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    openFrameNumDialog.value = true
                    frameNumberText.value = video.currentFrame.toString()
                }
            )

            // The slider to adjust the time-stamp
            Slider(
                value = video.currentFrame.toFloat(),
                valueRange = 0.0f..video.totalFrames.toFloat(),
                onValueChange = { // TODO Speed up seeking somehow
                    val initiallyPlaying = playVideo.value

                    // Pause the video
                    playVideo.value = false

                    // Seek to the appropiate location
                    video.seek(it.toInt())

                    val bytes = video.next().encode(".bmp")
                    imageBitmap.value = loadImageBitmap(bytes.inputStream())

                    // Play the video again
                    playVideo.value = initiallyPlaying
                }
            )
        }
    }

    if (openFrameNumDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openFrameNumDialog.value = false
            },
            text = {
                OutlinedTextField(
                    label = { Text("Enter Frame Number") },
                    value = frameNumberText.value,
                    onValueChange = { frameNumberText.value = it },
                    modifier = Modifier.padding(10.dp)
                )
            },
            confirmButton = { TextButton({
                openFrameNumDialog.value = false

                if (frameNumberText.value.toIntOrNull() != null) {
                    if (frameNumberText.value.toInt() < video.totalFrames) {
                        // Seek to the new frame number
                        val initiallyPlaying = playVideo.value

                        // Pause the video
                        playVideo.value = false

                        // Seek to the appropiate location
                        video.seek(frameNumberText.value.toInt())

                        val bytes = video.next().encode(".bmp")
                        imageBitmap.value = loadImageBitmap(bytes.inputStream())

                        // Play the video again
                        playVideo.value = initiallyPlaying
                    } else {
                        println("Frame number should not exceed total number of frames ${video.totalFrames}")
                    }
                } else {
                    println("Frame number should be an integer")
                }
            }) { Text("Confirm") } },
            dismissButton = { TextButton({ openFrameNumDialog.value = false }) { Text("Cancel") } },
        )
    }
}

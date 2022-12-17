package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.Video
import java.io.File
import kotlin.concurrent.timer
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Preview
@Composable
fun VideoPlayer(video: Video, width: MutableState<Dp>, syncing: MutableState<Boolean>) {
    val playVideo = remember { mutableStateOf(false) }
    val pauseSyncing = remember { mutableStateOf(false) }
    val threadCreated = remember { mutableStateOf(false) }
    val imageBitmap = remember { mutableStateOf(loadImageBitmap(File("test.bmp").inputStream())) }

    val sliderValue = remember { mutableStateOf(0) }

    val openFrameNumDialog = remember { mutableStateOf(false) }
    val frameNumberText = remember { mutableStateOf("") }

    val errorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.width(width.value).padding(10.dp), Arrangement.spacedBy(5.dp)) {
        Image(
            imageBitmap.value,
            contentDescription = ""
        )

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            // The play button
            IconButton(onClick = {
                // Switch between play and pause
                playVideo.value = !playVideo.value
                syncing.value = true

                // Check if the thread has been created
                if (!threadCreated.value) {
                    threadCreated.value = true

                    // Create the thread
                    Thread {
                        while (true) {
                            if (!pauseSyncing.value && syncing.value && video.hasNext()) {
                                try {
                                    val ms = measureTimeMillis {
                                        if (!playVideo.value) video.seek(video.currentFrame)

                                        val bytes = video.next().encode(".bmp")
                                        imageBitmap.value = loadImageBitmap(bytes.inputStream())
                                    }

                                    if (ms < 10)
                                        Thread.sleep(10 - ms)
                                } catch (ignored: ConcurrentModificationException) {}
                                catch (exception: Exception) {
                                    playVideo.value = false

                                    errorMessage.value = exception.toString()
                                    errorDialog.value = true

                                    syncing.value = false
                                }
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

                    // TODO Automatically focus on text field when dialog opens
                    // focusRequester.requestFocus()
                }
            )

            // The slider to adjust the time-stamp
            Slider(
                value = video.currentFrame.toFloat(),
                valueRange = 0.0f..video.totalFrames.toFloat(),
                onValueChange = { // TODO Speed up seeking somehow
                    // Stop the video from syncing for a while
                    pauseSyncing.value = true

                    Thread.sleep(100)

                    // Seek to the appropiate location
                    video.seek(it.toInt()) //video.currentFrame)
                    video.hasNext()

                    val bytes = video.next().encode(".bmp")
                    imageBitmap.value = loadImageBitmap(bytes.inputStream())

                    // Start syncing again
                    pauseSyncing.value = false
                }
            )
        }
    }

    fun f() {
        openFrameNumDialog.value = false

        if (frameNumberText.value.toIntOrNull() != null) {
            if (frameNumberText.value.toInt() < video.totalFrames) {
                // Stop the video from syncing for a while
                pauseSyncing.value = true

                Thread.sleep(100)

                // Seek to the appropiate location
                video.seek(frameNumberText.value.toInt())
                video.hasNext()

                val bytes = video.next().encode(".bmp")
                imageBitmap.value = loadImageBitmap(bytes.inputStream())

                // Play the video again
                pauseSyncing.value = false
            } else {
                println("Frame number should not exceed total number of frames ${video.totalFrames}")
            }
        } else {
            println("Frame number should be an integer")
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
                    modifier = Modifier.padding(10.dp).onPreviewKeyEvent {
                        if (it.key == Key.Enter) {
                            f()
                            true
                        } else false
                    }.focusRequester(focusRequester)
                )
            },
            confirmButton = { TextButton({ f() }) { Text("Confirm") } },
            dismissButton = { TextButton({ openFrameNumDialog.value = false }) { Text("Cancel") } },
        )
    }

    if (errorDialog.value) {
        AlertDialog(
            title = { Text("Error") },
            text = { Text(errorMessage.value) },
            confirmButton = {
                TextButton({ errorDialog.value = false }) { Text("Ok") }
            },
            onDismissRequest = { errorDialog.value = false },
            modifier = Modifier.padding(10.dp)
        )
    }
}

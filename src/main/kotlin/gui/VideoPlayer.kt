package gui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import kotlin.random.Random
import kotlin.system.measureTimeMillis

val temp = Random.nextInt(20) != 1

@Composable
fun Pulsating(pulseFraction: Float = 1.2f, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier.scale(scale)) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun VideoPlayer(video: Video, width: MutableState<Dp>, syncing: MutableState<Boolean>, onUpdate: MutableState<Int>) {
    val playVideo = remember { mutableStateOf(false) }
    val pauseSyncing = remember { mutableStateOf(false) }
    val threadCreated = remember { mutableStateOf(false) }
    val imageBitmap = remember { mutableStateOf(loadImageBitmap(File("test.bmp").inputStream())) }

    var openFrameNumDialog by remember { mutableStateOf(false) }
    val frameNumberText = remember { mutableStateOf("") }

    var errorDialog by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.width(width.value).padding(10.dp), Arrangement.spacedBy(5.dp)) {
        Box {
            Image(
                imageBitmap.value,
                contentDescription = ""
            )

            if (video.postprocessors.size > 0 && playVideo.value) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
                    Column {
                        Pulsating(modifier = Modifier.align(Alignment.End)) {
                            TooltipArea(
                                tooltip = {
                                    Surface(
                                        modifier = Modifier.shadow(4.dp),
                                        color = Color(50, 50, 50, 255),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Data is being recorded now! ${
                                                video.postprocessors.map {
                                                    it.data?.rows?.toList()?.size ?: 0
                                                }.sum()
                                            } rows have been recorded.",
                                            fontSize = 8.sp,
                                            modifier = Modifier.padding(5.dp),
                                            color = Color(255, 255, 255)
                                        )
                                    }
                                },
                                modifier = Modifier.padding(start = 0.dp),
                                delayMillis = 600
                            ) {
                                Icon(
                                    Icons.Filled.RadioButtonChecked,
                                    tint = Color.Red,
                                    contentDescription = "Data is being recorded now!",
                                )
                            }
                        }
                    }
                }
            }
        }

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
                            if (!pauseSyncing.value && syncing.value && (!playVideo.value || video.hasNext())) {
                                try {
                                    val ms = measureTimeMillis {
                                        val bytes = video.next(playVideo.value).encode(".bmp")
                                        imageBitmap.value = loadImageBitmap(bytes.inputStream())
                                    }

                                    onUpdate.value = Random.nextInt(100)

                                    if (ms < 10)
                                        Thread.sleep(10 - ms)
                                } catch (ignored: ConcurrentModificationException) {}
                                catch (exception: Exception) {
                                    playVideo.value = false

                                    errorMessage.value = exception.toString()
                                    errorDialog = true

                                    syncing.value = false
                                }
                            } else Thread.sleep(100)
                        }
                    }.start()
                }
            }) {
                if (temp) {
                    Icon(
                        if (playVideo.value) painterResource("pause_black_24dp.svg")
                        else painterResource("play_arrow_black_24dp.svg"),
                        contentDescription = "",
                        tint = if (playVideo.value) Color.Black else Color.Green
                    )
                } else {
                    Icon(
                        if (playVideo.value) painterResource("cd_2.png")
                        else painterResource("cd.png"),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }

            // Show the frame number
            Text(
                video.currentFrame.toString(),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    openFrameNumDialog = true
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
        openFrameNumDialog = false

        if (frameNumberText.value.toIntOrNull() != null) {
            if (frameNumberText.value.toInt() < video.totalFrames) {
                // Stop the video from syncing for a while
                pauseSyncing.value = true

                Thread.sleep(100)

                // Seek to the appropriate location
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

    AnimatedVisibility(
        openFrameNumDialog
    ) {
        AlertDialog(
            onDismissRequest = {
                openFrameNumDialog = false
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
            dismissButton = { TextButton({ openFrameNumDialog = false }) { Text("Cancel") } },
        )
    }

    AnimatedVisibility(
        visible = errorDialog
    ) {
        AlertDialog(
            title = { Text(if (Random.nextInt(50) != 1) "Error" else "Skill Issue") },
            text = { Text(errorMessage.value) },
            confirmButton = {
                TextButton({ errorDialog = false }) { Text("Ok") }
            },
            onDismissRequest = { errorDialog = false },
            modifier = Modifier.padding(10.dp)
        )
    }
}

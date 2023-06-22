package gui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import backend.Video
import org.bytedeco.opencv.global.opencv_videoio
import kotlin.math.roundToInt

@Composable
fun CroppingRectangle(
    dx: State<Double>,
    dy: State<Double>,
    dx2: State<Double>,
    dy2: State<Double>,
    scalingConstant: State<Double>,
    video: Video
) {
    val constant = with(LocalDensity.current) { 1.dp.toPx() }

    Box {
        Divider(
            color = Color.Blue,
            thickness = 2.dp,
            modifier = Modifier
                .width(5.dp)
                .height(5.dp)
                .offset {
                    IntOffset(
                        (constant * 20).roundToInt() + (dx.value / constant).roundToInt(),
                        (constant * 20).roundToInt() + (dy.value / constant).roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val dx = constant * dragAmount.x / scalingConstant.value
                        if ((video.cropX2.value - video.cropX1.value - dx > 10) &&
                            (video.cropX1.value + dx > 0)) {
                            video.cropX1.value += dx
                        }

                        val dy = constant * dragAmount.y / scalingConstant.value
                        if ((video.cropY2.value - video.cropY1.value - dy > 10) &&
                            (video.cropY1.value + dy > 0)) {
                            video.cropY1.value += dy
                        }
                    }
                }
        )

        Divider(
            color = Color.Blue,
            modifier = Modifier
                .height(5.dp)
                .width(5.dp)
                .offset {
                    IntOffset(
                        (constant * 20).roundToInt() + (dx2.value / constant).roundToInt(),
                        (constant * 20).roundToInt() + (dy2.value / constant).roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val dx = constant * dragAmount.x / scalingConstant.value
                        if ((video.cropX2.value + dx < video.videoCapture.get(opencv_videoio.CAP_PROP_FRAME_WIDTH)) &&
                            (video.cropX2.value + dx - video.cropX1.value > 10)) {
                            video.cropX2.value += dx
                        }

                        val dy = constant * dragAmount.y / scalingConstant.value
                        if ((video.cropY2.value + dy < video.videoCapture.get(opencv_videoio.CAP_PROP_FRAME_HEIGHT)) &&
                            (video.cropY2.value + dy - video.cropY1.value > 10)) {
                            video.cropY2.value += dy
                        }
                    }
                }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Red,
                style = Stroke(5f),
                topLeft = Offset(
                    x = (constant * 20).roundToInt() + dx.value.toFloat() / constant,
                    y = (constant * 20).roundToInt() + dy.value.toFloat() / constant
                ),
                size = Size(
                    dx2.value.toFloat() / constant - dx.value.toFloat() / constant,
                    dy2.value.toFloat() / constant - dy.value.toFloat() / constant
                )
            )
        }
    }
}

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
                        video.cropX1.value += constant * dragAmount.x / scalingConstant.value
                        video.cropY1.value += constant * dragAmount.y / scalingConstant.value
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
                        video.cropX2.value += constant * dragAmount.x / scalingConstant.value
                        video.cropY2.value += constant * dragAmount.y / scalingConstant.value
                    }
                }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Red,
                style = Stroke(1f),
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

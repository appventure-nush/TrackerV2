package gui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import backend.Video
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Axes(dx: State<Double>, dy: State<Double>, scalingConstant: State<Double>, video: Video) {
    val constant = with(LocalDensity.current) { 1.dp.toPx() }

    Box {
        Divider(
            color = Color.Red,
            thickness = 2.dp,
            modifier = Modifier
                .width(100000.dp)
                .offset { IntOffset(0, (constant * 20).roundToInt() + (dy.value / constant).roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        video.originX.value += constant * dragAmount.x / scalingConstant.value
                        video.originY.value += constant * dragAmount.y / scalingConstant.value
                    }
                }
        )

        Divider(
            color = Color.Red,
            modifier = Modifier
                .height(100000.dp)
                .width(2.dp)
                .offset { IntOffset((constant * 20).roundToInt() + (dx.value / constant).roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        video.originX.value += constant * dragAmount.x / scalingConstant.value
                        video.originY.value += constant * dragAmount.y / scalingConstant.value
                    }
                }
        )
    }
}

@Composable
fun TapeEnd(x: State<Double>, y: State<Double>,
            videoX: MutableState<Double>, videoY: MutableState<Double>, scalingConstant: State<Double>
) {
    val constant = with(LocalDensity.current) { 1.dp.toPx() }
    Box(
        modifier = Modifier
            .offset { IntOffset(
                (constant * 20).roundToInt() + (x.value / constant).roundToInt(),
                (constant * 20).roundToInt() + (y.value / constant).roundToInt()
            ) }
            .size(5.dp)
            //.clip(CircleShape)
            .background(Color.Green)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    videoX.value += constant * dragAmount.x / scalingConstant.value
                    videoY.value += constant * dragAmount.y / scalingConstant.value
                }
            }
    )
}

@Composable
fun Tape(
    x1: State<Double>,
    y1: State<Double>,
    x2: State<Double>,
    y2: State<Double>,
    videoX1: MutableState<Double>,
    videoY1: MutableState<Double>,
    videoX2: MutableState<Double>,
    videoY2: MutableState<Double>,
    scalingConstant: State<Double>,
    mValue: MutableState<Double>,
    scale: MutableState<Double>
) {
    val constant = with(LocalDensity.current) { 1.dp.toPx() }
    val cmTextValue = remember { mutableStateOf(mValue.value.toString()) }

    scale.value = mValue.value / (
        (x2.value - x1.value).pow(2.0) + (y2.value - y1.value).pow(2.0)
    ).pow(0.5)

    Box {
        TapeEnd(x1, y1, videoX1, videoY1, scalingConstant)
        TapeEnd(x2, y2, videoX2, videoY2, scalingConstant)

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                start = Offset(
                    x = (constant * 20).roundToInt() + x1.value.toFloat() / constant + 2.5f,
                    y = (constant * 20).roundToInt() + y1.value.toFloat() / constant + 2.5f
                ),
                end = Offset(
                    x = (constant * 20).roundToInt() + x2.value.toFloat() / constant + 2.5f,
                    y = (constant * 20).roundToInt() + y2.value.toFloat() / constant + 2.5f
                ),
                color = Color.Green,
                strokeWidth = 2.5f
            )
        }
        
        /*
        Divider(
            color = Color.Green,
            thickness = 2.dp,
            modifier = Modifier
                .offset { IntOffset(
                    (constant * 20).roundToInt() + ((x1.value + x2.value - width) / 2 / constant).roundToInt(),
                    (constant * 20).roundToInt() + ((y1.value + y2.value) / 2 / constant).roundToInt()
                ) }
                .width((width / constant).dp)
                .rotate(toDegrees(atan2(y1.value - y2.value, x1.value - x2.value).toDouble()).toFloat())
        )
        */

        OutlinedTextField(
            value = cmTextValue.value,
            onValueChange = {
                try {
                    cmTextValue.value = it
                    mValue.value = it.toDouble()
                } catch (ignored: Exception) { }
            },
            modifier = Modifier
                .height(50.dp)
                .width(100.dp)
                .offset { IntOffset(
                    (constant * 20).roundToInt() + ((x1.value + x2.value) / 2 / constant).roundToInt(),
                    (constant * 20).roundToInt() + ((y1.value + y2.value) / 2 / constant).roundToInt()
                ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedTextColor = Color.Green,
                focusedTextColor = Color.Green
            )
        )
    }
}

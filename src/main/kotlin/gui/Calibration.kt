package gui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Axes(dx: MutableState<Float>, dy: MutableState<Float>) {
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
                        println("thing")
                        change.consume()
                        dx.value += constant * dragAmount.x
                        dy.value += constant * dragAmount.y
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
                        println("thing")
                        change.consume()
                        dx.value += constant * dragAmount.x
                        dy.value += constant * dragAmount.y
                    }
                }
        )
    }
}

@Composable
fun TapeEnd(x: MutableState<Float>, y: MutableState<Float>) {
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
                    x.value += constant * dragAmount.x
                    y.value += constant * dragAmount.y
                }
            }
    )
}

@Composable
fun Tape(
    x1: MutableState<Float>,
    y1: MutableState<Float>,
    x2: MutableState<Float>,
    y2: MutableState<Float>,
    cmValue: MutableState<Float>,
    scale: MutableState<Double>
) {
    val constant = with(LocalDensity.current) { 1.dp.toPx() }
    val cmTextValue = remember { mutableStateOf("1.0") }

    Box {
        TapeEnd(x1, y1)
        TapeEnd(x2, y2)

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                start = Offset(
                    x = (constant * 20).roundToInt() + x1.value / constant + 2.5f,
                    y = (constant * 20).roundToInt() + y1.value / constant + 2.5f
                ),
                end = Offset(
                    x = (constant * 20).roundToInt() + x2.value / constant + 2.5f,
                    y = (constant * 20).roundToInt() + y2.value / constant + 2.5f
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
                    cmValue.value = it.toFloat()
                    scale.value = (cmValue.value / 100) / (
                        (x2.value - x1.value).toDouble().pow(2.0) + (y2.value - y1.value).toDouble().pow(2.0)
                    ).pow(0.5)
                } catch (ignored: Exception) {

                }
            },
            modifier = Modifier
                .height(50.dp)
                .width(100.dp)
                .offset { IntOffset(
                    (constant * 20).roundToInt() + ((x1.value + x2.value) / 2 / constant).roundToInt(),
                    (constant * 20).roundToInt() + ((y1.value + y2.value) / 2 / constant).roundToInt()
                ) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                textColor = Color.Green
            )
        )
    }

}

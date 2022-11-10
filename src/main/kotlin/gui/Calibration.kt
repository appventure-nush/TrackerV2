package gui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun Axes() {
    val dx = remember { mutableStateOf(0.0f) }
    val dy = remember { mutableStateOf(0.0f) }

    Box {
        Divider(
            color = Color.Red,
            thickness = 2.dp,
            modifier = Modifier
                .width(100000.dp)
                .offset { IntOffset(0, dy.value.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        dx.value += dragAmount.x
                        dy.value += dragAmount.y
                    }
                }
        )

        Divider(
            color = Color.Red,
            modifier = Modifier
                .height(100000.dp)
                .width(2.dp)
                .offset { IntOffset(dx.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        dx.value += dragAmount.x
                        dy.value += dragAmount.y
                    }
                }
        )
    }
}

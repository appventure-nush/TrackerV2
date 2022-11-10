package gui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
                        change.consume()
                        dx.value += constant * dragAmount.x
                        dy.value += constant * dragAmount.y
                    }
                }
        )
    }
}

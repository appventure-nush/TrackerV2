package application.gui.calibration

import com.thepyprogrammer.fxtools.draggable.DraggableNode
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Axes(val x: Double = 0.0, val y: Double = 0.0): DraggableNode() {
    override fun createWidget(): Node {
        val rect = Rectangle(3.0, 10000.0)
        rect.x = 0.0
        rect.y = -5000.0
        rect.fill = Color.MAGENTA

        val rect2 = Rectangle(10000.0, 3.0)
        rect2.x = -5000.0
        rect2.y = 0.0
        rect2.fill = Color.MAGENTA

        return Pane().apply { children.addAll(rect, rect2) }
    }
}
package application.gui.calibration

import com.thepyprogrammer.fxtools.draggable.DraggableNode
import javafx.scene.Node
import javafx.scene.image.ImageView

class CalibrationPoint(val x: Double = 0.0, val y: Double = 0.0): DraggableNode() {
    lateinit var image: ImageView

    override fun createWidget(): Node {
        image = ImageView("/image/outline_close_black_24dp.png")
        image.x = x
        image.y = y
        return image
    }
}
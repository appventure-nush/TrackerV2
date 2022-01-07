package application.gui.calibration

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line

class CalibrationRuler(val pane: Pane) {
    val pointOne = CalibrationPoint()
    val pointTwo = CalibrationPoint()
    val line = Line()
    
    init {
        line.apply {
            startXProperty().bind(pointOne.image.translateXProperty())
            startYProperty().bind(pointOne.image.translateYProperty())
            endXProperty().bind(pointTwo.image.translateXProperty())
            endYProperty().bind(pointTwo.image.translateYProperty())

            strokeWidth = 5.0
            stroke = Color.BLUE
        }

        pane.children.addAll(pointOne, pointTwo, line)
    }

    // Returns the number of metres that 1 pixel represents
    fun updateScale(actualDist: Double): Double {
        val dist = (pointTwo.nodeX - pointOne.nodeX) * (pointTwo.nodeX - pointOne.nodeX) +
                (pointTwo.nodeY - pointOne.nodeY) * (pointTwo.nodeY - pointOne.nodeY)

        return actualDist / dist  // TODO (Account for canvas zoom)
    }
}
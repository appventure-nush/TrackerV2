package application.wrappers.generic

import application.backend.Processing
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.shape.Circle
import javafx.scene.text.Font

class HelpButton(processing: Processing): Button("?") {
    init {
        val r = 15.0
        shape = Circle(r)
        setMinSize(2 * r, 2 * r)
        setMaxSize(2 * r, 2 * r)

        style = "-fx-font-weight: bold"
        this.tooltip = Tooltip(processing.help)
    }
}
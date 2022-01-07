package application.wrappers.generic

import application.backend.Processing
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.shape.Circle

class HelpButton(processing: Processing): Button("?") {
    init {
        val r = 1.5
        shape = Circle(r)
        setMinSize(2 * r, 2 * r)
        setMaxSize(2 * r, 2 * r)
        this.tooltip = Tooltip(processing.help)
    }

}
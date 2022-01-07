package application.wrappers.generic

import application.backend.Processing
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Tooltip
import javafx.scene.shape.Circle


class HelpButton(processing: Processing): Button("?") {
    val dialog = Dialog<String>().apply {
        title = "Help with ${processing.name}"
        contentText = processing.help
        dialogPane.buttonTypes.add(ButtonType("OK", ButtonData.OK_DONE))
    }

    init {
        val r = 15.0
        shape = Circle(r)
        setMinSize(2 * r, 2 * r)
        setMaxSize(2 * r, 2 * r)

        style = "-fx-font-weight: bold"
        tooltip = Tooltip(processing.help)

        onAction = EventHandler { dialog.showAndWait() }
    }
}
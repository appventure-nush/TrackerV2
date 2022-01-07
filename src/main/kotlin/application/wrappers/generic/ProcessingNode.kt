package application.wrappers.generic

import application.backend.Colourspace
import application.backend.Processing
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.text.Font

abstract class ProcessingNode(val node: Processing): AnchorPane() {
    val helpButton = HelpButton(node)

    val colourspaceCombobox = ComboBox<Colourspace>().apply {
        items = FXCollections.observableList(node.inputColourspaces)
        valueProperty().addListener { _, _, new -> node.inputColourspace = new }
        // promptText = "Select Colourspace"
        selectionModel.selectFirst();
    }

    init {
        // Set style :)
        style = "-fx-hgap: 20px; -fx-padding: 10px; -fx-background-radius: 5px;" +
                "-fx-background-color: #eeeeee; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);"

        // application.Main anchor pane
        // val menu = this
        // HBox for top-left
        children.addAll(
            HBox(8.0).apply {
                children.add(Label(node.name).apply {
                    font = Font(15.0)
                    style = "-fx-font-weight: bold"
                })
                children.add(helpButton)

                setTopAnchor(this, 0.0)
                setLeftAnchor(this, 0.0)
            },
            // ComboxBox for top-right
            colourspaceCombobox.apply {
                setTopAnchor(this, 0.0)
                setRightAnchor(this, 0.0)
            }
        )
    }
}
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

    abstract fun operationMenu(menu: AnchorPane)

    fun createWidget(): Node {
        // application.Main anchor pane
        val menu = this

        // HBox for top-left
        menu.children.addAll(
            HBox(8.0).apply {
                children.add(Label(node.name).apply {
                    font = Font(20.0)
                    style = "-fx-font-weight: bold"
                })
                children.add(helpButton)

                setTopAnchor(this, 20.0)
                setLeftAnchor(this, 20.0)
            },
            // ComboxBox for top-right
            colourspaceCombobox.apply {
                setTopAnchor(this, 20.0)
                setRightAnchor(this, 20.0)
            }
        )

        // Add in options
        operationMenu(menu)
        return menu
    }
}
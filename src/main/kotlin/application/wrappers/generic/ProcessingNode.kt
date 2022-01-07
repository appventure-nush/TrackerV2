package application.wrappers.generic

import application.backend.Colourspace
import application.backend.Processing
import com.thepyprogrammer.fxtools.draggable.DraggableNode
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Font

abstract class ProcessingNode(val node: Processing): AnchorPane() {
    val helpButton = HelpButton(node)
    val colourspaceCombobox = ComboBox<Colourspace>()

    init {
        colourspaceCombobox.items = FXCollections.observableList(node.inputColourspaces)
        colourspaceCombobox.valueProperty().addListener { _, _, new -> node.inputColourspace = new }
    }

    abstract fun operationMenu(menu: AnchorPane)

    fun createWidget(): Node {
        // application.Main anchor pane
        val menu = this

        // HBox for top-left
        menu.children.add(HBox(8.0).apply {
            children.add(Label(node.name).apply {
                font = Font(20.0)
                style = "-fx-font-weight: bold"
            })
            children.add(helpButton)


            AnchorPane.setTopAnchor(this, 20.0)
            AnchorPane.setLeftAnchor(this, 20.0)
        })

        // ComboxBox for top-right
        menu.children.add(colourspaceCombobox.apply {
            AnchorPane.setTopAnchor(this, 20.0)
            AnchorPane.setRightAnchor(this, 20.0)
        })

        // Add in options
        operationMenu(menu)
        return menu
    }
}
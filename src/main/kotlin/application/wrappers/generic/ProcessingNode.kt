package application.wrappers.generic

import application.backend.Colourspace
import application.backend.Processing
import com.thepyprogrammer.fxtools.draggable.DraggableNode
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.ComboBox

abstract class ProcessingNode(val node: Processing): DraggableNode() {
    abstract fun operationMenu(helpButton: HelpButton): Node

    val helpButton = HelpButton(node)
    val colourspaceButton = ComboBox<Colourspace>()

    init {
        colourspaceButton.items = FXCollections.observableList(node.inputColourspaces)
        colourspaceButton.valueProperty().addListener { _, _, new -> node.inputColourspace = new }
    }

    override fun createWidget(): Node {
        val menu = operationMenu(helpButton)
        return RoundedPane(menu)
    }
}
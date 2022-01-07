package application.wrappers.generic

import application.backend.Processing
import com.thepyprogrammer.fxtools.draggable.DraggableNode
import javafx.scene.Node

abstract class ProcessingNode(processing: Processing): DraggableNode() {
    abstract fun operationMenu(helpButton: HelpButton): Node

    val helpButton = HelpButton(processing)

    override fun createWidget(): Node {
        val menu = operationMenu(helpButton)
        return RoundedPane(menu)
    }
}
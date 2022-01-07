package application.gui

import application.backend.Preprocessor
import application.backend.preprocess.PreprocessingNode
import application.gui.preprocessing.BlurringPane
import application.gui.preprocessing.CannyEdgePane
import application.gui.preprocessing.ColourRangePane
import application.gui.preprocessing.ThresholdingPane
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

class NodesPane: ScrollPane() {
    val hbox: HBox = HBox(20.0)
    val nodes: ArrayList<PreprocessingPane> = arrayListOf()

    val preprocessor: Preprocessor
        get() {
            val processor = Preprocessor()
            processor.nodes.addAll(nodes.map { it.node as PreprocessingNode })

            return processor
        }

    init {
        content = hbox
        hbox.prefHeightProperty().bind(heightProperty())

        contextMenu = ContextMenu().apply {
            items.add(MenuItem("Add Blur").apply {
                onAction = EventHandler { addNode(BlurringPane().apply {
                    deleteButton.onAction = EventHandler { deleteNode(this) }
                }) }
            })
            items.add(MenuItem("Add Thresholding").apply {
                onAction = EventHandler { addNode(ThresholdingPane().apply {
                    deleteButton.onAction = EventHandler { deleteNode(this) }
                }) }
            })
            items.add(MenuItem("Add Colour Filter").apply {
                onAction = EventHandler { addNode(ColourRangePane().apply {
                    deleteButton.onAction = EventHandler { deleteNode(this) }
                }) }
            })
            items.add(MenuItem("Add Edge Detection").apply {
                onAction = EventHandler { addNode(CannyEdgePane().apply {
                    deleteButton.onAction = EventHandler { deleteNode(this) }
                }) }
            })
        }
    }

    private fun deleteNode(node: PreprocessingPane) {
        val index = nodes.find { it === node }!!
        nodes.remove(index)
        Platform.runLater { hbox.children.remove(index) }
    }

    private fun addNode(node: PreprocessingPane) {
        Platform.runLater { hbox.children.add(node) }
        nodes.add(node)
    }
}
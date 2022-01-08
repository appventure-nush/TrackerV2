package application.gui

import application.backend.Postprocessor
import application.backend.Preprocessor
import application.backend.postprocess.PostprocessingNode
import application.backend.postprocess.fitting.EllipseFittingNode
import application.backend.preprocess.PreprocessingNode
import application.gui.postprocessing.CirclePane
import application.gui.postprocessing.EllipsePane
import application.gui.preprocessing.BlurringPane
import application.gui.preprocessing.CannyEdgePane
import application.gui.preprocessing.ColourRangePane
import application.gui.preprocessing.ThresholdingPane
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import java.awt.Dialog

class NodesPane: ScrollPane() {
    val hbox: HBox = HBox(20.0)
    val nodes: ArrayList<PreprocessingPane> = arrayListOf()
    var outputNode: PostprocessingPane? = null

    val preprocessor: Preprocessor
        get() {
            val processor = Preprocessor()
            processor.nodes.addAll(nodes.map { it.node as PreprocessingNode })
            return processor
        }

    val postprocessor: Postprocessor?
    get() {
        if(outputNode != null) return Postprocessor(outputNode!!.node as PostprocessingNode)
        else return null
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
            items.add(MenuItem("Add Ellipse Fitting").apply {
                onAction = EventHandler { setOutputPane(EllipsePane().apply {
                    deleteButton.onAction = EventHandler { deleteOutputNode(this) }
                }) }
            })
            items.add(MenuItem("Add Circle Fitting").apply {
                onAction = EventHandler { setOutputPane(CirclePane().apply {
                    deleteButton.onAction = EventHandler { deleteOutputNode(this) }
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
        Platform.runLater {
            if (outputNode == null) hbox.children.add(node)
            else hbox.children.add(hbox.children.size - 2, node)
        }

        nodes.add(node)
    }

    private fun setOutputPane(node: PostprocessingPane) {
        if (outputNode == null) {
            Platform.runLater { hbox.children.add(node) }
            outputNode = node
        } else {
            Alert(Alert.AlertType.ERROR).apply {
                title = "Error!"
                headerText = "Cannot have more an 1 output node!"
                contentText = "Cannot have more an 1 output node!"
            }.show()
        }
    }

    private fun deleteOutputNode(node: PostprocessingPane) {
        Platform.runLater { hbox.children.remove(node) }
        outputNode = null
    }
}
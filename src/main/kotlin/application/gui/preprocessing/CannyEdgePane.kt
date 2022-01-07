package application.gui.preprocessing

import application.backend.preprocess.edge_detection.CannyEdgeNode
import application.gui.PreprocessingPane
import application.wrappers.generic.ProcessingNode
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class CannyEdgePane: PreprocessingPane(CannyEdgeNode()) {
    val kernelSizeSlider = Slider(3.0, 53.0, 3.0)
    val thresholdSlider = Slider(0.0, 255.0, 1.0)

    init {
        children.add(VBox(10.0).apply {
            // Setup kernel size slider
            children.add(HBox(10.0).apply {
                children.add(Label("Kernel Size:"))
                children.add(kernelSizeSlider.apply {
                    tooltip = Tooltip("Controls the kernel size of the sorbel kernel used for edge detection")
                    valueProperty().addListener { _, _, new -> (node as CannyEdgeNode).kernelSize = new.toInt() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 10.0
                    blockIncrement = 2.0
                })
            })

            // Setup threshold slider
            children.add(HBox(10.0).apply {
                children.add(Label("Minimum threshold:"))
                children.add(thresholdSlider.apply {
                    tooltip = Tooltip("The minimum threshold")
                    valueProperty().addListener { _, _, new -> (node as CannyEdgeNode).threshold = new.toDouble() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 10.0
                    blockIncrement = 2.0
                })
            })

            // Set AnchorPane location
            setBottomAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
        })
    }
}
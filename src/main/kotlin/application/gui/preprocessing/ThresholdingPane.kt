package application.gui.preprocessing

import application.backend.preprocess.masking.ThresholdingNode
import application.gui.PreprocessingPane
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class ThresholdingPane: PreprocessingPane(ThresholdingNode(0.0, 255.0, false)) {
    val minThresholdSlider = Slider(0.0, 255.0, 1.0)
    val maxThresholdSlider = Slider(0.0, 255.0, 1.0)
    val binariseCheckbox = CheckBox("Binarise")

    init {
        children.add(VBox(10.0).apply {
            // Checkbox
            children.add(binariseCheckbox.apply {
                selectedProperty().addListener { _, _, new -> (node as ThresholdingNode).binarise = new }
            })

            // Setup first slider
            children.add(HBox(10.0).apply {
                children.add(Label("Minimum threshold:"))
                children.add(minThresholdSlider.apply {
                    tooltip = Tooltip("The minimum threshold")
                    valueProperty().addListener { _, _, new -> (node as ThresholdingNode).minThreshold = new.toDouble() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 10.0
                    blockIncrement = 2.0
                })
            })

            // Setup second slider
            children.add(HBox(10.0).apply {
                children.add(Label("Maximum threshold:"))
                children.add(maxThresholdSlider.apply {
                    tooltip = Tooltip("The maximum threshold")
                    valueProperty().addListener { _, _, new -> (node as ThresholdingNode).maxThreshold = new.toDouble() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 10.0
                    blockIncrement = 2.0
                })
            })

            // Set AnchorPane location
            AnchorPane.setBottomAnchor(this, 0.0)
            AnchorPane.setLeftAnchor(this, 0.0)
        })
    }
}
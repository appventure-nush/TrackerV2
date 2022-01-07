package application.gui.postprocessing

import application.backend.postprocess.fitting.CircleFittingNode
import application.backend.preprocess.blurring.Blurring
import application.backend.preprocess.blurring.BlurringNode
import application.gui.PostprocessingPane
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class CirclePane: PostprocessingPane(CircleFittingNode()) {
    val minRadiusField = TextField()
    val maxRadiusField = TextField()
    val minDistSlider = Slider(0.0, 100.0, 20.0)
    val param1Slider = Slider(0.0, 400.0, 100.0)
    val param2Slider = Slider(0.0, 400.0, 100.0)

    init {
        children.add(VBox(10.0).apply {
            children.add(HBox(10.0).apply {
                children.add(Label("Minimum Radius:"))
                children.add(minRadiusField.apply {
                    tooltip = Tooltip("Minimum radius of the circle")
                    textProperty().addListener { _, _, new ->
                        try {
                            (node as CircleFittingNode).minRadius = new.toInt()
                        } catch (_: NumberFormatException) {
                        }
                    }
                })
            })

            children.add(HBox(10.0).apply {
                children.add(Label("Maximum Radius:"))
                children.add(maxRadiusField.apply {
                    tooltip = Tooltip("Maximum radius of the circle")
                    textProperty().addListener { _, _, new ->
                        try {
                            (node as CircleFittingNode).maxRadius = new.toInt()
                        } catch (_: NumberFormatException) {
                        }
                    }
                })
            })

            children.add(HBox(10.0).apply {
                children.add(Label("Minimum Distance:"))
                children.add(minDistSlider.apply {
                    tooltip = Tooltip("Controls the minimum distance between 2 circles")
                    valueProperty().addListener { _, _, new -> (node as CircleFittingNode).minDist = new.toDouble() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 20.0
                })
            })

            children.add(HBox(10.0).apply {
                children.add(Label("Edge Detection:"))
                children.add(param1Slider.apply {
                    tooltip = Tooltip("For edge detection")
                    valueProperty().addListener { _, _, new -> (node as CircleFittingNode).param1 = new.toDouble() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 100.0
                })
            })

            children.add(HBox(10.0).apply {
                children.add(Label("Circle Standards:"))
                children.add(param2Slider.apply {
                    tooltip = Tooltip("Controls how circular a circle has to be to be a circle")
                    valueProperty().addListener { _, _, new -> (node as CircleFittingNode).param2 = new.toDouble() }

                    isShowTickMarks = true
                    isShowTickLabels = true
                    majorTickUnit = 100.0
                })
            })

            // Set AnchorPane location
            setBottomAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
        })
    }
}
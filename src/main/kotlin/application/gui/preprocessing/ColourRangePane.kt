package application.gui.preprocessing

import application.backend.preprocess.masking.ColourRangeNode
import application.gui.PreprocessingPane
import javafx.scene.control.CheckBox
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class ColourRangePane: PreprocessingPane(ColourRangeNode(arrayListOf(Pair(Color.rgb(0, 0, 0),
    Color.rgb(255, 255, 255))), true)) {
    val minColour = ColorPicker()
    val maxColour = ColorPicker()
    val binariseCheckbox = CheckBox("Binarise")

    init {
        children.add(VBox(10.0).apply {
            // Checkbox
            children.add(binariseCheckbox.apply {
                selectedProperty().addListener { _, _, new -> (node as ColourRangeNode).binarise = new }
            })

            // Setup first picker
            children.add(HBox(10.0).apply {
                children.add(Label("Minimum colour:"))
                children.add(minColour.apply {
                    tooltip = Tooltip("The lower bound of the colour range for filtering")
                    valueProperty().addListener { _, _, new ->
                        (node as ColourRangeNode).colours[0] = Pair(new, node.colours[0].second)
                    }
                })
            })

            // Setup second picker
            children.add(HBox(10.0).apply {
                children.add(Label("Maximum threshold:"))
                children.add(maxColour.apply {
                    tooltip = Tooltip("The higher bound of the colour range for filtering")
                    valueProperty().addListener { _, _, new ->
                        (node as ColourRangeNode).colours[0] = Pair(node.colours[0].first, new)
                    }
                })
            })

            // Set AnchorPane location
            AnchorPane.setBottomAnchor(this, 0.0)
            AnchorPane.setLeftAnchor(this, 0.0)
        })
    }
}
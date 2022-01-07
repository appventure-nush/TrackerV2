package application.gui.preprocessing

import application.backend.preprocess.blurring.Blurring
import application.backend.preprocess.blurring.BlurringNode
import application.gui.PreprocessingPane
import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox

class BlurringPane: PreprocessingPane(BlurringNode()) {
    val kernelSizeSlider = Slider(3.0, 53.0, 3.0)
    val blurringComboBox = ComboBox<Blurring>()

    init {
        children.add(VBox(10.0).apply {
            // Setup slider
            children.add(kernelSizeSlider.apply {
                tooltip = Tooltip("Controls the extent of blurring")
                isShowTickMarks = true
                isShowTickLabels = true
                majorTickUnit = 10.0
                blockIncrement = 2.0
            })

            // Setup combobox
            children.add(blurringComboBox.apply {
                items = FXCollections.observableList(listOf(Blurring.GAUSSIAN, Blurring.MEDIAN, Blurring.BOX_FILTER))
                valueProperty().addListener { _, _, new -> (node as BlurringNode).blurType = new }
                selectionModel.selectFirst();
                tooltip = Tooltip("Controls the type of blurring")
            })

            // Set AnchorPane location
            setBottomAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
        })
    }
}
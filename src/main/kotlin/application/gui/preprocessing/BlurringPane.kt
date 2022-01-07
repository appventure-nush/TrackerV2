package application.gui.preprocessing

import application.backend.preprocess.blurring.BlurringNode
import application.gui.PreprocessingPane
import javafx.scene.Node
import javafx.scene.layout.AnchorPane

class BlurringPane: PreprocessingPane(BlurringNode()) {
    override fun operationMenu(menu: AnchorPane) {
    }
}
package application

import application.backend.preprocess.edge_detection.CannyEdgeNode
import application.gui.preprocessing.BlurringPane
import application.gui.preprocessing.CannyEdgePane
import application.gui.preprocessing.ColourRangePane
import application.gui.preprocessing.ThresholdingPane
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun main(args: Array<String>) {
    launch(Main::class.java)
}

class Main : Application() {
    override fun start(primaryStage: Stage) {
        val layout = CannyEdgePane()

        primaryStage.run {
            scene = Scene(layout, 300.0, 200.0)
            show()
        }
    }
}
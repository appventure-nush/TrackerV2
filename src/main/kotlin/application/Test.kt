package application

import application.gui.calibration.Axes
import application.gui.calibration.CalibrationRuler
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

fun main(args: Array<String>) {
    launch(Test::class.java)
}

class Test : Application() {
    override fun start(primaryStage: Stage) {
        val root = Pane()

        val ruler = CalibrationRuler(root)
        //root.children.add(Axes())
        primaryStage.run {
            scene = Scene(root, 300.0, 200.0)
            show()
        }
    }
}
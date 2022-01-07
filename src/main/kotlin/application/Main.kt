package application

import application.gui.preprocessing.BlurringPane
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun main(args: Array<String>) {
    launch(Main::class.java)
}

class Main : Application() {
    override fun start(primaryStage: Stage) {
        val layout = VBox().apply {
            children.add(BlurringPane().apply { createWidget() })
        }

        primaryStage.run {
            scene = Scene(layout, 500.0, 500.0)
            show()
        }
    }
}
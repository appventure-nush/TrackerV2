package application

import application.gui.NodesPane
import application.gui.postprocessing.CirclePane
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.stage.Stage

fun main(args: Array<String>) {
    launch(Main::class.java)
}

class Main : Application() {
    override fun start(primaryStage: Stage) {
        val layout = NodesPane()

        primaryStage.run {
            scene = Scene(layout, 300.0, 200.0)
            show()
        }
    }
}
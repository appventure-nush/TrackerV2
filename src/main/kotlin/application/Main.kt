package application

import application.gui.NodesPane
import application.gui.postprocessing.CirclePane
import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

fun main(args: Array<String>) {
    launch(Main::class.java)
}

class Main : Application() {
    override fun start(primaryStage: Stage) {
        //val layout = NodesPane()
        val root = FXMLLoader.load<AnchorPane>(javaClass.getResource("/main.fxml"))
        primaryStage.run {
            scene = Scene(root, 300.0, 200.0)
            show()
        }
    }
}
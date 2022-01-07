package application.gui

import application.Main
import application.model.MediaControl
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.net.URL
import java.util.*

class MainController: Initializable {
    @FXML lateinit var pane: Pane
    @FXML lateinit var nodesPane: NodesPane


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        pane.children.add(createContent())
    }

    fun createContent(): Parent {
        val mediaPlayer = MediaPlayer(
            Media(
                Objects.requireNonNull(
                    Main::class.java.getResource("/video/Untitledd.mp4")
                ).toExternalForm()
            )
        )
        mediaPlayer.isAutoPlay = true

        val mediaControl = MediaControl(mediaPlayer)
        mediaControl.setMinSize(800.0, 467.0)
        mediaControl.setPrefSize(800.0, 467.0)
        mediaControl.setMaxSize(800.0, 467.0)

        val box = VBox(mediaControl)
        box.isFillWidth = true

        return mediaControl
    }
}
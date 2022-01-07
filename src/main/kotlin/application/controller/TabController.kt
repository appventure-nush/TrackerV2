package application.controller

import application.Main
import application.gui.NodesPane
import application.model.MediaControl
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.net.URL
import java.util.*

class TabController: Initializable {
    companion object {
        var panes = HashMap<VBox, TabController>()

        fun getController(node: Node?): TabController? {
            return if (panes.containsKey(node)) panes[node] else null
        }
    }

    @FXML lateinit var pane: Pane
    @FXML lateinit var nodesPane: NodesPane

    lateinit var resource: String

    lateinit var media: Parent



    override fun initialize(location: URL?, resources: ResourceBundle?) {
        resource = Objects.requireNonNull(
            Main::class.java.getResource("/video/Untitledd.mp4")
        ).toExternalForm()

        media = createContent()
        pane.children.add(media)
    }

    fun setFile(filename: String) {
        resource = filename
        pane.children.remove(media)
        media = createContent()
        pane.children.add(media)
    }

    fun createContent(): Parent {
        val mediaPlayer = MediaPlayer(Media(resource)).apply { isAutoPlay = true }
        val mediaControl = MediaControl(mediaPlayer)
        mediaControl.setMinSize(800.0, 467.0)
        mediaControl.setPrefSize(800.0, 467.0)
        mediaControl.setMaxSize(800.0, 467.0)
        return VBox(mediaControl).apply { isFillWidth = true }
    }
}
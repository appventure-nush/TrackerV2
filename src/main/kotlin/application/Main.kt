package application

import application.splash.Splash
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.application.Application.launch
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import java.io.IOException
import java.util.*
import com.thepyprogrammer.fxtools.resizable.*

fun main(args: Array<String>) {
    launch(Main::class.java)
}

class Main : Application() {
    private lateinit var splash: Splash
    private var xOffset = 0.0
    private var yOffset = 0.0

    override fun start(stage: Stage) {
        Companion.stage = stage
        stage.initStyle(StageStyle.UNDECORATED)
        icon = Image(
            Objects.requireNonNull(Main::class.java.getResource("/images/icons/phyton.png")).toExternalForm()
        )
        stage.icons.add(icon)
        splash()
        stage.show()
    }
    fun splash() {
        splash = Splash()
        splash.show()
        stage.scene = Splash.splashScene
        Splash.splashScene?.stylesheets?.add(
            Objects.requireNonNull(Main::class.java.getResource("/stylesheets/splash.css")).toExternalForm()
        )
        splash.progresser.onFinished = EventHandler { ex: ActionEvent? -> endSplash(ex) }
    }

    fun endSplash(ex: ActionEvent?) {
        val timeline = Timeline()
        val key = KeyFrame(
            Duration.millis(1600.0),
            KeyValue(Splash.splashScene?.root?.opacityProperty(), 0)
        )
        timeline.keyFrames.add(key)
        timeline.onFinished = EventHandler { e: ActionEvent? -> loadFXML() }
        timeline.play()
    }

    fun loadFXML() {
            val root = FXMLLoader.load<Parent>(Objects.requireNonNull(javaClass.getResource("/mainframe.fxml")))
            root.onMousePressed = EventHandler { event: MouseEvent ->
                xOffset = event.sceneX
                yOffset = event.sceneY
            }
            root.onMouseDragged = EventHandler { event: MouseEvent ->
                stage.x = event.screenX - xOffset
                stage.y = event.screenY - yOffset
            }
            val scene = Scene(root)
            scene.stylesheets.add(Objects.requireNonNull(Main::class.java.getResource("/stylesheets/style.css")).toExternalForm())
            stage.scene = scene
            stage.minHeight = (root as VBox).minHeight
            stage.minWidth = root.minWidth
            stage.title = "tracker2.workspace"
            fullScreen()
            addResizeListener(stage)
    }

    companion object {
        @JvmStatic
        lateinit var stage: Stage
            private set
        @JvmStatic
        var icon: Image? = null
            private set

        fun fullScreen() {
            val screen: Screen = Screen.getPrimary()
            val bounds: Rectangle2D = screen.visualBounds
            stage.x = bounds.minX
            stage.y = bounds.minY
            stage.width = bounds.width
            stage.height = bounds.height
        }
    }
}
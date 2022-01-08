package application.controller

import application.Main
import application.gui.NodesPane
import application.model.MediaControl
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import org.apache.poi.ss.formula.functions.NumericFunction.LOG
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.JavaFXFrameConverter
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ShortBuffer
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine


class TabController: Initializable {
    companion object {
        var panes = HashMap<VBox, TabController>()

        fun getController(node: Node?): TabController? {
            return if (panes.containsKey(node)) panes[node] else null
        }
    }

    @FXML lateinit var pane: Pane
    @FXML lateinit var nodesPane: NodesPane
    @FXML lateinit var imageView: ImageView

    lateinit var resource: String

    // lateinit var media: Parent

    lateinit var playThread: Thread

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        resource = Objects.requireNonNull(Main::class.java.getResource("/video/Untitledd.mp4")).toExternalForm()
        play()
        // media = createContent()
        // pane.children.add(media)
    }

    fun setFile(filename: String) {
        resource = filename
//        Platform.runLater {
//            //pane.children.remove(media)
//            //media = createContent()
//            //pane.children.add(media)
//        }

        playThread.interrupt()
        play()

    }

    fun play() {
        playThread = Thread {
            val grabber = FFmpegFrameGrabber(resource)
            grabber.start()
            grabber.frameRate = 30.00
            val converter = JavaFXFrameConverter()
            val executor = Executors.newSingleThreadExecutor()
            while (!Thread.interrupted()) {
                val frame = grabber.grab() ?: break
                if (frame.image != null) Platform.runLater { imageView.setImage(converter.convert(frame)) }
                else if (frame.samples != null) {
                    val channelSamplesShortBuffer = frame.samples[0] as ShortBuffer
                    channelSamplesShortBuffer.rewind()
                    val outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2)
                    for (i in 0 until channelSamplesShortBuffer.capacity()) outBuffer.putShort(channelSamplesShortBuffer[i])
                    try {
                        executor.execute { outBuffer.clear() }
                    } catch (interruptedException: java.lang.Exception) {
                        Thread.currentThread().interrupt()
                    }
                }
            }
            executor.shutdownNow()
            executor.awaitTermination(10, TimeUnit.SECONDS)
            grabber.stop()
            grabber.release()
            Platform.exit()

        }
        playThread.start()
    }

    fun createContent(): Parent {
        val mediaPlayer = MediaPlayer(Media(resource)).apply { isAutoPlay = true }
        val mediaControl = MediaControl(mediaPlayer)
        mediaControl.setMinSize(800.0, 467.0)
        mediaControl.setPrefSize(800.0, 467.0)
        mediaControl.setMaxSize(800.0, 467.0)
        return mediaControl
    }
}
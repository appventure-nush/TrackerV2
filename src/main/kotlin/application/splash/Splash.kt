package application.splash

import application.Main
import java.util.Objects
import javafx.scene.layout.Pane
import javafx.scene.control.ProgressBar
import kotlin.Throws
import java.io.FileNotFoundException
import javafx.animation.*
import javafx.scene.layout.VBox
import javafx.scene.Scene
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.util.Duration
import kotlin.math.abs

class Splash {
    val IMAGE_URL =
        Objects.requireNonNull(Main::class.java.getResource("/images/lightning.gif")).toExternalForm()
    private val seqT = SequentialTransition()
    val progresser = SequentialTransition()
    private val fillerT = SequentialTransition()
    val iv = ImageView(Image(IMAGE_URL)).apply {
        fitWidth = 400.0
        fitHeight = 300.0
        x = 300.0
        y = 0.0
    }
    var scale = 30
    var dur = 800
    private var pane = Pane().apply { style = "-fx-background-color:black" }
    private val loadProgress = ProgressBar().apply { prefWidth = 800.0 }
    private var progressText = Label("Generating phyton.workspace...").apply { font = Font("System", 13.0) }
    private var splashLayout = VBox(loadProgress, progressText).apply {
        effect = DropShadow()
        style = "-fx-border-radius: 5"
        layoutX = 0.0
        layoutY = 240.0
        opaqueInsets = Insets(10.0)
    }
    private val label = Label("phyton").apply {
        font = Font("Verdana", 40.0)
        style = "-fx-text-fill:white"
        layoutX = 140.0
        layoutY = 70.0
    }


    @Throws(FileNotFoundException::class)
    fun init() {
        rect = Rectangle((100 - 2 * scale).toDouble(), 20.0, scale.toDouble(), scale.toDouble()).apply { fill = Color.BLACK }
        progressText.alignment = Pos.CENTER
        pane.children.addAll(rect, label, iv, splashLayout)
    }

    @Throws(FileNotFoundException::class)
    fun show() {
        init()
        fillerT.children.addAll(
            FillTransition(Duration.millis(2000.0), rect, Color.BLACK, Color.CORNFLOWERBLUE),
            FillTransition(Duration.millis(2000.0), rect, Color.CORNFLOWERBLUE, Color.DEEPSKYBLUE),
            FillTransition(Duration.millis(2000.0), rect, Color.DEEPSKYBLUE, Color.MIDNIGHTBLUE),
            FillTransition(Duration.millis(2000.0), rect, Color.MIDNIGHTBLUE, Color.BLACK)
        )
        fillerT.play()
        progresser.children.add(object : Transition() {
            override fun interpolate(frac: Double) {
                loadProgress.progress = frac
                progressText.text = texts[(frac * (texts.size - 1)).toInt()]
            }

            init {
                cycleDuration = Duration.millis(8000.0)
            }
        })
        progresser.play()
        val rotins = intArrayOf(
            scale,
            2 * scale,
            3 * scale,
            4 * scale,
            5 * scale,
            -6 * scale,
            -5 * scale,
            -4 * scale,
            -3 * scale,
            -2 * scale
        )
        var x: Int
        var y: Int
        for (i in rotins) {
            val rt = RotateTransition(Duration.millis(dur.toDouble()), rect)
            rt.byAngle = (i / abs(i) * 90).toDouble()
            rt.cycleCount = 1
            val pt = TranslateTransition(Duration.millis(dur.toDouble()), rect)
            x = (rect.x + abs(i)).toInt()
            y = (rect.x + abs(i) + abs(i) / i * scale).toInt()
            pt.fromX = x.toDouble()
            pt.toX = y.toDouble()
            val pat = ParallelTransition()
            pat.children.addAll(pt, rt)
            pat.cycleCount = 1
            seqT.children.add(pat)
        }
        seqT.node = rect
        seqT.play()
    }

    companion object {
        var splashScene: Scene? = null;
        var rect = Rectangle()
        private val texts = FXCollections.observableArrayList(
            "Implementing MVC Framework...",
            "Creating 100+ Files...",
            "Reading Java API...",
            "Importing *...",
            "Generating classes...",
            "Generating interfaces...",
            "Generating enumerations...",
            "Extending classes...",
            "Implementing interfaces...",
            "Setting preferences...",
            "Initializing Current Flow...",
            "Introducing models to environment...",
            "Creating power sources...",
            "Creating components...",
            "Creating practical components...",
            "Catching all Pokemon...",
            "Parsing i18N.java...",
            "Translating into multiple languages...",
            "Seeking damages...",
            "Formatting Menubar...",
            "Creating JavaFX Controls...",
            "Initializing Drag and Drop...",
            "Making phyton.canvas...",
            "Deriving images...",
            "Finding logo...",
            "Generating logo...",
            "Loading FXML Files...",
            "Generating mainframe.fxml...",
            "Searching for JavaFX Widgets...",
            "Generating GUI...",
            "Generating About The Programmer page...",
            "Generating About phyton page...",
            "Generating autocomplete combobox...",
            "Generating sidebar...",
            "Styling the window...",
            "Initializing MainframeController...",
            "Reading phyton source...",
            "Calculating resistance...",
            "Restarting phyton...",
            "Parsing .exml files...",
            "Generating electrons...",
            "Annihilating fake electrons...",
            "Searching games...",
            "Failing in generating games...",
            "Finding other possible components...",
            "Threading everything together...",
            "Forming simulator...",
            "Reading everything...",
            "Ignoring magnetism...",
            "Excusing Quantum Physics for this session...",
            "Trying to understand PO...",
            "Failing to understand PO...",
            "Ignoring PO...",
            "Preparing Environment...",
            "Finally loading phyton.workspace...",
            "Completed."
        )
    }

    init {
        splashScene = Scene(pane)
    }
}
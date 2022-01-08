package application.model

import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.math.abs
import kotlin.math.roundToInt


open class MediaControl(val mp: MediaPlayer): BorderPane() {
    var mediaView: MediaView = MediaView(mp)
    val repeat = false
    var stopRequested = false
    var atEndOfMedia = false
    var duration: Duration? = null
    var timeSlider = Slider().apply {
        minWidth = 30.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.ALWAYS)
        valueProperty().addListener { _: ObservableValue<out Number>?, old: Number, now: Number ->
            if (isValueChanging) {
                if (duration != null) {
                    mp.seek(duration!!.multiply(value / 100.0))
                }
                updateValues()
            } else if (abs(now.toDouble() - old.toDouble()) > 1.5) {
                if (duration != null) {
                    mp.seek(duration!!.multiply(value / 100.0))
                }
            }
        }
    }

    var playTime = Label().apply {
        minWidth = Control.USE_PREF_SIZE
    }

    val volumeSlider: Slider? = null
    val mediaBar = HBox(5.0).apply {
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        alignment = Pos.CENTER_LEFT
        setAlignment(this, Pos.CENTER)
    }

    val mvPane = Pane().apply {
        children.add(mediaView)
        style = "-fx-background-color: black;"
        this@MediaControl.center = this
    }
    var newStage: Stage? = null
    var fullScreen = false


    override fun layoutChildren() {
        if (bottom != null) {
            mediaView.fitWidth = width
            mediaView.fitHeight = height - bottom.prefHeight(-1.0)
        }
        super.layoutChildren()
        if (center != null) {
            mediaView.translateX = ((center as Pane).width -
                    mediaView.prefWidth(-1.0)) / 2
            mediaView.translateY = ((center as Pane).height -
                    mediaView.prefHeight(-1.0)) / 2
        }
    }

    override fun computeMinWidth(height: Double): Double {
        return mediaBar.prefWidth(-1.0)
    }

    override fun computeMinHeight(width: Double): Double {
        return 200.0
    }

    override fun computePrefWidth(height: Double): Double {
        return Math.max(mp.media.width.toDouble(), mediaBar.prefWidth(height))
    }

    override fun computePrefHeight(width: Double): Double {
        return mp.media.height + mediaBar.prefHeight(width)
    }

    override fun computeMaxWidth(height: Double): Double {
        return Double.MAX_VALUE
    }

    override fun computeMaxHeight(width: Double): Double {
        return Double.MAX_VALUE
    }

    init {
        style = "-fx-background-color: #bfc2c7;"

        val imageViewPlay = ImageView(Image("file:src/main/resources/image/playbutton.png"))
        val imageViewPause = ImageView(Image("file:src/main/resources/image/pausebutton.png"))

        val playButton = Button().apply {
            minWidth = Control.USE_PREF_SIZE
            graphic = imageViewPlay
            onAction = EventHandler { _: ActionEvent? ->
                updateValues()
                val status = mp.status
                if (status == MediaPlayer.Status.UNKNOWN
                    || status == MediaPlayer.Status.HALTED
                ) {
                    return@EventHandler
                }
                if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED
                ) {
                    if (atEndOfMedia) {
                        mp.seek(mp.startTime)
                        atEndOfMedia = false
                        graphic = imageViewPlay
                        updateValues()
                    }
                    mp.play()
                    graphic = imageViewPause
                } else mp.pause()
            }
        }

        mp.apply {
            currentTimeProperty().addListener { _: ObservableValue<out Duration?>?, _: Duration?, _: Duration? -> updateValues() }
            onPlaying = Runnable {
                if (stopRequested) {
                    pause()
                    stopRequested = false
                } else playButton.graphic = imageViewPause
            }

            onPaused = Runnable { playButton.graphic = imageViewPlay }

            onReady = Runnable {
                duration = media.duration
                updateValues()
            }

            cycleCount = if (repeat) MediaPlayer.INDEFINITE else 1

            onEndOfMedia = Runnable {
                if (!repeat) {
                    playButton.graphic = imageViewPlay
                    stopRequested = true
                    atEndOfMedia = true
                }
            }
        }


        bottom = mediaBar.apply {
            children.addAll(
                playButton,
                Label("Time").apply { minWidth = Control.USE_PREF_SIZE },
                timeSlider, playTime,
                Button("Full Screen").apply {
                    minWidth = Control.USE_PREF_SIZE
                    onAction = EventHandler {
                        if (!fullScreen) {
                            newStage = Stage()
                            val full = newStage!!.fullScreenProperty()
                            full.addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? -> onFullScreen() }
                            val borderPane: BorderPane = object : BorderPane() {
                                override fun layoutChildren() {
                                    if (bottom != null) {
                                        mediaView.fitWidth = width
                                        mediaView.fitHeight = height - bottom.prefHeight(-1.0)
                                    }
                                    super.layoutChildren()
                                        (center as Pane).apply {
                                            mediaView.translateX = (this.width - mediaView.prefWidth(-1.0)) / 2.0
                                            mediaView.translateY = (this.height - mediaView.prefHeight(-1.0)) / 2.0
                                        }

                                }
                            }
                            center = null
                            bottom = null
                            borderPane.apply { center = mvPane; bottom = mediaBar }
                            val newScene = Scene(borderPane)
                            newStage!!.apply {
                                scene = newScene
                                x = -100000.0
                                y = -100000.0
                                isFullScreen = true
                                show()
                            }
                        } else {
                            newStage!!.isFullScreen = false
                        }
                        fullScreen = !fullScreen
                    }
                }
            )
        }
    }

    fun onFullScreen() {
        if (!newStage!!.isFullScreen) {
            fullScreen = false
            val smallBP = newStage!!.scene.root as BorderPane
            smallBP.center = null
            center = mvPane
            smallBP.bottom = null
            bottom = mediaBar
            Platform.runLater { newStage!!.close() }
        }
    }

    fun updateValues() {
        if (volumeSlider != null && duration != null) {
            Platform.runLater {
                val now = mp.currentTime
                playTime.text = formatTime(now, duration!!)
                timeSlider.isDisable = duration!!.isUnknown
                if (!timeSlider.isDisabled && duration!!.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging) timeSlider.value =
                    now.divide(duration).toMillis() * 100.0
                if (!volumeSlider.isValueChanging) volumeSlider.value = (mp.volume * 100).roundToInt().toDouble()
            }
        }
    }

    open fun formatTime(elapsed: Duration, duration: Duration): String? {
        var intElapsed = Math.floor(elapsed.toSeconds()).toInt()
        val elapsedHours = intElapsed / (60 * 60)
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60
        }
        val elapsedMinutes = intElapsed / 60
        val elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60
        return if (duration.greaterThan(Duration.ZERO)) {
            var intDuration = Math.floor(duration.toSeconds()).toInt()
            val durationHours = intDuration / (60 * 60)
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60
            }
            val durationMinutes = intDuration / 60
            val durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60
            if (durationHours > 0) {
                String.format(
                    "%d:%02d:%02d/%d:%02d:%02d",
                    elapsedHours, elapsedMinutes, elapsedSeconds,
                    durationHours, durationMinutes, durationSeconds
                )
            } else {
                String.format(
                    "%02d:%02d/%02d:%02d",
                    elapsedMinutes, elapsedSeconds,
                    durationMinutes, durationSeconds
                )
            }
        } else {
            if (elapsedHours > 0) {
                String.format(
                    "%d:%02d:%02d",
                    elapsedHours, elapsedMinutes, elapsedSeconds
                )
            } else {
                String.format(
                    "%02d:%02d",
                    elapsedMinutes, elapsedSeconds
                )
            }
        }
    }
}


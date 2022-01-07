package com.thepyprogrammer.fxtools.anim

import javafx.animation.Interpolator
import javafx.animation.Transition
import javafx.scene.control.Label
import javafx.util.Duration
import kotlin.math.roundToInt


class TypingTransition(var label: Label, var text: String, var speed: Double = 50.0) : Transition() {
    constructor(label: Label, speed: Double = 50.0) : this(label, label.text, speed)

    override fun interpolate(frac: Double) {
        val length = text.length
        val n = (length * frac.toFloat()).roundToInt()
        label.text = text.substring(0, n)
    }

    init {
        label.text = ""
        cycleDuration = Duration.millis(speed * text.length)
        interpolator = Interpolator.LINEAR
    }
}
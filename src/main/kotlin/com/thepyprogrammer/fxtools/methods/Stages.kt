package com.thepyprogrammer.fxtools.methods

import com.thepyprogrammer.fxtools.point.Point
import javafx.application.Application
import javafx.geometry.Rectangle2D
import javafx.stage.Screen
import javafx.stage.Stage

fun Application.fullScreen(stage: Stage) {
    val screen = Screen.getPrimary()
    val bounds: Rectangle2D = screen.visualBounds

    stage.x = bounds.minX
    stage.y = bounds.minY
    stage.width = bounds.width
    stage.height = bounds.height
}

fun List<Stage>.hide() {
    forEach { it.hide() }
}

var Stage.point: Point
get() = Point(x, y)
set(point) {
    x = point.x
    y = point.y
}

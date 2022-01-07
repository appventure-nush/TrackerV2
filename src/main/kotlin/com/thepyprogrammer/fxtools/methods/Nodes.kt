package com.thepyprogrammer.fxtools.methods

import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.layout.VBox

val Node.centeredNode
    get() = VBox(this).apply { alignment = Pos.CENTER }

val Control.absoluteRect
    get() = Rectangle2D(
        localToScene(layoutBounds.minX, layoutBounds.minY).x + scene.window.x,
        localToScene(layoutBounds.minX, layoutBounds.minY).y + scene.window.y,
        width,
        height
    )




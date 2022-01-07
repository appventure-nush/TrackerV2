package com.thepyprogrammer.fxtools.methods

import com.thepyprogrammer.fxtools.point.Point
import javafx.scene.input.*

val MouseEvent.screenPoint: Point
    get() = Point(screenX, screenY)

val MouseEvent.point: Point
    get() = Point(x, y)

val MouseEvent.scenePoint: Point
    get() = Point(sceneX, sceneY)


val DragEvent.screenPoint: Point
    get() = Point(screenX, screenY)

val DragEvent.point: Point
    get() = Point(x, y)

val DragEvent.scenePoint: Point
    get() = Point(sceneX, sceneY)


val ContextMenuEvent.screenPoint: Point
    get() = Point(screenX, screenY)

val ContextMenuEvent.point: Point
    get() = Point(x, y)

val ContextMenuEvent.scenePoint: Point
    get() = Point(sceneX, sceneY)


val GestureEvent.screenPoint: Point
    get() = Point(screenX, screenY)

val GestureEvent.point: Point
    get() = Point(x, y)

val GestureEvent.scenePoint: Point
    get() = Point(sceneX, sceneY)


val ScrollEvent.screenPoint: Point
    get() = Point(screenX, screenY)

val ScrollEvent.point: Point
    get() = Point(x, y)

val ScrollEvent.scenePoint: Point
    get() = Point(sceneX, sceneY)




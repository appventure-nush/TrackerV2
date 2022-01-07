package com.thepyprogrammer.fxtools.draggable

import com.thepyprogrammer.fxtools.util.given
import com.thepyprogrammer.fxtools.resizable.ResizeMode
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import kotlin.math.abs

abstract class DraggableNode : Pane() {
    val view: Node

    // node position
    private var x = 0.0
    private var y = 0.0

    // mouse position
    private var mousex = 0.0
    private var mousey = 0.0

    protected var isDragging = false
        private set

    var isMoveToFront = true
    private var scaleTransform: Scale? = null

    var isZoomable = false
    private var resizable = false
    set(resizable) {
        field = resizable.given { initScale() }
    }

    fun initScale() {
        scaleTransform = Scale(1.0, 1.0).apply {
            pivotX = 0.0
            pivotY = 0.0
            pivotZ = 0.0
            transforms.add(this)
        }
    }

    var minScale = 0.1
    var maxScale = 10.0
    var scaleIncrement = 0.001
    private var resizeMode: ResizeMode? = null
    private var RESIZE_TOP = false
    private var RESIZE_LEFT = false
    private var RESIZE_BOTTOM = false
    private var RESIZE_RIGHT = false
    abstract fun createWidget(): Node
    private fun init() {
        resizable.given { initScale() }
        onMousePressedProperty().set(EventHandler { event: MouseEvent ->
            val n: Node = this@DraggableNode
            val parentScaleX = n.parent.localToSceneTransformProperty().value.mxx
            val parentScaleY = n.parent.localToSceneTransformProperty().value.myy

            // record the current mouse X and Y position on Node
            mousex = event.sceneX
            mousey = event.sceneY
            x = n.layoutX * parentScaleX
            y = n.layoutY * parentScaleY
            if (isMoveToFront) {
                toFront()
            }
        })

        //Event Listener for MouseDragged
        onMouseDraggedProperty().set(EventHandler { event: MouseEvent ->
            val n: Node = this@DraggableNode
            val parentScaleX = n.parent.localToSceneTransformProperty().value.mxx
            val parentScaleY = n.parent.localToSceneTransformProperty().value.myy
            val scaleX = n.localToSceneTransformProperty().value.mxx
            val scaleY = n.localToSceneTransformProperty().value.myy

            // Get the exact moved X and Y
            val offsetX = event.sceneX - mousex
            val offsetY = event.sceneY - mousey
            if (resizeMode === ResizeMode.NONE) {
                x += offsetX
                y += offsetY
                val scaledX = x / parentScaleX
                val scaledY = y / parentScaleY
                layoutX = scaledX
                layoutY = scaledY
                isDragging = true
            } else {
                if (RESIZE_TOP) {
                    val newHeight = (boundsInLocal.height
                            - offsetY / scaleY - insets.top)
                    y += offsetY
                    val scaledY = y / parentScaleY
                    layoutY = scaledY
                    prefHeight = newHeight
                }
                if (RESIZE_LEFT) {
                    val newWidth = (boundsInLocal.width
                            - offsetX / scaleX - insets.left)
                    x += offsetX
                    val scaledX = x / parentScaleX
                    layoutX = scaledX
                    prefWidth = newWidth
                }
                if (RESIZE_BOTTOM) {
                    val newHeight = (boundsInLocal.height
                            + offsetY / scaleY
                            - insets.bottom)
                    prefHeight = newHeight
                }
                if (RESIZE_RIGHT) {
                    val newWidth = (boundsInLocal.width
                            + offsetX / scaleX
                            - insets.right)
                    prefWidth = newWidth
                }
            }

            // again set current Mouse x AND y position
            mousex = event.sceneX
            mousey = event.sceneY
            event.consume()
        })
        onMouseClickedProperty().set(EventHandler {
            isDragging = false
        })
        onMouseMovedProperty().set(EventHandler { t: MouseEvent ->
            val n: Node = this@DraggableNode
            val scaleX = n.localToSceneTransformProperty().value.mxx
            val scaleY = n.localToSceneTransformProperty().value.myy
            val border = 10.0
            val diffMinX = abs(n.boundsInLocal.minX - t.x)
            val diffMinY = abs(n.boundsInLocal.minY - t.y)
            val diffMaxX = abs(n.boundsInLocal.maxX - t.x)
            val diffMaxY = abs(n.boundsInLocal.maxY - t.y)
            val left = diffMinX * scaleX < border
            val top = diffMinY * scaleY < border
            val right = diffMaxX * scaleX < border
            val bottom = diffMaxY * scaleY < border
            RESIZE_TOP = false
            RESIZE_LEFT = false
            RESIZE_BOTTOM = false
            RESIZE_RIGHT = false
            if (left && !top && !bottom) {
                n.cursor = Cursor.W_RESIZE
                resizeMode = ResizeMode.LEFT
                RESIZE_LEFT = true
            } else if (left && top && !bottom) {
                n.cursor = Cursor.NW_RESIZE
                resizeMode = ResizeMode.TOP_LEFT
                RESIZE_LEFT = true
                RESIZE_TOP = true
            } else if (left && !top && bottom) {
                n.cursor = Cursor.SW_RESIZE
                resizeMode = ResizeMode.BOTTOM_LEFT
                RESIZE_LEFT = true
                RESIZE_BOTTOM = true
            } else if (right && !top && !bottom) {
                n.cursor = Cursor.E_RESIZE
                resizeMode = ResizeMode.RIGHT
                RESIZE_RIGHT = true
            } else if (right && top && !bottom) {
                n.cursor = Cursor.NE_RESIZE
                resizeMode = ResizeMode.TOP_RIGHT
                RESIZE_RIGHT = true
                RESIZE_TOP = true
            } else if (right && !top && bottom) {
                n.cursor = Cursor.SE_RESIZE
                resizeMode = ResizeMode.BOTTOM_RIGHT
                RESIZE_RIGHT = true
                RESIZE_BOTTOM = true
            } else if (top && !left && !right) {
                n.cursor = Cursor.N_RESIZE
                resizeMode = ResizeMode.TOP
                RESIZE_TOP = true
            } else if (bottom && !left && !right) {
                n.cursor = Cursor.S_RESIZE
                resizeMode = ResizeMode.BOTTOM
                RESIZE_BOTTOM = true
            } else {
                n.cursor = Cursor.DEFAULT
                resizeMode = ResizeMode.NONE
            }
            if (!resizable) {
                n.cursor = Cursor.DEFAULT
                resizeMode = ResizeMode.NONE
                RESIZE_TOP = false
                RESIZE_LEFT = false
                RESIZE_BOTTOM = false
                RESIZE_RIGHT = false
            }
        })
        onScroll = EventHandler { event: ScrollEvent ->
            if (!isZoomable) return@EventHandler
            var scaleValue = scaleTransform!!.y + event.deltaY * scaleIncrement
            scaleValue = scaleValue.coerceAtLeast(minScale)
            scaleValue = scaleValue.coerceAtMost(maxScale)
            scaleTransform!!.x = scaleValue
            scaleTransform!!.y = scaleValue
            scaleTransform!!.pivotX = 0.0
            scaleTransform!!.pivotX = 0.0
            scaleTransform!!.pivotZ = 0.0
            event.consume()
        }
    }

    /**
     * @return the resizable
     */
    override fun isResizable(): Boolean {
        return resizable
    }

    fun removeNode(n: Node?) = children.remove(n)

    companion object {
        const val CSS_STYLE = "  -fx-alignment: center;\n" + "  -fx-font-size: 20;\n"
    }

    init {
        view = createWidget()
        children.add(view)
        init()
        style = CSS_STYLE
    }
}
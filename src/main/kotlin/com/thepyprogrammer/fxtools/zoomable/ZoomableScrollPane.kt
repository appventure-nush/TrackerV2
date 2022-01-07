package com.thepyprogrammer.fxtools.zoomable

import com.thepyprogrammer.fxtools.methods.centeredNode
import com.thepyprogrammer.fxtools.methods.point
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import kotlin.math.exp

class ZoomableScrollPane(
    var targetProperty: ObjectProperty<Node> = SimpleObjectProperty()
): ScrollPane() {
    var scaleValue = 0.7
    val zoomIntensity = 0.02
    var zoomNode: Node? = null

    private var target: Node
        get() = targetProperty.get()
        set(target) {
            targetProperty.set(target)
            zoomNode = Group(target)
            content = outerNode(zoomNode as Group)

            updateScale()
        }

    private fun outerNode(node: Node) =
        node.centeredNode.apply {
            setOnScroll { e ->
                e.consume()
                onScroll(e.textDeltaY, e.point.toPoint2d())
            }
        }

    private fun updateScale() {
        target.scaleX = scaleValue
        target.scaleY = scaleValue
    }

    private fun onScroll(wheelDelta: Double, mousePoint: Point2D) {
        val zoomFactor = exp(wheelDelta * zoomIntensity)

        val innerBounds = zoomNode!!.layoutBounds
        val viewportBounds = viewportBounds

        // calculate pixel offsets from [0, 1] range

        // calculate pixel offsets from [0, 1] range
        val valX = hvalue * (innerBounds.width - viewportBounds.width)
        val valY = vvalue * (innerBounds.height - viewportBounds.height)

        scaleValue *= zoomFactor
        updateScale()
        layout() // refresh ScrollPane scroll positions & target bounds


        // convert target coordinates to zoomTarget coordinates

        // convert target coordinates to zoomTarget coordinates
        val posInZoomTarget: Point2D = target.parentToLocal(zoomNode!!.parentToLocal(mousePoint))

        // calculate adjustment of scroll position (pixels)

        // calculate adjustment of scroll position (pixels)
        val adjustment: Point2D =
            target.localToParentTransform.deltaTransform(posInZoomTarget.multiply(zoomFactor - 1))

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        val updatedInnerBounds = zoomNode!!.boundsInLocal
        hvalue = (valX + adjustment.x) / (updatedInnerBounds.width - viewportBounds.width)
        vvalue = (valY + adjustment.y) / (updatedInnerBounds.height - viewportBounds.height)
    }

    init {
        isPannable = true
        hbarPolicy = ScrollBarPolicy.NEVER
        vbarPolicy = ScrollBarPolicy.NEVER
        isFitToHeight = true  // center
        isFitToWidth = true  // center

    }

    constructor(target: Node): this() {
        this.target = target
    }


}
package com.thepyprogrammer.fxtools.point

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Point2D
import kotlin.math.sqrt


data class Point(
    var xProperty: DoubleProperty = SimpleDoubleProperty(0.0),
    var yProperty: DoubleProperty = SimpleDoubleProperty(0.0)
) : Cloneable, Comparable<Any?> {

    var x: Double
        get() = xProperty.get()
        set(other) = xProperty.set(other)

    var y: Double
        get() = yProperty.get()
        set(other) = yProperty.set(other)

    constructor(x: Double, y: Double): this() {
        set(x, y)
    }

    constructor(point: Point2D) : this(point.x, point.y)

    operator fun set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    infix fun set(point: Point2D) {
        this.x = point.x
        this.y = point.y
    }

    override fun toString(): String {
        return "($xProperty, $yProperty)"
    }

    fun copy(): Point {
        return Point(x, y)
    }

    public override fun clone(): Point {
        return copy()
    }

    infix fun bindX(xProperty: DoubleProperty) {
        this.xProperty.bind(xProperty)
    }

    infix fun bindXBidirectional(xProperty: DoubleProperty) {
        this.xProperty.bindBidirectional(xProperty)
    }

    infix fun bindY(yProperty: DoubleProperty) {
        this.yProperty.bind(yProperty)
    }

    infix fun bindYBidirectional(yProperty: DoubleProperty) {
        this.yProperty.bindBidirectional(yProperty)
    }

    infix fun bind(p: Point) {
        bindX(p.xProperty)
        bindY(p.yProperty)
    }

    infix fun bindBidirectional(p: Point) {
        bindXBidirectional(p.xProperty)
        bindYBidirectional(p.yProperty)
    }

    fun toPoint2d(): Point2D {
        return Point2D(x, y)
    }

    override infix operator fun compareTo(other: Any?): Int {
        return if (other is Point2D) compareTo(other) else if (other is Point) compareTo(other) else 0
    }

    infix operator fun compareTo(p: Point): Int {
        return if (x != p.x) (x - p.x).toInt() else (y - p.y).toInt()
    }

    infix operator fun compareTo(p: Point2D): Int {
        return if (x != p.x) (x - p.x).toInt() else (x - p.y).toInt()
    }

    infix fun fromOrigin(p: Point): Point {
        return Point(x - p.x, y - p.y)
    }

    private val hypotenuse: Double
        get() = sqrt(x * x + y * y)

    infix fun hypotenuseFrom(p: Point): Double {
        return fromOrigin(p).hypotenuse
    }

    fun sumOfProperties(): DoubleProperty {
        return xProperty + yProperty
    }

    infix operator fun plus(p: Point): Point = Point(
        xProperty + p.xProperty,
        yProperty + p.yProperty
    )

    fun plus(xProperty: DoubleProperty, yProperty: DoubleProperty): Point = Point(
        xProperty + this.xProperty,
        yProperty + this.yProperty
    )

    infix operator fun times(p: Point): DoubleProperty =
        scale(p).sumOfProperties()

    infix operator fun times(scalingProperty: DoubleProperty): DoubleProperty =
        (xProperty * scalingProperty) + (yProperty * scalingProperty)

    infix fun scaleX(scalingProperty: DoubleProperty) =
        Point((xProperty * scalingProperty), (yProperty))

    infix fun scaleY(scalingProperty: DoubleProperty) =
        Point((xProperty), (yProperty * scalingProperty))

    fun scale(scalingXProperty: DoubleProperty, scalingYProperty: DoubleProperty): Point =
        this scaleX scalingXProperty scaleY scalingYProperty

    infix fun scale(scalingProperty: DoubleProperty): Point = scale(scalingProperty, scalingProperty)

    infix fun scale(p: Point): Point = scale(p.xProperty, p.yProperty)

    infix operator fun minus(p: Point): Point = Point(
        xProperty - p.xProperty,
        yProperty - p.yProperty
        )
}
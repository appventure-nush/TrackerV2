package com.thepyprogrammer.fxtools.methods

import com.thepyprogrammer.fxtools.point.Point
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D

infix operator fun DoubleProperty.plus(other: DoubleProperty): DoubleProperty =
    SimpleDoubleProperty().apply { bind(this@plus.add(other)) }

infix operator fun DoubleProperty.minus(other: DoubleProperty): DoubleProperty =
    SimpleDoubleProperty().apply { bind(this@minus.subtract(other)) }

infix operator fun DoubleProperty.div(other: DoubleProperty): DoubleProperty =
    SimpleDoubleProperty().apply { bind(this@div.divide(other)) }

infix operator fun DoubleProperty.times(other: DoubleProperty): DoubleProperty =
    SimpleDoubleProperty().apply { bind(this@times.multiply(other)) }

fun Point2D.toPoint() = Point(this)

infix fun <T> Property<T>.bindBidirectional(other: ObservableValue<T>) {
    bind(other)
}

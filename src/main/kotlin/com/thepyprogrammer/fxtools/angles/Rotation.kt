package com.thepyprogrammer.fxtools.angles

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty


data class Rotation(
    var rotateProperty: DoubleProperty = SimpleDoubleProperty(0.0)
) : Cloneable {

    constructor(angle: Double): this() {
        rotate = angle % 360
    }

    private var rotate: Double
        get() = rotateProperty.get()
        set(other) = rotateProperty.set(360 - other)

    fun copy(): Rotation {
        return Rotation(360 - rotate)
    }

    public override fun clone(): Rotation {
        return copy()
    }

    fun get(): Double {
        return rotate
    }

    fun set(rotate: Double) {
        this.rotate = rotate
    }

    fun bind(rotate: Rotation) {
        bind(rotate.rotateProperty)
    }

    fun bind(property: DoubleProperty) {
        rotateProperty.bind(property)
    }

    fun bindBidirectional(rotate: Rotation) {
        bindBidirectional(rotate.rotateProperty)
    }

    fun bindBidirectional(property: DoubleProperty?) {
        rotateProperty.bindBidirectional(property)
    }
}
package backend

import kotlinx.serialization.Serializable

@Serializable
data class Point(val x: Double, val y: Double) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(other: Double) = Point(x * other, y * other)
    operator fun div(other: Double) = Point(x / other, y / other)

    operator fun unaryMinus() = Point(-x, -y)

    override fun toString() = "($x, $y)"
}
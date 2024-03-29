package backend.image_processing

import backend.Point

data class Circle(val centre: Point, val radius: Double)

data class Ellipse(val centre: Point, val angle: Double, val width: Double, val height: Double)

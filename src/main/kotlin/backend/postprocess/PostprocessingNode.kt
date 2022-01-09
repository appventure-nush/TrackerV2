package backend.postprocess

import backend.Point
import backend.Processing
import org.bytedeco.opencv.opencv_core.Mat

abstract class PostprocessingNode: Processing() {
    var scale = 1.0  // 1 pixel is scale metres
    var origin = Point(0.0, 0.0)  // The origin (in pixels)

    abstract val entries: List<String>

    abstract fun process(img: Mat): List<Any>

    /** Converts a point in pixels to the same point in metres */
    fun position(point: Point) = (point - origin) * scale
}
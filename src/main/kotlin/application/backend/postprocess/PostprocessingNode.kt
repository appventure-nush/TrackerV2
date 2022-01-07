package application.backend.postprocess

import application.backend.Colourspace
import application.backend.Point
import org.bytedeco.opencv.opencv_core.Mat

abstract class PostprocessingNode {
    var scale = 0.01  // 1 pixel is scale metres
    var origin = Point(0.0, 0.0)  // The origin (in pixels)

    abstract val help: String
    abstract val entries: List<String>

    open var inputColourspace: Colourspace = Colourspace.RGB
    abstract val inputColourspaces: List<Colourspace>

    abstract fun process(img: Mat): List<Any>

    /** Converts a point in pixels to the same point in metres */
    fun position(point: Point) = (point - origin) * scale
}
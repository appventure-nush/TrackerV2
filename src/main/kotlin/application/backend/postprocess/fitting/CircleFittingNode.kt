package application.backend.postprocess.fitting

import application.backend.Colourspace
import application.backend.Point
import application.backend.postprocess.PostprocessingNode
import org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT
import org.bytedeco.opencv.global.opencv_imgproc.HoughCircles
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector

// Param 1 is for edge detection
// Param 2 is for centre detection. Lower for lower standards of what is a circle.
data class CircleFittingNode(var minDist: Double = 20.0, var param1: Double = 200.0, var param2: Double = 100.0,
                             var minRadius: Int = 0, var maxRadius: Int = 0) : PostprocessingNode() {
    override val name: String = "Circle Fitting"
    override val help: String = "Finds circle in the pictures and returns the biggest one."
    override val entries: List<String> = listOf("centre_x", "centre_y", "radius")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.GRAYSCALE

    override fun process(img: Mat): List<Any> {
        val circles = Vec3fVector()
        HoughCircles(img, circles, CV_HOUGH_GRADIENT, 1.0, minDist, param1, param2, minRadius, maxRadius)

        val circlesList = arrayListOf<List<Double>>()
        for (i in 0 until circles.size()) {
            val circle = circles[i]
            val center = position(Point(circle[1].toDouble(), circle[2].toDouble()))
            circlesList.add(listOf(circle[0].toDouble(), center.x, center.y))
        }

        return circlesList.maxByOrNull { it[2] } ?: listOf(-1, -1, -1)
    }
}
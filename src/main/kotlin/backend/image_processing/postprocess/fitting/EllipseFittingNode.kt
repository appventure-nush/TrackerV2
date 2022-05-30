package backend.image_processing.postprocess.fitting

import backend.Colourspace
import backend.Point
import backend.image_processing.postprocess.PostprocessingNode
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.MatVector


class EllipseFittingNode : PostprocessingNode() {
    override val name: String = "Ellipse Fitting"
    override val help: String = "Finds ellipse in the pictures and returns the biggest one."
    override val entries: List<String> = listOf("width", "height", "angle", "centre_x", "centre_y")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.GRAYSCALE

    override fun process(img: Mat): List<Any> {
        val hierarchy = Mat()
        val contours = MatVector()
        findContours(img, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE)

        val ellipses = Array(contours.size().toInt()) {
            require(contours.size() > 5) { "Ellipse needs to fitted to at least 5 points." }
            fitEllipse(contours[it.toLong()])
        }

        return ellipses.map {
            val centre = position(Point(it.center().x().toDouble(), it.center().y().toDouble()))
            listOf(it.size().width(), it.size().height(), it.angle(), centre.x, centre.y)
        }.maxByOrNull { it[0] as Double } ?: listOf(-1, -1, -1, -1)
    }
}
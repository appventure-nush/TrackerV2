package backend.image_processing.postprocess.fitting

import backend.Colourspace
import backend.Image
import backend.image_processing.postprocess.PostprocessingNode
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector

// Param 1 is for edge detection
// Param 2 is for centre detection. Lower for lower standards of what is a circle.
data class CircleFittingNode(var minDist: Double = 20.0, var param1: Double = 200.0, var param2: Double = 100.0,
                             var minRadius: Int = 0, var maxRadius: Int = 0) : PostprocessingNode() {
    override val name: String = "Circle Fitting"
    override val help: String = "Finds circles in the image and returns the biggest one."
    override val entries: List<String> = listOf("centre_x", "centre_y", "radius")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.GRAYSCALE

    override fun process(img: Image): Pair<List<Any>, Image> {
        val circles = img.fitCircle(minDist, param1, param2, minRadius, maxRadius)
        val biggestCircle = circles.maxByOrNull { it.radius }

        val newImg = img.clone().apply { drawCircle(circles) }

        return if (biggestCircle == null) Pair(listOf(-1, -1, -1), newImg)
        else Pair(listOf(biggestCircle.centre.x, biggestCircle.centre.y, biggestCircle.radius), newImg)
    }
}
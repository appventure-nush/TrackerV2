package backend.image_processing.postprocess.fitting

import backend.Colourspace
import backend.Image
import backend.image_processing.postprocess.PostprocessingNode
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector


/**
 * The post-processing node that fits circles with radius between [minRadius] and [maxRadius] to the image and
 * returns the biggest circle. TODO Return all circles
 * @param minDist The minimum distance between circles
 * @param param1 This parameter is used for edge detection
 * @param param2 This controls how circular an object must be to be considered a circle
 */
data class CircleFittingNode(var minDist: Double = 20.0, var param1: Double = 200.0, var param2: Double = 100.0,
                             var minRadius: Int = 0, var maxRadius: Int = 0) : PostprocessingNode() {
    override val name: String = "Circle Fitting"
    override val help: String = "Finds circles in the image and returns the biggest one."
    override val entries: List<String> = listOf("centre_x", "centre_y", "radius")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV, Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.RGB

    override fun process(img: Image): Pair<List<Any>, Image> {
        val grayscaleImg = img.clone()
        grayscaleImg.colourspace = Colourspace.GRAYSCALE

        val circles = grayscaleImg.fitCircle(minDist, param1, param2, minRadius, maxRadius)
        val biggestCircle = circles.maxByOrNull { it.radius }

        val newImg = img.clone().apply { drawCircle(circles) }

        return if (biggestCircle == null) Pair(listOf(-1, -1, -1), newImg)
        else Pair(listOf(biggestCircle.centre.x, biggestCircle.centre.y, biggestCircle.radius), newImg)
    }

    override fun clone() = CircleFittingNode(minDist, param1, param2, minRadius, maxRadius)
}
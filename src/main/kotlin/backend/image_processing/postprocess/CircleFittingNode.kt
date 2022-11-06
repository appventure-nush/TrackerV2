package backend.image_processing.postprocess

import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable


/**
 * The post-processing node that fits circles with radius between [minRadius] and [maxRadius] to the image
 * @param minDist The minimum distance between circles
 * @property minDist The minimum distance between circles
 * @param param1 This parameter is used for edge detection
 * @property param1 This parameter is used for edge detection
 * @param param2 This controls how circular an object must be to be considered a circle
 * @property param2 This controls how circular an object must be to be considered a circle
 * @param index The contour to export. Use -1 to export all.
 * @property index The contour to export. Use -1 to export all.
 */
@Serializable
data class CircleFittingNode(var index: Int, var minDist: Double = 20.0, var param1: Double = 200.0, var param2: Double = 100.0,
                             var minRadius: Int = 0, var maxRadius: Int = 0) : PostprocessingNode() {
    override val name: String = "Circle Fitting"
    override val help: String = "Finds circles in the image and returns the biggest one."
    override val entries: List<String> = listOf("centre_x", "centre_y", "radius")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV, Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.RGB

    override fun process(img: Image): Pair<List<List<Any>>, Image> {
        val grayscaleImg = img.clone()
        grayscaleImg.colourspace = Colourspace.GRAYSCALE

        val circles = grayscaleImg.fitCircle(minDist, param1, param2, minRadius, maxRadius)
        return if (index == -1) {
            val newImg = img.clone().apply { drawCircle(circles) }
            Pair(circles.map { listOf(it.centre.x, it.centre.y, it.radius) }, newImg)
        } else {
            val newImg = img.clone().apply { drawCircle(circles[index]) }
            Pair(listOf(listOf(circles[index].centre.x, circles[index].centre.y, circles[index].radius)), newImg)
        }
    }

    override fun clone() = CircleFittingNode(index, minDist, param1, param2, minRadius, maxRadius)
}
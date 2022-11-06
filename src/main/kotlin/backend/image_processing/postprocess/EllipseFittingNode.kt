package backend.image_processing.postprocess

import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable


/**
 * The post-processing node used to fit ellipses and return the biggest one
 */
@Serializable
class EllipseFittingNode : PostprocessingNode() {
    override val name: String = "Ellipse Fitting"
    override val help: String = "Finds ellipse in the pictures and returns the biggest one."
    override val entries: List<String> = listOf("width", "height", "angle", "centre_x", "centre_y")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV, Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.RGB

    override fun process(img: Image): Pair<List<List<Any>>, Image> {
        val grayscaleImg = img.clone()
        grayscaleImg.colourspace = Colourspace.GRAYSCALE

        val ellipses = grayscaleImg.fitEllipse()
        val newImg = img.clone().apply { drawEllipse(ellipses) }

        return Pair(ellipses.map { with(it) { listOf(width, height, angle, centre.x, centre.y) } }, newImg)
    }

    override fun clone() = EllipseFittingNode()
}
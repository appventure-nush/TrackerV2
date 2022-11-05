package backend.image_processing.postprocess

import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable


/**
 * The post-processing node used to fit contours
 */
@Serializable
class ContourFittingNode : PostprocessingNode() {
    override val name: String = "Contour Fitting"
    override val help: String = "Finds contours in the pictures"
    override val entries: List<String> = listOf("asd")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV, Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.RGB

    override fun process(img: Image): Pair<List<Any>, Image> {
        val newImg = img.clone()
        newImg.drawContours2(newImg.fitContours())

        return Pair(listOf(-1), newImg)
    }

    override fun clone() = ContourFittingNode()
}
package backend.image_processing.postprocess

import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable


/**
 * The post-processing node used to fit contours
 * @param index The contour to export. Use -1 to export all.
 * @property index The contour to export. Use -1 to export all.
 */
@Serializable
class ContourFittingNode(var index: Int) : PostprocessingNode() {
    override val name: String = "Contour Fitting"
    override val help: String = "Finds contours in the pictures"
    override val entries: List<String> = listOf("length", "contour")

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV, Colourspace.GRAYSCALE)
    override var inputColourspace: Colourspace = Colourspace.RGB

    override fun process(img: Image): Pair<List<List<Any>>, Image> {
        val newImg = img.clone()
        val contours = img.fitContours()
        return if (index >= 0) {
            val contour = contours[index]
            newImg.drawContours(listOf(contour))

            Pair(listOf(listOf(contour.size, contour.joinToString(","))), newImg)
        } else {
            newImg.drawContours(contours)

            Pair(contours.map { listOf(it.size, it.joinToString(",")) }, newImg)
        }
    }

    override fun clone(): PostprocessingNode {
        val node = ContourFittingNode(index)
        node.inputColourspace = inputColourspace
        return node
    }
}
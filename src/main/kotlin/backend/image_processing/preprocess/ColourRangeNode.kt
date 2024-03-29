package backend.image_processing.preprocess

import backend.Colourspace
import backend.Image
import backend.RGB
import kotlinx.serialization.Serializable
import com.github.ajalt.colormath.model.RGB as sRGB

/**
 * The node for filter colours within the specified colour ranges
 * @param colours A list of colour ranges used to filter the image
 * @property colours A list of colour ranges used to filter the image
 * @param binarise Should the image outputted be a binary mask?
 * @property binarise Should the image outputted be a binary mask?
 */
@Serializable
data class ColourRangeNode(
    var colours: ArrayList<Pair<RGB, RGB>> = arrayListOf(),
    var binarise: Boolean = false
): PreprocessingNode() {
    override val name: String = "Filter Colours"
    override val help: String = "Filters out parts of the images within the given colour range."

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV)
    override val outputColourspace: Colourspace get() = inputColourspace

    override fun process(img: Image): Image = img.clone().apply {
        colourFilter(colours.map {
            Pair(
                sRGB(it.first.red, it.first.blue, it.first.green),
                sRGB(it.second.red, it.second.blue, it.second.green)
            )
        }, binarise)
    }

    override fun clone(): PreprocessingNode = ColourRangeNode(colours, binarise)
}
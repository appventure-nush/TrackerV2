package backend.image_processing.preprocess.masking

import backend.Colourspace
import backend.Image
import backend.image_processing.preprocess.PreprocessingNode
import com.github.ajalt.colormath.Color

/**
 * The node for filter colours within the specified colour ranges
 * @param colours A list of colour ranges used to filter the image
 * @property colours A list of colour ranges used to filter the image
 * @param binarise Should the image outputted be a binary mask?
 * @property binarise Should the image outputted be a binary mask?
 */
data class ColourRangeNode(var colours: ArrayList<Pair<Color, Color>>, var binarise: Boolean): PreprocessingNode() {
    override val name: String = "Filter Colours"
    override val help: String = "Filters out parts of the images within the given colour range."

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV)
    override val outputColourspace: Colourspace get() = inputColourspace

    override fun process(img: Image): Image = img.clone().apply { colourFilter(colours, binarise) }

    override fun clone(): PreprocessingNode = ColourRangeNode(colours, binarise)
}
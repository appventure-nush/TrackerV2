package backend.image_processing.preprocess

import backend.ALL_SPACES
import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable

/**
 * All the types of blurring supported
 */
enum class Morphological {
    ERODE,
    DILATE,
    OPENING,
    CLOSING,
    GRADIENT
}

/**
 * The node for blurring images
 * @param operationType The type of blurring to use
 * @property operationType The type of blurring to use
 * @param kernelSize The kernel size to use for blurring
 * @property kernelSize The kernel size to use for blurring
 * @param iterations The number of times to apply the operation
 * @property iterations The number of times to apply the operation
 */
@Serializable
class MorphologicalNode(var operationType: Morphological = Morphological.ERODE,
                        var kernelSize: Int = 3, var iterations: Int = 1): PreprocessingNode() {
    override val name: String = "Morphing"
    override val help: String = "This node performs morphological operations on the image. " +
            "The most useful type of operation is \"OPENING\" and \"CLOSING\" which remove noise and " +
            "patch holes respectively."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace get() = inputColourspace

    override fun process(img: Image): Image = img.clone().apply {
        when (operationType) {
            Morphological.ERODE -> erode(kernelSize, iterations)
            Morphological.DILATE -> dilate(kernelSize, iterations)
            else -> boxFilter(kernelSize)
        }
    }

    override fun clone(): MorphologicalNode = MorphologicalNode(operationType, kernelSize, iterations)
}
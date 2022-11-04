package backend.image_processing.preprocess

import backend.ALL_SPACES
import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable

/**
 * The node used for thresholding
 * @param minThreshold The minimum threshold to use
 * @property minThreshold The minimum threshold to use
 * @param maxThreshold The maximum threshold to use
 * @property maxThreshold The minimum threshold to use
 * @param binarise Should the image be converted to a binary mask?
 * @property binarise Should the image be converted to a binary mask?
 */
@Serializable
class ThresholdingNode(var minThreshold: Double = 0.0, var maxThreshold: Double = 255.0, var binarise: Boolean = true): PreprocessingNode() {
    override val name: String = "Binarise"
    override val help: String = "Performs a black and white threshold on the image."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace get() = if (binarise) inputColourspace else Colourspace.GRAYSCALE

    override fun process(img: Image): Image = img.clone().apply { threshold(minThreshold, maxThreshold, binarise) }

    override fun clone(): ThresholdingNode = ThresholdingNode(minThreshold, maxThreshold, binarise)
}
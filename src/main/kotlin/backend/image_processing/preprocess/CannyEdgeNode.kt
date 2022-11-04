package backend.image_processing.preprocess

import backend.ALL_SPACES
import backend.Colourspace
import backend.Image
import kotlinx.serialization.Serializable

/**
 * The node that performs canny edge detection
 * @param threshold The threshold to use for the edge detection
 * @property threshold The threshold to use for the edge detection
 * @param kernelSize The size of the kernel used for edge detection
 * @property kernelSize The size of the kernel used for edge detection
 */
@Serializable
class CannyEdgeNode(var threshold: Double = 200.0, var kernelSize: Int = 3): PreprocessingNode() {
    override val name: String = "Edge\nDetection"
    override val help: String = "Detects edges in the image. Blurring first in recommended."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace = Colourspace.GRAYSCALE

    override fun process(img: Image): Image = img.clone().apply { cannyEdge(threshold, kernelSize = kernelSize) }

    override fun clone(): PreprocessingNode = CannyEdgeNode(threshold, kernelSize)
}
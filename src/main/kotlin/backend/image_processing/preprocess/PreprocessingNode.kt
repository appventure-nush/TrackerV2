package backend.image_processing.preprocess

import backend.Colourspace
import backend.Image
import backend.image_processing.Processing
import kotlinx.serialization.Serializable

/**
 * The base class for all preprocessing nodes
 */
@Serializable
sealed class PreprocessingNode: Processing() {
    /**
     * The output colourspace of the node
     */
    abstract val outputColourspace: Colourspace

    /**
     * Processes the given [img] and outputs the processed image
     */
    abstract fun process(img: Image): Image

    /**
     * Returns a deep copy of the preprocessing node
     */
    abstract fun clone(): PreprocessingNode

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        return "${name}@${hashCode().toHexString()}"
    }
}
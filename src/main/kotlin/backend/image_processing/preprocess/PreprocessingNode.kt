package backend.image_processing.preprocess

import backend.Colourspace
import backend.Image
import backend.image_processing.Processing

/**
 * The base class for all preprocessing nodes
 */
abstract class PreprocessingNode: Processing() {
    /**
     * The output colourspace of the node
     */
    abstract val outputColourspace: Colourspace

    /**
     * Processes the given [img] and outputs the processed image
     */
    abstract fun process(img: Image): Image
}
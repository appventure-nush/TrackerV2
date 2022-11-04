package backend.image_processing.preprocess

import backend.Image
import kotlinx.serialization.Serializable

/**
 * Preprocesses the inputted image
 */
@Serializable
class Preprocessor {
    /**
     * The nodes to use in the pre-processing
     */
    val nodes: ArrayList<PreprocessingNode> = arrayListOf()

    /**
     * Preprocesses the image with the [nodes]
     */
    fun process(img: Image): Image {
        var newImg = img.clone()

        nodes.forEach {
            newImg.colourspace = it.inputColourspace
            newImg = it.process(newImg)
        }

        return newImg
    }
}
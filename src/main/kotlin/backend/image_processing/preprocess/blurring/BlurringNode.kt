package backend.image_processing.preprocess.blurring

import backend.ALL_SPACES
import backend.Colourspace
import backend.Image
import backend.image_processing.preprocess.PreprocessingNode

/**
 * All the types of blurring supported
 */
enum class Blurring {
    GAUSSIAN,
    MEDIAN,
    BOX_FILTER
}

/**
 * The node for blurring images
 * @param blurType The type of blurring to use
 * @property blurType The type of blurring to use
 * @param kernelSize The kernel size to use for blurring
 * @property kernelSize The kernel size to use for blurring
 */
class BlurringNode(var blurType: Blurring = Blurring.GAUSSIAN, var kernelSize: Int = 3): PreprocessingNode() {
    override val name: String = "Blurring"
    override val help: String = "This node blurs the video to remove noise. The kernel size controls the extent of blurring and " +
            "can only be odd."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace get() = inputColourspace

    override fun process(img: Image): Image = img.clone().apply {
        when (blurType) {
            Blurring.GAUSSIAN -> gaussianBlur(kernelSize)
            Blurring.MEDIAN -> medianBlur(kernelSize)
            else -> boxFilter(kernelSize)
        }
    }
}
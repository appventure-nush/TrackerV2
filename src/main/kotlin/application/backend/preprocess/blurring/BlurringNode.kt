package application.backend.preprocess.blurring

import application.backend.ALL_SPACES
import application.backend.Colourspace
import application.backend.preprocess.PreprocessingNode
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Size

enum class Blurring {
    GAUSSIAN,
    MEDIAN,
    BOX_FILTER
}

data class BlurringNode(val blurType: Blurring = Blurring.GAUSSIAN, val kernelSize: Int = 3): PreprocessingNode() {
    override val name: String = "Blurring"
    override val help: String = "This node blurs the video to remove noise. The kernel size controls the extent of blurring and " +
            "can only be odd."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace get() = inputColourspace

    override fun process(img: Mat): Mat {
        val newImg = img.clone()

        when (blurType) {
            Blurring.GAUSSIAN -> GaussianBlur(img, newImg, Size(kernelSize, kernelSize), 0.0)
            Blurring.MEDIAN -> medianBlur(img, newImg, kernelSize)
            else -> blur(img, newImg, Size(kernelSize, kernelSize))
        }

        return newImg
    }
}
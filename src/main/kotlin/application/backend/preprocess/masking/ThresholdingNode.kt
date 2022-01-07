package application.backend.preprocess.masking

import application.backend.ALL_SPACES
import application.backend.Colourspace
import application.backend.preprocess.PreprocessingNode
import org.bytedeco.opencv.global.opencv_core.bitwise_and
import org.bytedeco.opencv.global.opencv_core.bitwise_or
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat

class ThresholdingNode(var minThreshold: Double, var maxThreshold: Double, var binarise: Boolean): PreprocessingNode() {
    override val name: String = "Thresholding"
    override val help: String = "Performs a black and white threshold on the image."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace get() = if (binarise) inputColourspace else Colourspace.GRAYSCALE

    override fun process(img: Mat): Mat {
        val mask = Mat()
        val newImg = img.clone()

        var gray = Mat()
        when (inputColourspace) {
            Colourspace.RGB -> cvtColor(newImg, gray, COLOR_RGB2GRAY)
            Colourspace.HSV -> {
                cvtColor(newImg, gray, COLOR_HSV2RGB)
                cvtColor(gray, gray, COLOR_RGB2GRAY)
            }
            else -> gray = newImg
        }

        threshold(gray, mask, minThreshold, maxThreshold, 1)

        val newerImg = Mat()
        if (!binarise) bitwise_and(img, img, newerImg, mask)
        return if (binarise) mask else newerImg
    }
}
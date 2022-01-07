package application.backend.preprocess.edge_detection

import application.backend.ALL_SPACES
import application.backend.Colourspace
import application.backend.preprocess.PreprocessingNode
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.global.opencv_imgproc.Canny
import org.bytedeco.opencv.opencv_core.Mat

class CannyEdgeNode(var threshold: Double = 200.0, var kernelSize: Int = 3): PreprocessingNode() {
    override val name: String = "Canny Edge \nDetection"
    override val help: String = "Detects edges in the image. Blurring first in recommended."

    override val inputColourspaces: List<Colourspace> = ALL_SPACES
    override val outputColourspace: Colourspace = Colourspace.GRAYSCALE

    override fun process(img: Mat): Mat {
        val newImg = img.clone()

        var gray = Mat()
        when (inputColourspace) {
            Colourspace.RGB -> opencv_imgproc.cvtColor(newImg, gray, opencv_imgproc.COLOR_RGB2GRAY)
            Colourspace.HSV -> {
                opencv_imgproc.cvtColor(newImg, gray, opencv_imgproc.COLOR_HSV2RGB)
                opencv_imgproc.cvtColor(gray, gray, opencv_imgproc.COLOR_RGB2GRAY)
            }
            else -> gray = newImg
        }

        val edges = Mat()
        Canny(gray, edges, threshold, 2 * threshold, kernelSize, false)

        return edges
    }
}
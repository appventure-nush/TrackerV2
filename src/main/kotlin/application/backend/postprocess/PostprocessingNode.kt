package application.backend.postprocess

import org.bytedeco.opencv.opencv_core.Mat

abstract class PostprocessingNode {
    val scale = 0.01  // 1 pixel is scale metres

    abstract val help: String
    abstract val entries: List<String>

    abstract fun process(img: Mat): List<Any>
}
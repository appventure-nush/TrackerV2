package application.backend.postprocess

import application.backend.Processing
import org.bytedeco.opencv.opencv_core.Mat

abstract class PostprocessingNode: Processing() {
    val scale = 0.01  // 1 pixel is scale metres
    abstract val entries: List<String>

    abstract fun process(img: Mat): List<Any>
}
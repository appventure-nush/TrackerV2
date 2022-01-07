package application.backend.postprocess

import org.bytedeco.opencv.opencv_core.Mat

abstract class PostprocessingNode {
    abstract val help: String
    abstract val entries: List<String>

    abstract fun process(img: Mat): List<Any>
}
package application.backend.preprocess

import org.bytedeco.opencv.opencv_core.Mat

abstract class PreprocessingNode {
    abstract val help: String

    abstract fun process(img: Mat): Mat
}
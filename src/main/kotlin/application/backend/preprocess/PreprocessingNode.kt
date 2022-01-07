package application.backend.preprocess

import application.backend.Colourspace
import application.backend.Processing
import org.bytedeco.opencv.opencv_core.Mat

abstract class PreprocessingNode: Processing() {
    abstract val outputColourspace: Colourspace

    abstract fun process(img: Mat): Mat
}
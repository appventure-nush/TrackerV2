package backend.preprocess

import backend.Colourspace
import backend.Processing
import org.bytedeco.opencv.opencv_core.Mat

abstract class PreprocessingNode: Processing() {
    abstract val outputColourspace: Colourspace

    abstract fun process(img: Mat): Mat
}
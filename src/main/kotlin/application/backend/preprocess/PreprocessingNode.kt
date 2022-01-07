package application.backend.preprocess

import application.backend.Colourspace
import org.bytedeco.opencv.opencv_core.Mat

abstract class PreprocessingNode {
    abstract val help: String

    var inputColourspace: Colourspace = Colourspace.RGB

    abstract val inputColourspaces: List<Colourspace>
    abstract val outputColourspace: Colourspace

    abstract fun process(img: Mat): Mat
}
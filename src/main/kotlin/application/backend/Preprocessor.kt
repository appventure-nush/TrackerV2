package application.backend

import application.backend.preprocess.PreprocessingNode
import org.bytedeco.opencv.opencv_core.Mat

class Preprocessor {
    val nodes: ArrayList<PreprocessingNode> = arrayListOf()

    fun process(img: Mat): Mat {
        var finalImg = img.clone()
        nodes.forEach { finalImg = it.process(finalImg) }

        return finalImg
    }
}
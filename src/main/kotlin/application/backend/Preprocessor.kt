package application.backend

import application.backend.preprocess.PreprocessingNode
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat

class Preprocessor {
    val nodes: ArrayList<PreprocessingNode> = arrayListOf()

    fun process(img: Mat): Mat {
        var finalImg = img.clone()
        var currentSpace = Colourspace.RGB

        nodes.forEach {
            if (it.inputColourspace != currentSpace) {
                when (currentSpace) {
                    Colourspace.RGB -> when (it.inputColourspace) {
                        Colourspace.HSV -> cvtColor(finalImg, finalImg, COLOR_RGB2HSV)
                        Colourspace.GRAYSCALE -> cvtColor(finalImg, finalImg, COLOR_RGB2GRAY)
                        else -> {}
                    }
                    Colourspace.HSV -> when (it.inputColourspace) {
                        Colourspace.RGB -> cvtColor(finalImg, finalImg, COLOR_HSV2RGB)
                        Colourspace.GRAYSCALE -> {
                            cvtColor(finalImg, finalImg, COLOR_HSV2RGB)
                            cvtColor(finalImg, finalImg, COLOR_RGB2GRAY)
                        }
                        else -> {}
                    }
                    Colourspace.GRAYSCALE -> when (it.inputColourspace) {
                        Colourspace.RGB -> cvtColor(finalImg, finalImg, COLOR_GRAY2RGB)
                        Colourspace.HSV -> {
                            cvtColor(finalImg, finalImg, COLOR_GRAY2RGB)
                            cvtColor(finalImg, finalImg, COLOR_RGB2HSV)
                        }
                        else -> {}
                    }
                }
            }

            finalImg = it.process(finalImg)
            currentSpace = it.outputColourspace
        }

        return finalImg
    }
}
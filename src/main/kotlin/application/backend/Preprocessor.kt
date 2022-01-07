package application.backend

import application.backend.preprocess.PreprocessingNode
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat

class Preprocessor {
    val nodes: ArrayList<PreprocessingNode> = arrayListOf()

    fun process(img: Mat): Mat {
        var newImg = img.clone()
        cvtColor(img, newImg, COLOR_BGR2RGB)

        var currentSpace = Colourspace.RGB

        nodes.forEach {
            if (it.inputColourspace != currentSpace) {
                when (currentSpace) {
                    Colourspace.RGB -> when (it.inputColourspace) {
                        Colourspace.HSV -> cvtColor(newImg, newImg, COLOR_RGB2HSV)
                        Colourspace.GRAYSCALE -> cvtColor(newImg, newImg, COLOR_RGB2GRAY)
                        else -> {}
                    }
                    Colourspace.HSV -> when (it.inputColourspace) {
                        Colourspace.RGB -> cvtColor(newImg, newImg, COLOR_HSV2RGB)
                        Colourspace.GRAYSCALE -> {
                            cvtColor(newImg, newImg, COLOR_HSV2RGB)
                            cvtColor(newImg, newImg, COLOR_RGB2GRAY)
                        }
                        else -> {}
                    }
                    Colourspace.GRAYSCALE -> when (it.inputColourspace) {
                        Colourspace.RGB -> cvtColor(newImg, newImg, COLOR_GRAY2RGB)
                        Colourspace.HSV -> {
                            cvtColor(newImg, newImg, COLOR_GRAY2RGB)
                            cvtColor(newImg, newImg, COLOR_RGB2HSV)
                        }
                        else -> {}
                    }
                }
            }

            newImg = it.process(newImg)
            currentSpace = it.outputColourspace
        }

        val finalImg = Mat()
        cvtColor(newImg, finalImg, COLOR_RGB2BGR)

        return finalImg
    }
}
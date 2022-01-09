package backend

import backend.postprocess.PostprocessingNode
import krangl.DataFrame
import krangl.dataFrameOf
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat

class Postprocessor(val node: PostprocessingNode) {
    fun process(videoCapture: Sequence<Mat>): DataFrame {
        val img = videoCapture.take(1).toList()[0]

        var data = dataFrameOf(node.entries)(node.process(convert(img, node.inputColourspace)))
        videoCapture.forEach { _ ->
            data = data.addRow(node.process(convert(img, node.inputColourspace)))
        }

        return data
    }

    fun convert(img: Mat, colour: Colourspace): Mat {
        val newImg = Mat()
        when (colour) {
            Colourspace.RGB -> cvtColor(img, newImg, COLOR_BGR2RGB)
            Colourspace.HSV -> cvtColor(img, newImg, COLOR_BGR2HSV)
            Colourspace.GRAYSCALE -> cvtColor(img, newImg, COLOR_BGR2GRAY)
        }

        return newImg
    }
}
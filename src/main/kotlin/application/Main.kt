package application

import application.backend.Postprocessor
import application.backend.Preprocessor
import application.backend.postprocess.PostprocessTest
import application.backend.preprocess.masking.ThresholdingNode
import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val preprocessor = Preprocessor()
            preprocessor.nodes.add(ThresholdingNode(100.0, 255.0, binarise = false))

            val postprocessor = Postprocessor(PostprocessTest())

            val video = VideoCapture(
                "C:\\Users\\jedli\\OneDrive - NUS High School\\Documents\\Physics\\SYPT 2022\\" +
                        "16. Saving Honey\\Experimental Data\\IMG_1911.MOV")

            val img = Mat()
            println(video.read(img))
            imwrite("test.png", preprocessor.process(img))

            postprocessor.process(sequence { while (video.read(img)) yield(img) })
        }
    }
}
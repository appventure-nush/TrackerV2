package application

import application.backend.Colourspace
import application.backend.Postprocessor
import application.backend.Preprocessor
import application.backend.postprocess.fitting.CircleFitting
import application.backend.preprocess.blurring.Blurring
import application.backend.preprocess.blurring.BlurringNode
import application.backend.preprocess.masking.ThresholdingNode
import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val preprocessor = Preprocessor()
            preprocessor.nodes.add(ThresholdingNode(100.0, 255.0, true))

            val postprocessor = Postprocessor(CircleFitting(20.0, param2 = 20.0))

            val video = VideoCapture(
                "C:\\Users\\jedli\\OneDrive - NUS High School\\Documents\\Physics\\SYPT 2022\\" +
                        "16. Saving Honey\\Experimental Data\\Anim2.mp4")

            val img = Mat()
            //imwrite("test.png", preprocessor.process(img))

            println(postprocessor.process(sequence {
                while (video.read(img)) yield(preprocessor.process(img))
            }))
        }
    }
}
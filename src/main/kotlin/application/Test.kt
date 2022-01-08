package application

import application.backend.Preprocessor
import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

class Test {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val preprocessor = Preprocessor()
            //preprocessor.nodes.add(ThresholdingNode(100.0, 255.0, true))

            val video = VideoCapture(
                "C:\\Users\\jedli\\OneDrive - NUS High School\\Documents\\Physics\\SYPT 2022\\" +
                        "16. Saving Honey\\Experimental Data\\Anim2.mp4")

            val img = Mat()
            video.read(img)
            imwrite("test.png", preprocessor.process(img))
        }
    }
}
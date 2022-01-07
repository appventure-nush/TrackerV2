package application

import application.backend.Postprocessor
import application.backend.postprocess.PostprocessTest
import org.bytedeco.opencv.opencv_videoio.VideoCapture

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val postprocessor = Postprocessor(PostprocessTest())
            println(postprocessor.process(VideoCapture(
                "C:\\Users\\jedli\\OneDrive - NUS High School\\Documents\\Physics\\SYPT 2022\\" +
                        "16. Saving Honey\\Experimental Data\\Anim2.mp4")))
        }
    }
}
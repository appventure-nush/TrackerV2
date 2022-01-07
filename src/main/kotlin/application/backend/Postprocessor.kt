package application.backend

import application.backend.postprocess.PostprocessingNode
import krangl.DataFrame
import krangl.dataFrameOf
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

class Postprocessor(val node: PostprocessingNode) {
    fun process(videoCapture: VideoCapture): DataFrame {
        val img = Mat()
        videoCapture.read(img)

        var data = dataFrameOf(node.entries)(node.process(img))
        while (videoCapture.read(img)) {
            data = data.addRow(node.process(img))
        }

        videoCapture.release()

        return data
    }
}
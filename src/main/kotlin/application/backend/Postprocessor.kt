package application.backend

import application.backend.postprocess.PostprocessingNode
import krangl.DataFrame
import krangl.dataFrameOf
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

class Postprocessor(val node: PostprocessingNode) {
    fun process(videoCapture: Sequence<Mat>): DataFrame {
        val img = videoCapture.take(1).toList()[0]

        var data = dataFrameOf(node.entries)(node.process(img))
        videoCapture.forEach { _ ->
            data = data.addRow(node.process(img))
        }

        return data
    }
}
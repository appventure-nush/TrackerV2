package backend

import org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_COUNT
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

/**
 * Represents a video
 */
class Video(val videoCapture: VideoCapture) : Iterator<Image> {
    private var nextImage: Mat = Mat()

    /**
     * The current image shown in the video
     */
    lateinit var currentImage: Image
        private set

    /**
     * The total number of frames of the video
     */
    val totalFrames: Int = videoCapture.get(CAP_PROP_FRAME_COUNT).toInt()

    /**
     * The current frame number of the video
     */
    var currentFrame: Int = 0
        private set

    /**
     * Imports a video from a [file]
     */
    constructor(file: String) : this(VideoCapture(file))

    /**
     * Moves the video to the specified [frameNumber]
     */
    fun seek(frameNumber: Int) {
        videoCapture.set(1, (frameNumber - 1).toDouble())
        currentFrame = frameNumber - 1
    }

    override fun hasNext(): Boolean = videoCapture.read(nextImage)

    override fun next(): Image {
        currentFrame++

        // Convert from BGR to RGB
        cvtColor(nextImage, nextImage, COLOR_BGR2RGB)
        currentImage = Image(Colourspace.RGB, nextImage)

        return currentImage
    }
}
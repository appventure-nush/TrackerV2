package backend

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.preprocess.Preprocessor
import org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FPS
import org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_COUNT
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

/**
 * Represents a video
 */
class Video(videoCapture: VideoCapture) : Iterator<Image> {
    private var nextImage: Mat = Mat()

    /**
     * The video that is currently loaded
     */
    var videoCapture: VideoCapture = videoCapture
        set(video) {
            field = video
            currentFrame = 0
            totalFrames = field.get(CAP_PROP_FRAME_COUNT).toInt()
            frameRate = field.get(CAP_PROP_FPS)
        }

    /**
     * The current image shown in the video
     */
    lateinit var currentImage: Image
        private set

    /**
     * The total number of frames of the video
     */
    var totalFrames: Int = videoCapture.get(CAP_PROP_FRAME_COUNT).toInt()
        private set

    /**
     * The preprocessor that preprocesses the video frames
     */
    val preprocesser: Preprocessor = Preprocessor()

    /**
     * The frame rate of the video
     */
    var frameRate: Double = videoCapture.get(CAP_PROP_FPS)
        private set

    /**
     * The postprocessors that convert the information in the video frames to data
     */
    val postprocessors: MutableList<Postprocessor> = arrayListOf()

    /**
     * The postprocessor that will be used in displaying the video frames
     */
    val focusedPostprocessor: Int = -1

    /**
     * The x coordinate of the origin point
     */
    var originX: MutableState<Float> = mutableStateOf(0.0f)

    /**
     * The y coordinate of the origin point
     */
    var originY: MutableState<Float> = mutableStateOf(0.0f)

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
        currentImage.origin = Point(originX.value.toDouble(), originY.value.toDouble())
        currentImage = preprocesser.process(currentImage)

        // Perform post-processing
        postprocessors.map { currentImage = it.process(currentImage, currentFrame / frameRate) }
        return currentImage
    }

    fun process() {
        while (hasNext()) next()
    }
}
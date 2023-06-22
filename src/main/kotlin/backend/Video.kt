package backend

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.preprocess.Preprocessor
import org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_videoio.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture

/**
 * Represents a video
 */
class Video(videoCapture: VideoCapture) : Iterator<Image> {
    private var nextImage: Mat = Mat()

    /**
     * The path to the current video file
     */
    var videoFile: String = ""

    /**
     * Should the external video be synced?
     */
    var syncing: MutableState<Boolean>? = null

    /**
     * The video that is currently loaded
     */
    var videoCapture: VideoCapture = videoCapture
        set(video) {
            field = video
            currentFrame = -1
            totalFrames = field.get(CAP_PROP_FRAME_COUNT).toInt()
            frameRate = field.get(CAP_PROP_FPS)

            hasNext()
            next()
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

    /**
     * The postprocessors that convert the information in the video frames to data
     */
    val postprocessors: MutableList<Postprocessor> = arrayListOf()

    /**
     * The x coordinate of the origin point
     */
    var originX: MutableState<Double> = mutableStateOf(0.0)

    /**
     * The y coordinate of the origin point
     */
    var originY: MutableState<Double> = mutableStateOf(0.0)

    /**
     * The scale of the video
     */
    var scale: MutableState<Double> = mutableStateOf(1.0)

    /**
     * The first x-coordinate of the bounding rectangle of the video
     */
    var cropX1: MutableState<Double> = mutableStateOf(0.0)

    /**
     * The first y-coordinate of the bounding rectangle of the video
     */
    var cropY1: MutableState<Double> = mutableStateOf(0.0)

    /**
     * The second x-coordinate of the bounding rectangle of the video
     */
    var cropX2: MutableState<Double> = mutableStateOf(videoCapture.get(CAP_PROP_FRAME_WIDTH))

    /**
     * The second y-coordinate of the bounding rectangle of the video
     */
    var cropY2: MutableState<Double> = mutableStateOf(videoCapture.get(CAP_PROP_FRAME_HEIGHT))

    /**
     * The current frame number of the video
     */
    var currentFrame: Int = 0
        private set

    /**
     * Imports a video from a [file]
     */
    constructor(file: String) : this(VideoCapture(file)) {
        videoFile = file
    }

    /**
     * Moves the video to the specified [frameNumber]
     */
    fun seek(frameNumber: Int) {
        println("Before: $currentFrame")
        if (currentFrame - frameNumber > 400) {
            syncing?.value = false
            videoCapture = VideoCapture(videoFile)
            syncing?.value = true
        }

        if (currentFrame == -1) currentFrame = 0
        else if (frameNumber != currentFrame) {
            videoCapture.set(1, (frameNumber - 1).toDouble())
            currentFrame = frameNumber - 1
        }

        println("After: $currentFrame")
    }

    override fun hasNext(): Boolean = videoCapture.read(nextImage)

    fun next(increment: Boolean): Image {
        if (increment) currentFrame++

        // Convert from BGR to RGB
        val nextImage2 = nextImage.clone()
        cvtColor(nextImage, nextImage, COLOR_BGR2RGB)
        currentImage = Image(Colourspace.RGB, nextImage)
        currentImage.origin = Point(originX.value.toDouble(), originY.value.toDouble())
        currentImage.scale = scale.value

        var cropped = currentImage.crop(cropX1.value, cropY1.value, cropX2.value, cropY2.value)
        cropped = preprocesser.process(cropped)

        nextImage = nextImage2

        // Perform post-processing
        postprocessors.map { cropped = it.process(cropped, currentFrame / frameRate) }
        return currentImage.expand(cropX1.value, cropY1.value, cropX2.value, cropY2.value, cropped)
    }

    override fun next(): Image = next(true)

    fun process() {
        while (hasNext()) next()
    }
}
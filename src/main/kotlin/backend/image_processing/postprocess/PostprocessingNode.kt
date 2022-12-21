package backend.image_processing.postprocess

import backend.Image
import backend.Point
import backend.image_processing.Processing
import kotlinx.serialization.Serializable

/**
 * The base class for all post-processing nodes
 */
@Serializable
sealed class PostprocessingNode: Processing() {
    /**
     * The scaling of the video. 1 px is to [scale] meters.
     */
    var scale = 1.0

    /**
     * The point of origin in the video (in pixels)
     */
    var origin = Point(0.0, 0.0)

    /**
     * The output information that will be provided
     */
    abstract val entries: List<String>

    /**
     * Processes the [img] and outputs the information stated in [entries] as well as an output image
     * which is used to indicate that the processing is working well
     */
    abstract fun process(img: Image): Pair<List<List<Any>>, Image>

    /** Converts a point in pixels to the same point in metres */
    fun position(point: Point) = (point - origin) * scale

    /**
     * Returns a deep copy of the postprocessing node
     */
    abstract fun clone(): PostprocessingNode
}
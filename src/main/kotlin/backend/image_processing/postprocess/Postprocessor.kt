package backend.image_processing.postprocess

import backend.Image
import krangl.DataFrame
import krangl.dataFrameOf
import org.bytedeco.opencv.global.opencv_imgproc.*

/**
 * Performs postprocessing on the images and converts the outputs into a DataFrame that
 * can be exported as a CSV file.
 */
class Postprocessor(val node: PostprocessingNode) {
    /**
     * The data will be eventually be outputted to a csv
     */
    lateinit var data: DataFrame

    /**
     * Preprocesses the image with the [node]
     */
    fun process(img: Image): Image {
        // Clone the image and convert to the correct colourspace
        val newImg = img.clone()
        newImg.colourspace = node.inputColourspace

        val (row, newerImg) = node.process(newImg)

        // Adding to dataframe
        data = if (this::data.isInitialized) {
            data.addRow(row)
        } else dataFrameOf(node.entries)(row)

        return newerImg
    }
}
package backend.image_processing.postprocess

import backend.Image
import krangl.DataFrame
import krangl.dataFrameOf
import krangl.writeCSV
import java.io.File

/**
 * Performs postprocessing on the images and converts the outputs into a DataFrame that
 * can be exported as a CSV file.
 */
class Postprocessor(val node: PostprocessingNode) {
    /**
     * The data will be eventually be outputted to a csv
     */
    private var data: DataFrame? = null

    /**
     * The entries of the post-processing node, including time
     */
    val entries: List<String> = listOf("time") + node.entries

    /**
     * Preprocesses the image with the [node]
     */
    fun process(img: Image, time: Double): Image {
        // Clone the image and convert to the correct colourspace
        val newImg = img.clone()
        newImg.colourspace = node.inputColourspace

        val (rows, newerImg) = node.process(newImg)

        // Adding to dataframe
        rows.forEach {
            val rowWithTime = listOf(time) + it
            data = data?.addRow(rowWithTime) ?: (dataFrameOf(entries)(rowWithTime))
        }

        return newerImg
    }

    /**
     * Exports the data as a csv
     */
    fun export(file: File) {
        require(data != null) { "There is no data to export." }
        data!!.writeCSV(file)
    }

    /**
     * Removes all the data
     */
    fun clear() {
        data = null
    }
}
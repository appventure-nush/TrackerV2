package application.backend.preprocess.masking

import application.backend.Colourspace
import application.backend.preprocess.PreprocessingNode
import javafx.scene.paint.Color
import org.bytedeco.opencv.global.opencv_core.inRange
import org.bytedeco.opencv.opencv_core.Mat

data class ColourRangeNode(var colours: ArrayList<Pair<Color, Color>>, var binarise: Boolean): PreprocessingNode() {
    override val name: String = "Filter Colours"
    override val help: String = "Filters out parts of the images within the given colour range."

    override val inputColourspaces: List<Colourspace> = listOf(Colourspace.RGB, Colourspace.HSV)
    override val outputColourspace: Colourspace get() = inputColourspace

    override fun process(img: Mat): Mat {
        val mask = Mat()
        var newImg = img.clone()
        colours.forEach { (start, end) ->
            when (inputColourspace) {
                Colourspace.RGB -> inRange(newImg, Mat(start.red * 255, start.green * 255, start.blue * 255),
                    Mat(end.red * 255, end.green * 255, end.blue * 255), mask)
                Colourspace.HSV -> inRange(newImg, Mat(start.hue / 360 * 255, start.saturation / 360 * 255, start.brightness / 360 * 255),
                    Mat(end.hue / 360 * 255, end.saturation / 360 * 255, end.brightness / 360 * 255), mask)
                else -> {}
            }

            val newerImg = Mat()
            newImg.copyTo(newerImg, mask)
            newImg = newerImg
        }

        return if (binarise) mask else newImg
    }
}
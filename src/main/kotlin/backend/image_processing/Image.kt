package backend.image_processing

import backend.Colourspace
import backend.Point
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.HSV
import com.github.ajalt.colormath.model.RGB
import org.bytedeco.javacpp.indexer.UByteIndexer
import org.bytedeco.opencv.global.opencv_core.*
import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_core.Point as cvPoint


/**
 * Represents an image
 */
class Image(colourspace: Colourspace, img: Mat) {
    /**
     * The scale of the image (1 pixel is [scale] metres)
     */
    var scale = 1.0

    /**
     * The angle that the x axis makes with the horizontal. Take anti-clockwise to be positive.
     */
    var angle = 0.0

    /**
     * The position of the origin
     */
    var origin = Point(0.0, 0.0)

    /**
     * The colourspace of the image
     */
    var colourspace: Colourspace = colourspace
        set(value) {
            when (field) {
                Colourspace.RGB -> when (value) {
                    Colourspace.HSV -> cvtColor(img, img, COLOR_RGB2HSV)
                    Colourspace.GRAYSCALE -> cvtColor(img, img, COLOR_RGB2GRAY)
                    else -> {}
                }
                Colourspace.HSV -> when (value) {
                    Colourspace.RGB -> cvtColor(img, img, COLOR_HSV2RGB)
                    Colourspace.GRAYSCALE -> {
                        cvtColor(img, img, COLOR_HSV2RGB)
                        cvtColor(img, img, COLOR_RGB2GRAY)
                    }
                    else -> {}
                }
                Colourspace.GRAYSCALE -> when (value) {
                    Colourspace.RGB -> cvtColor(img, img, COLOR_GRAY2RGB)
                    Colourspace.HSV -> {
                        cvtColor(img, img, COLOR_GRAY2RGB)
                        cvtColor(img, img, COLOR_RGB2HSV)
                    }
                    else -> {}
                }
            }

            field = value
        }

    /**
     * The OpenCV image
     */
    var img: Mat = img
        private set(value) {
            field = value
            indexer = field.createIndexer()
        }

    private var indexer: UByteIndexer = img.createIndexer()

    constructor(path: String) : this(Colourspace.RGB, with(null) {
        val img = imread(path)
        cvtColor(img, img, COLOR_BGR2RGB)

        img
    })

    /* Pixel Manipulation */

    /**
     * Returns the colour of the pixel at the given [point]
     */
    operator fun get(point: Point): Color {
        val point2 = fromScaled(point)

        return when (colourspace) {
            Colourspace.RGB -> RGB.from255(indexer.get(point2.y.toLong(), point2.x.toLong(), 0),
                indexer.get(point2.y.toLong(), point2.x.toLong(), 1),
                indexer.get(point2.y.toLong(), point2.x.toLong(), 2))
            Colourspace.HSV -> HSV(indexer.get(point2.y.toLong(), point2.x.toLong(), 0) / 255.0 * 360,
                indexer.get(point2.y.toLong(), point2.x.toLong(), 1) / 255.0,
                indexer.get(point2.y.toLong(), point2.x.toLong(), 2) / 255.0)
            Colourspace.GRAYSCALE -> RGB.from255(indexer.get(point2.y.toLong(), point2.x.toLong()),
                indexer.get(point2.y.toLong(), point2.x.toLong()),
                indexer.get(point2.y.toLong(), point2.x.toLong()))
        }
    }

    /**
     * Returns the colour at the given [x] and [y] coordinates
     */
    operator fun get(x: Double, y: Double) = this[Point(x, y)]

    /**
     * Returns the colour of the pixel at the given [point]
     */
    operator fun set(point: Point, colour: Color) {
        val point2 = fromScaled(point)
        when (colourspace) {
            Colourspace.RGB -> {
                val rgb = colour.toSRGB()
                indexer.put(point2.x.toLong(), point2.y.toLong(), 0, (rgb.r * 255).toInt())
                indexer.put(point2.x.toLong(), point2.y.toLong(), 1, (rgb.g * 255).toInt())
                indexer.put(point2.x.toLong(), point2.y.toLong(), 2, (rgb.b * 255).toInt())
            }
            Colourspace.HSV -> {
                val hsv = colour.toHSV()
                indexer.put(point2.x.toLong(), point2.y.toLong(), 0, (hsv.h / 360.0 * 255).toInt())
                indexer.put(point2.x.toLong(), point2.y.toLong(), 1, (hsv.s * 255).toInt())
                indexer.put(point2.x.toLong(), point2.y.toLong(), 2, (hsv.v * 255).toInt())
            }
            else -> {
                val rgb = colour.toSRGB()
                indexer.put(point2.x.toLong(), point2.y.toLong(), (rgb.r * 255).toInt())
            }
        }

        indexer = img.createIndexer()
    }

    /**
     * Returns the colour at the given [x] and [y] coordinates
     */
    operator fun set(x: Double, y: Double, colour: Color) {
        this[Point(x, y)] = colour
    }

    /* Blurring */

    /**
     * Performs a gaussian blur on the image
     * @param kernelSize The kernel size of the gaussian kernel
     * @param sigma The standard deviation of the gaussian kernel.
     * If set to 0, it is automatically calculated.
     */
    fun gaussianBlur(kernelSize: Int, sigma: Double = 0.0): Image {
        require(kernelSize >= 3 && kernelSize % 2 == 1) { "Kernel size must be odd and larger than or equal to 3" }

        GaussianBlur(img, img, Size(kernelSize, kernelSize), sigma)
        indexer = img.createIndexer()

        return this
    }

    /**
     * Blurs the image using a box filter convolution of the given [kernelSize]
     */
    fun boxFilter(kernelSize: Int): Image {
        blur(img, img, Size(kernelSize, kernelSize))
        indexer = img.createIndexer()

        return this
    }

    /**
     * Performs a median blur on the image
     */
    fun medianBlur(kernelSize: Int): Image {
        medianBlur(img, img, kernelSize)
        indexer = img.createIndexer()

        return this
    }

    /* Masking */

    /**
     * Applies the given [mask] to the image
     */
    fun applyMask(mask: Image): Image = applyMask(mask.img)

    /**
     * Applies the given [mask] to the image
     */
    private fun applyMask(mask: Mat): Image {
        bitwise_and(img, img, img, mask)
        return this
    }

    /**
     * Filters out all pixels dimmer than [minThreshold] and brighter than [maxThreshold].
     * If [binarise] is true, the image will be converted to a binary mask.
     * If not the resulting binary mask is applied to the original image.
     */
    fun threshold(minThreshold: Double, maxThreshold: Double = 255.0, binarise: Boolean = true): Image {
        val mask = Mat()
        val newImg = img.clone()

        // Converting to grayscale
        var gray = Mat()
        when (colourspace) {
            Colourspace.RGB -> cvtColor(newImg, gray, COLOR_RGB2GRAY)
            Colourspace.HSV -> {
                cvtColor(newImg, gray, COLOR_HSV2RGB)
                cvtColor(gray, gray, COLOR_RGB2GRAY)
            }
            else -> gray = newImg
        }

        // Perform thresholding
        threshold(gray, mask, minThreshold, maxThreshold, 1)

        // Apply mask on the image if necessary
        if (binarise) img = mask else applyMask(mask)

        indexer = img.createIndexer()
        return this
    }

    /**
     * Filters out all colours not within the ranges specified in [colours]
     * If [binarise] is true, the image will be converted to a binary mask.
     * If not the resulting binary mask is applied to the original image.
     */
    fun colourFilter(colours: List<Pair<Color, Color>>, binarise: Boolean = true): Image {
        val mask = Mat()
        var newImg = img.clone()
        colours.forEach { (start, end) ->
            when (colourspace) {
                Colourspace.RGB -> inRange(newImg, Mat(start.toSRGB().r * 255, start.toSRGB().g * 255, start.toSRGB().b * 255),
                    Mat(end.toSRGB().r * 255, end.toSRGB().g * 255, end.toSRGB().b * 255), mask)
                Colourspace.HSV -> {
                    inRange(newImg, Mat(start.toHSV().h / 360 * 255, start.toHSV().s * 255, start.toHSV().v * 255),
                        Mat(end.toHSV().h / 360 * 255, end.toHSV().s / 360 * 255, end.toHSV().v / 360 * 255), mask)
                }
                else -> {}
            }

            val newerImg = Mat()
            newImg.copyTo(newerImg, mask)
            newImg = newerImg
        }

        if (binarise) img = mask else applyMask(mask)

        indexer = img.createIndexer()
        return this
    }

    /* Morphological Transformations */

    /**
     * Performs erosion with the specified [kernelSize] [iterations] times.
     */
    fun erode(kernelSize: Int, iterations: Int = 1): Image {
        erode(img, img, getStructuringElement(
            CV_SHAPE_RECT, Size(2 * kernelSize + 1, 2 * kernelSize + 1),
            cvPoint(kernelSize, kernelSize)
        ), cvPoint(-1, -1), iterations, BORDER_CONSTANT, null)

        indexer = img.createIndexer()
        return this
    }

    /**
     * Performs dilation with the specified [kernelSize] [iterations] times.
     */
    fun dilate(kernelSize: Int, iterations: Int = 1): Image {
        dilate(img, img, getStructuringElement(
            CV_SHAPE_RECT, Size(2 * kernelSize + 1, 2 * kernelSize + 1),
            cvPoint(kernelSize, kernelSize)
        ), cvPoint(-1, -1), iterations, BORDER_CONSTANT, null)

        indexer = img.createIndexer()
        return this
    }

    /* Edge Detection */

    /**
     * Performs canny edge detection on the image with given [lowerThreshold] and [upperThreshold].
     * @param kernelSize The size of the kernel used for the edge detection
     */
    fun cannyEdge(lowerThreshold: Double, upperThreshold: Double = lowerThreshold * 2, kernelSize: Int = 3): Image {
        var gray = Mat()
        when (colourspace) {
            Colourspace.RGB -> cvtColor(img, gray, COLOR_RGB2GRAY)
            Colourspace.HSV -> {
                cvtColor(img, gray, COLOR_HSV2RGB)
                cvtColor(gray, gray, COLOR_RGB2GRAY)
            }
            else -> gray = img
        }

        Canny(gray, img, lowerThreshold, upperThreshold, kernelSize, false)

        return this
    }

    /* Fitting Shapes */

    /* Misc */

    /**
     * Writes the image to the given [path]
     */
    fun write(path: String) {
        val newImg = Mat()
        cvtColor(img, newImg, COLOR_RGB2BGR)

        imwrite(path, newImg)
    }

    /**
     * Returns a deep copy of the image
     */
    fun clone() = Image(colourspace, img.clone())

    /**
     * Returns the unscaled point given the scaled [point]
     */
    private fun fromScaled(point: Point) = (point - origin) * scale

    /**
     * Returns the unscaled point given the scaled [x] and [y] coordinates
     */
    private fun fromScaled(x: Double, y: Double) = fromScaled(Point(x, y))
}
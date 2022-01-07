package application.backend

import org.bytedeco.javacv.*
import org.bytedeco.opencv.opencv_core.Mat

fun convertToImage(mat: Mat) =  JavaFXFrameConverter().convert(OpenCVFrameConverter.ToMat().convert(mat))

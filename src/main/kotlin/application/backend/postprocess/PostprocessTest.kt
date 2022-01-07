package application.backend.postprocess

import org.bytedeco.opencv.opencv_core.Mat
import kotlin.random.Random

class PostprocessTest: PostprocessingNode() {
    override val help = "I'm useless"
    override val entries: List<String> = listOf("Useless")

    override fun process(img: Mat): List<Any> = listOf(Random.nextInt())
}
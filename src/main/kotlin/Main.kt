import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import backend.Video
import backend.clearAndAddAll
import backend.image_processing.postprocess.PostprocessingNode
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.preprocess.Preprocessor
import gui.Axes
import gui.NodesPane
import gui.VideoPlayer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat
import org.bytedeco.opencv.opencv_videoio.VideoCapture
import java.awt.FileDialog
import java.io.File
import kotlin.random.Random


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
fun main() {
    val video = Video("IMG_3782.mov")
    video.hasNext()
    video.next().write("test.bmp")

    return application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        val width = remember { mutableStateOf(900.dp) }
        val windowWidth = remember { mutableStateOf(windowState.size.width) }

        val onUpdate = remember { mutableStateOf(0) }

        val isAxesVisible = remember { mutableStateOf(false) }

        val originX = remember { mutableStateOf(0.0f) }
        val originY = remember { mutableStateOf(0.0f) }
        video.originX = originX
        video.originY = originY

        val aboutDialog = remember { mutableStateOf(false) }
        val aboutTimes = remember { mutableStateOf(0) }

        val syncing = remember { mutableStateOf(false) }

        val icon = painterResource("trackerv2.png")

        Window(
            onCloseRequest = ::exitApplication,
            title = "Tracker 2.0",
            state = windowState,
            icon = icon
        ) {
            LaunchedEffect(windowState) {
                snapshotFlow { windowState.size }
                    .onEach { windowWidth.value = it.width }
                    .launchIn(this)
            }

            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item(
                        "Open Video",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Open Configuration", FileDialog.LOAD)
                            dialog.file = "*.mp4;*.mov;*avi"
                            dialog.isVisible = true

                            if (dialog.file != null) {
                                syncing.value = false
                                video.videoCapture = VideoCapture(dialog.directory + "/" + dialog.file)
                                syncing.value = true
                            }
                        }
                    )
                    Separator()
                    Item(
                        "Open Configuration",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Open Configuration", FileDialog.LOAD)
                            dialog.file = "*.trk2"
                            dialog.isVisible = true

                            if (dialog.file != null) {
                                val text = File(dialog.directory + "/" + dialog.file).readText().split("\n")
                                val newPreprocessingNodes = Json.decodeFromString<Preprocessor>(text[0]).nodes
                                val newPostprocessingNodes = Json.decodeFromString<List<PostprocessingNode>>(text[1])

                                video.preprocesser.nodes.clear()
                                video.preprocesser.nodes.addAll(newPreprocessingNodes)

                                video.postprocessors.clear()
                                newPostprocessingNodes.forEach {
                                    video.postprocessors.add(Postprocessor(it))
                                }

                                onUpdate.value = Random.nextInt(100)
                            }
                        },
                        shortcut = KeyShortcut(Key.O, ctrl = true)
                    )
                    Item(
                        "Save Configuration",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Save Configuration", FileDialog.SAVE)
                            dialog.file = "*.trk2"
                            dialog.isVisible = true

                            if (dialog.file != null) {
                                val serialisedPreprocessor = Json.encodeToString(video.preprocesser)
                                val serialisedPostprocessor = Json.encodeToString(video.postprocessors.map { it.node })
                                File(dialog.directory + "/" + dialog.file).writeText(
                                    serialisedPreprocessor + "\n" + serialisedPostprocessor
                                )
                            }
                        },
                        shortcut = KeyShortcut(Key.S, ctrl = true)
                    )
                }
                Menu("View", mnemonic = 'V') {
                    Item(
                        "Toggle Axes Visibility",
                        onClick = {
                            isAxesVisible.value = !isAxesVisible.value
                        }
                    )
                }
                Menu("Batch", mnemonic = 'B') {
                    Item(
                        "Batch Current Configuration",
                        onClick = {
                            val dialog = FileDialog(ComposeWindow(), "Open Files", FileDialog.LOAD).apply {
                                file = "*.mp4;*.mov;*avi"
                                isMultipleMode = true
                                isVisible = true
                            }

                            if(dialog.files != null && dialog.files.isNotEmpty()) {
                                // The Configuration has been decided.
                                for(file in dialog.files) {
                                    val filename = file.absolutePath.split(".")[0]
                                    val tmpVideo = Video(file.absolutePath).apply {
                                        preprocesser.nodes.clearAndAddAll(video.preprocesser.nodes)
                                        postprocessors.clearAndAddAll(video.postprocessors)
                                        process()
                                        postprocessors.mapIndexed { index, postprocessor ->
                                            postprocessor.export(File("${filename}_processed${index}.csv"))
                                        }
                                    }
                                }
                            }


                        }
                    )
//                    Item(
//                        "Batch Another Configuration...",
//                        onClick = {
//                        }
//                    )
                }
                Menu("About", mnemonic = 'A') {
                    Item(
                        "About TrackerV2",
                        onClick = {
                            aboutDialog.value = true
                        }
                    )
                }
            }

            MaterialTheme {
                Row(modifier = Modifier.padding(10.dp)) {
                    VideoPlayer(video, width, syncing)

                    Button(
                        modifier = Modifier.fillMaxHeight()
                            .width(1.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    width.value += dragAmount.x.toDp()
                                }
                            },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                        onClick = {}
                    ) {
                        Divider(
                            color = Color.Red,
                            modifier = Modifier.fillMaxHeight().width(1.dp)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        NodesPane(video, windowWidth, width, onUpdate, syncing)
                    }
                }
            }

            if (isAxesVisible.value) Axes(originX, originY)

            if (aboutDialog.value) {
                if (aboutTimes.value <= 5) {
                    AlertDialog(
                        title = { Text("About") },
                        text = {
                            Text("""
                        Tracker but better! This application is brought to you by AppVenture, the CS Interest Group
                        as well as your SYPT / IYPT alumni.
                        It was created and is maintained by Jed, with the help of Luc, Kabir and Prannaya.
                        """.trimIndent().replace("\n", " "))
                        },
                        confirmButton = {
                            TextButton({
                                aboutDialog.value = false
                                aboutTimes.value++
                            }) { Text("Ok") }
                        },
                        onDismissRequest = {
                            aboutDialog.value = false
                            aboutTimes.value++
                        },
                        modifier = Modifier.size(300.dp, 300.dp).padding(10.dp)
                    )
                } else {
                    AlertDialog(
                        title = { Text(problems[index].first) },
                        text = { Text(problems[index].second) },
                        confirmButton = {
                            TextButton({ aboutDialog.value = false }) { Text("Ok") }
                            index = Random.nextInt(problems.size)
                        },
                        onDismissRequest = {
                            aboutDialog.value = false
                            index = Random.nextInt(problems.size)
                        },
                        modifier = Modifier.size(300.dp, 300.dp).padding(10.dp)
                    )
                }
            }
        }
    }
}

val problems = mapOf(
    "1. Fractal Fingers" to """
        The effect of fractal fingering can be observed if a droplet of an ink-alcohol mixture is deposited onto diluted acrylic paint. 
        How are the geometry and dynamics of the fingers influenced by relevant parameters?
    """.trimIndent().replace("\n", ""),
    "2. Oscillating Sphere" to """
        A light sphere with a conducting surface is suspended from a thin wire. 
        When the sphere is rotated about its vertical axis (thereby twisting the wire) and then released, 
        it starts to oscillate. Investigate how the presence of a magnetic field affects the motion.
    """.trimIndent().replace("\n", ""),
    "3. Siren" to """
        If you direct an air flow onto a rotating disk with holes, a sound may be heard. 
        Explain this phenomenon and investigate how the sound characteristics depend on the relevant parameters.
    """.trimIndent().replace("\n", ""),
    "4. Coloured Line" to """
        When a compact disc or DVD is illuminated with light coming from a filament lamp in such a way that only rays 
        with large angles of incidence are selected, a clear green line can be observed. The colour varies upon 
        slightly changing the angle of the disc. Explain and investigate this phenomenon.
    """.trimIndent().replace("\n", ""),
    "5. Whistling Mesh" to """
        When a stream of water hits a rigid metal mesh within a range of angles, a whistling tone may be heard. 
        Investigate how the properties of the mesh, stream and angle affect the characteristics of the sound produced.
    """.trimIndent().replace("\n", ""),
    "6. Magnetic-Mechanical Oscillator" to """
        Secure the lower ends of two identical leaf springs to a non-magnetic base and attach magnets to the upper 
        ends such that they repel and are free to move. Investigate how the movement of the springs depends 
        on relevant parameters.
    """.trimIndent().replace("\n", ""),
    "7. Faraday Waves" to """
        A droplet of less viscous liquid floating in a bath of a more viscous liquid develops surprising wave-like 
        patterns when the entire system is set into vertical oscillation. Investigate this phenomenon and the 
        parameters relevant to the production of stable patterns.
    """.trimIndent().replace("\n", ""),
    "8. Euler's Pendulum" to """
        Take a thick plate of non-magnetic material and fix a neodymium magnet on top of it. Suspend a 
        magnetic rod (which can be assembled from cylindrical neodymium magnets) underneath it. Deflect the 
        rod so that it touches the plate only with highest edge and release it. Study the motion of such a pendulum 
        under various conditions.
    """.trimIndent().replace("\n", ""),
    "9. Oscillating Screw" to """
        When placed on its side on a ramp and released, a screw may experience growing oscillations as it travels down 
        the ramp. Investigate how the motion of the screw, as well as the growth of these oscillations 
        depend on the relevant parameters.
    """.trimIndent().replace("\n", ""),
    "10. Upstream Flow" to """
        Sprinkle light particles on a water surface. Then allow a water stream to be incident on the surface from a 
        small height. Under certain conditions, the particles may begin to move up the stream. Investigate and explain 
        this phenomenon.
    """.trimIndent().replace("\n", ""),
    "11. Ball on Ferrite Rod" to """
        A ferrite rod is placed at the bottom end of a vertical tube. Apply an ac voltage, of a frequency of the 
        same order as the natural frequency of the rod, to a fine wire coil wrapped around its lower end. 
        When a ball is placed on top of the rod, it will start to bounce. Explain and investigate this phenomenon.
    """.trimIndent().replace("\n", ""),
    "12. Rice Kettlebells" to """
        Take a vessel and pour some granular material into it, for example, rice. If you dip e.g. a spoon into it, 
        then at a certain depth of immersion, you can lift the vessel and contents by holding the spoon. 
        Explain this phenomenon and explore the relevant parameters of the system.
    """.trimIndent().replace("\n", ""),
    "13. Ponyo’s Heat Tube" to """
        A glass tube with a sealed top is filled with water and mounted vertically. The bottom end of the tube is 
        immersed in a beaker of water and a short segment of the tube is heated. Investigate and explain the 
        periodic motion of the water and any vapour bubbles observed.
    """.trimIndent().replace("\n", ""),
    "14. Jet Refraction" to """
        A vertical jet can be refracted when passing through an inclined sieve with a fine mesh. 
        Propose a law for such refraction and investigate relevant parameters.
    """.trimIndent().replace("\n", ""),
    "15. Pancake Rotation" to """
        Place a few balls in a round container. If you move the container around a vertical axis, the balls can move 
        co-directionally with the movement of the container, or they can move in the opposite direction. 
        Explain this phenomenon and investigate how the direction of movement depends on relevant parameters.
    """.trimIndent().replace("\n", ""),
    "16. Thermoasoutic Engine" to """
        A piston placed in the open end of a horizontal test tube which has its other end partially filled with steel 
        wool may oscillate when the closed end is heated up. Investigate the phenomenon and determine the 
        efficiency of this engine.
    """.trimIndent().replace("\n", ""),
    "17. Arrester Bed" to """
        A sand-filled lane results in the dissipation of the kinetic energy of a moving vehicle. What length is 
        necessary for such an arrester bed to entirely stop a passively moving object (e.g. a ball)? 
        What parameters does the length depend on?
    """.trimIndent().replace("\n", "")
).toList()
var index = Random.nextInt(problems.size)

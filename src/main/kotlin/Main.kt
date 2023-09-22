import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import backend.Video
import backend.clearAndAddAll
import backend.image_processing.postprocess.PostprocessingNode
import backend.image_processing.postprocess.Postprocessor
import backend.image_processing.preprocess.Preprocessor
import gui.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bytedeco.opencv.global.opencv_videoio
import org.bytedeco.opencv.opencv_videoio.VideoCapture
import java.awt.FileDialog
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.random.Random


fun main() {
    val video = Video("video0.mov")
    video.hasNext()
    video.next().write("test.bmp")

    return application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        val width = remember { mutableStateOf(900.dp) }
        val windowWidth = remember { mutableStateOf(windowState.size.width) }

        val videoWidth = remember { mutableStateOf(video.videoCapture.get(opencv_videoio.CAP_PROP_FRAME_WIDTH)) }

        val onUpdate = remember { mutableStateOf(0) }

        val constant = with(LocalDensity.current) { 1.dp.toPx() }
        val scalingConstant = remember {
            derivedStateOf {
                ((width.value.value - 20) * constant / videoWidth.value).coerceAtMost(1.0) * 3/2
            }
        }

        val isAxesVisible = remember { mutableStateOf(false) }
        val croppingRectangleVisible = remember { mutableStateOf(false) }

        val originX = remember { derivedStateOf { video.originX.value * scalingConstant.value } }
        val originY = remember { derivedStateOf { video.originY.value * scalingConstant.value } }

        val isCalibrationVisible = remember { mutableStateOf(false) }

        val videoCalibrationX1 = remember { mutableStateOf(0.0) }
        val videoCalibrationY1 = remember { mutableStateOf(0.0) }
        val videoCalibrationX2 = remember { mutableStateOf(100.0 / 2.0.pow(0.5)) }
        val videoCalibrationY2 = remember { mutableStateOf(100.0 / 2.0.pow(0.5)) }

        val calibrationX1 = remember { derivedStateOf { videoCalibrationX1.value * scalingConstant.value } }
        val calibrationY1 = remember { derivedStateOf { videoCalibrationY1.value * scalingConstant.value } }
        val calibrationX2 = remember { derivedStateOf { videoCalibrationX2.value * scalingConstant.value } }
        val calibrationY2 = remember { derivedStateOf { videoCalibrationY2.value * scalingConstant.value } }

        val mValue = remember { mutableStateOf(1.0) }

        val cropX1 = remember { derivedStateOf { video.cropX1.value * scalingConstant.value } }
        val cropY1 = remember { derivedStateOf { video.cropY1.value * scalingConstant.value } }
        val cropX2 = remember { derivedStateOf { video.cropX2.value * scalingConstant.value } }
        val cropY2 = remember { derivedStateOf { video.cropY2.value * scalingConstant.value } }

        val graphs = mutableListOf<GraphData>()

        val aboutDialog = remember { mutableStateOf(false) }
        val aboutTimes = remember { mutableStateOf(0) }

        val fpsDialog = remember { mutableStateOf(false) }
        val fps = remember { mutableStateOf(video.frameRate.toString()) }

        val batchDialog = remember { mutableStateOf(false) }
        val batchFile = remember { mutableStateOf("") }
        val batchTask = remember { mutableStateOf("") }
        val batchProgress = remember { mutableStateOf(0.0F) }
        val batchStartTime = remember { mutableStateOf(0L) }

        val syncing = remember { mutableStateOf(false) }
        video.syncing = syncing

        val icon = painterResource("trackerv2.png")

        AppTheme {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Tracker 2.0",
                state = windowState,
                icon = icon,
            ) {
                LaunchedEffect(windowState) {
                    snapshotFlow { windowState.size }
                        .onEach { windowWidth.value = it.width }
                        .launchIn(this)
                }

                Scaffold {
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

                                        video.cropX1.value = 0.0
                                        video.cropY1.value = 0.0
                                        video.cropX2.value = 1.0
                                        video.cropY2.value = 1.0

                                        video.videoCapture = VideoCapture(dialog.directory + "/" + dialog.file)
                                        video.videoFile = dialog.directory + "/" + dialog.file

                                        video.cropX1.value = 0.0
                                        video.cropY1.value = 0.0
                                        video.cropX2.value = video.videoCapture.get(opencv_videoio.CAP_PROP_FRAME_WIDTH)
                                        video.cropY2.value = video.videoCapture.get(opencv_videoio.CAP_PROP_FRAME_HEIGHT)

                                        videoWidth.value = video.videoCapture.get(opencv_videoio.CAP_PROP_FRAME_WIDTH)

                                        syncing.value = true

                                        fps.value = video.frameRate.toString()
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
                            Item(
                                "Toggle Cropping Rectangle",
                                onClick = {
                                    croppingRectangleVisible.value = !croppingRectangleVisible.value
                                }
                            )
                            Item(
                                "Toggle Calibration Stick",
                                onClick = {
                                    isCalibrationVisible.value = !isCalibrationVisible.value
                                }
                            )
                        }
                        Menu("Track", mnemonic = 'T') {
                            Item(
                                "Set FPS",
                                onClick = {
                                    fpsDialog.value = true
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

                                    if (dialog.files != null && dialog.files.isNotEmpty()) {
                                        batchDialog.value = true
                                        batchStartTime.value = System.currentTimeMillis() / 1000
                                        batchProgress.value = 0F

                                        val numFiles = dialog.files.size
                                        val perFileProgress = 1F / numFiles
                                        thread(start = true) {
                                            dialog.files.map { file ->
                                                val filename = file.absolutePath.split(".")[0]
                                                batchFile.value = file.absolutePath
                                                Video(file.absolutePath).apply {
                                                    preprocesser.nodes.clearAndAddAll(video.preprocesser.nodes)
                                                    postprocessors.clearAndAddAll(video.postprocessors)
                                                    batchTask.value = "Running through Video"

                                                    process()

                                                    val numProcessors = postprocessors.size
                                                    val perProcessorProgress = perFileProgress / numProcessors
                                                    postprocessors.map { postprocessor ->
                                                        batchTask.value = "Exporting for " + postprocessor.node.name
                                                        val task = postprocessor.node.name.lowercase().replace(" ", "_")
                                                        postprocessor.export(File("${filename}_processed_${task}.csv"))

                                                        batchProgress.value += perProcessorProgress
                                                    }
                                                }
                                            }

                                            batchDialog.value = false
                                        }
                                    }
                                }
                            )
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

                    Row(modifier = Modifier.padding(10.dp)) {
                        VideoPlayer(video, width, syncing, onUpdate)

                        Button(
                            modifier = Modifier.fillMaxHeight()
                                .width(1.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        width.value += dragAmount.x.toDp()
                                    }
                                },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                            onClick = {}
                        ) {
                            Divider(
                                color = Color.Red,
                                modifier = Modifier.fillMaxHeight().width(1.dp)
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            NodesPane(video, graphs, windowWidth, width, onUpdate, syncing)
                        }
                    }

                    if (isAxesVisible.value) Axes(originX, originY, scalingConstant, video)

                    if (croppingRectangleVisible.value) CroppingRectangle(cropX1, cropY1, cropX2, cropY2, scalingConstant, video)

                    if (isCalibrationVisible.value) Tape(
                        calibrationX1, calibrationY1, calibrationX2, calibrationY2,
                        videoCalibrationX1, videoCalibrationY1, videoCalibrationX2, videoCalibrationY2,
                        scalingConstant, mValue, video.scale
                    )

                    if (aboutDialog.value) {
                        if (aboutTimes.value <= 5) {
                            AlertDialog(
                                title = { Text("About") },
                                text = {
                                    Text("""
                            Tracker but better! This application is brought to you by AppVenture, the CS Interest Group
                            as well as your SYPT / IYPT alumni.
                            It was created and is maintained by Jed, with the help of Luc, Kabir, Josher and Prannaya.
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
                                modifier = Modifier.size(370.dp, 290.dp).padding(10.dp)
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
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (batchDialog.value) {
                        AlertDialog(
                            title = { Text("Batch Process Underway...") },
                            text = {
                                val remainingTime = (1 - batchProgress.value) / batchProgress.value *
                                        (System.currentTimeMillis() / 1000 - batchStartTime.value)

                                val hours = (remainingTime / 3600).toInt()
                                val mins = ((remainingTime % 3600) / 60).toInt()
                                val secs = (remainingTime % 60).toInt()
                                val timeIndication: String = if(hours == 0) {
                                    if (mins == 0) "$secs s"
                                    else "$mins m $secs s"
                                } else {
                                    if (mins == 0) "$hours h $secs s"
                                    else "$hours h $mins m $secs s"
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Processing file ${batchFile.value.split(Regex("\\|/")).last()}.\n${batchTask.value}...")

                                    LinearProgressIndicator(progress=batchProgress.value, modifier=Modifier.fillMaxWidth())

                                    Text("ETA: $timeIndication")
                                }
                            },
                            confirmButton = {},
                            onDismissRequest = {},
                            modifier = Modifier.size(500.dp, 200.dp).padding(10.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = fpsDialog.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        AlertDialog(
                            onDismissRequest = {
                                fpsDialog.value = false
                            },
                            text = {
                                OutlinedTextField(
                                    label = { Text("Enter FPS") },
                                    value = fps.value,
                                    onValueChange = { fps.value = it }
                                )
                            },
                            confirmButton = { TextButton({
                                try {
                                    video.frameRate = fps.value.toDouble()
                                } catch (ignored: NumberFormatException) { }
                                fpsDialog.value = false
                            }) { Text("Confirm") } },
                            dismissButton = { TextButton({ fpsDialog.value = false }) { Text("Cancel") } },
                        )
                    }
                }
            }
        }
    }
}

val problems = mapOf(
    "1. Invent Yourself" to """
        Take a box (e.g. a matchbox), filled with identical objects (e.g. matches, balls, …). 
        Find a method to determine the number of objects in the box solely by the sound produced while shaking the box. 
        How does the accuracy depend on the properties of the objects, the box, and the packing density?
    """.trimIndent().replace("\n", ""),
    "2. Droplet Microscope" to """
        By looking through a single water droplet placed on a glass surface, one can observe that the droplet acts as an imaging system. 
        Investigate the magnification and resolution of such a lens.
    """.trimIndent().replace("\n", ""),
    "3. Rigid Ramp Walker" to """
        Construct a rigid ramp walker with four legs (e.g. in the form of a ladder). 
        The construction may begin to ‘walk’ down a rough ramp. 
        Investigate how the geometry of the walker and relevant parameters affect its terminal velocity of walking.
    """.trimIndent().replace("\n", ""),
    "4. Shooting Rubber Band" to """
        A rubber band may fly a longer distance if it is non-uniformly stretched when shot, giving it spin. 
        Optimise the distance that a rubber band with spin can reach.
    """.trimIndent().replace("\n", ""),
    "5. Ping Pong Rocket" to """
        A ping pong ball is placed in a container of water. 
        When the container is dropped, the ping pong ball will get launched to a great height. 
        What maximum height can you reach with up to 2 liters of water?
    """.trimIndent().replace("\n", ""),
    "6. Non-contact Resistance" to """
        The responses of a LRC circuit driven by an AC source can be changed by inserting either a non-magnetic metal 
        rod or a ferromagnetic rod into the inductor coil. How can we obtain the magnetic and 
        electric properties of the inserted rod from the circuit’s responses?
    """.trimIndent().replace("\n", ""),
    "7. Giant Sounding Plate" to """
        When a large, thin and flexible plate (e.g. plastic, metal or plexiglass) is bent, 
        it may produce a loud and unusual howling sound. Explain and investigate this phenomenon.
    """.trimIndent().replace("\n", ""),
    "8. Another Magnetic Levitation" to """
        Place a large disk-shaped magnet on a non-magnetic conductive plate. 
        When a smaller magnet is moved under the plate, the magnet on top may levitate under certain conditions. 
        Investigate the levitation and the possible motion of the magnet on top.
    """.trimIndent().replace("\n", ""),
    "9. Juicy Solar Cell" to """
        A functional solar cell can be created using conducting glass slides, iodine, juice (eg. blackberry) and 
        titanium dioxide. This type of cell is called a Grätzel cell. Make such a cell and 
        investigate the necessary parameters to obtain maximum efficiency.
    """.trimIndent().replace("\n", ""),
    "10. Magnetic Gear" to """
        Take several identical fidget spinners and attach neodymium magnets to their ends. 
        If you place them side by side on a plane and rotate one of them, the remaining ones start to rotate only 
        due to the magnetic field. Investigate and explain the phenomenon.
    """.trimIndent().replace("\n", ""),
    "11. Pumping Straw" to """
        A simple water pump can be made using a straw shaped into a triangle and cut open at the vertices. 
        When such a triangle is partially immersed in water with one of its vertices and rotated around its vertical 
        axis, water may flow up through the straw. Investigate how the geometry and other relevant parameters 
        affect the pumping speed.
    """.trimIndent().replace("\n", ""),
    "12. The Soap Spiral" to """
        Lower a compressed slinky into a soap solution, pull it out and straighten it. A soap film is formed between 
        the turns of the slinky. If you break the integrity of the film, the front of the film will begin to move. 
        Explain this phenomenon and investigate the movement of the front of the soap film.
    """.trimIndent().replace("\n", ""),
    "13. Charge Meter" to """
        A lightweight ball is suspended from a thread in the area between two charged plates. If the ball is also 
        charged it will be deflected to one side at a certain angle. What is the accuracy of such a device for 
        measuring the amount of charge on the ball? Optimise your device to measure the smallest possible charge on the ball.
    """.trimIndent().replace("\n", ""),
    "14. Ruler Trick" to """
        Place a ruler on the edge of a table, and throw a ball at its free end. The ruler will fall. 
        However, if you cover a part of the ruler with a piece of paper and repeat the throw, then the ruler will 
        remain on the table while the ball will bounce off it. Explain this phenomenon, and investigate the relevant parameters.
    """.trimIndent().replace("\n", ""),
    "15. Wet Scroll" to """
        Gently place a piece of tracing paper on the surface of water. It rapidly curls into a scroll and then slowly uncurls. 
        Explain and investigate this phenomenon.
    """.trimIndent().replace("\n", ""),
    "16. Cushion Catapult" to """
        Place an object on a large air cushion and drop several other objects in such a way that the first object is catapulted away. 
        Investigate how the exit velocity depends on relevant parameters.
    """.trimIndent().replace("\n", ""),
    "17. Quantum Light Dimmer" to """
        If you put a flame with table salt added in front of a vapour sodium lamp, the flame casts a shadow. 
        The shadow can become lighter, if the flame is put into a strong magnetic field. Investigate and explain the phenomenon.
    """.trimIndent().replace("\n", "")
).toList()
var index = Random.nextInt(problems.size)

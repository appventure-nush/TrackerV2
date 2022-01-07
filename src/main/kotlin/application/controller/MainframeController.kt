package application.controller

import application.Main
import application.Main.Companion.icon
import com.thepyprogrammer.fxtools.draggable.DraggableTab
import com.thepyprogrammer.fxtools.io.File
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.IOException
import java.lang.NullPointerException
import java.net.URL
import java.util.*

class MainframeController: Initializable {
    private val files = HashMap<String, DraggableTab?>()

    @FXML
    lateinit var win: VBox

    @FXML
    lateinit var menubar: MenuBar

    @FXML
    lateinit var file: Menu

    @FXML
    lateinit var help: Menu

    @FXML
    lateinit var demoMenu: MenuItem

    @FXML
    lateinit var openMenu: MenuItem

    @FXML
    lateinit var aboutItem: MenuItem

    @FXML
    lateinit var aboutMeItem: MenuItem

    @FXML
    lateinit var notebook: TabPane

    @FXML
    lateinit var title: Label

    @FXML
    lateinit var minimize: Hyperlink

    @FXML
    lateinit var maximize: Hyperlink

    @FXML
    lateinit var close: Hyperlink

    /**
     * @param event Closes window
     */
    @FXML
    fun closeWin(event: ActionEvent) {
        for (tab in notebook.tabs) {
            tab.onCloseRequest.handle(null)
        }
        val stage = (event.source as Hyperlink).scene.window as Stage
        stage.close()
    }

    /**
     * @param event Minimizes window
     */
    @FXML
    fun minimizeWin(event: ActionEvent) {
        val stage = (event.source as Hyperlink).scene.window as Stage
        stage.isIconified = true
    }

    @FXML
    private fun setFullScreen(event: ActionEvent) {
        Main.fullScreen()
    }

    fun setTitle(title: String?) {
        this.title.text = title
    }

    @FXML
    @Throws(IOException::class)
    fun open(event: ActionEvent?) {
            val file = File.getMP4()
            var tab = getByFile(file.absolutePath)
            if (tab != null) {
                notebook.selectionModel.select(tab)
                return
            }
            val path = file.absolutePath.split("\\\\".toRegex()).toTypedArray()

            val node = FXMLLoader.load<Parent>(Main::class.java.getResource("/tab.fxml"))
            tab = DraggableTab("      " + path[path.size - 1] + "      ")
            tab.isClosable = true
            tab.detachable = true
            tab.label.style = "-fx-background-color: #ffffbf;"
            tab.style = "-fx-background-color: #ffffbf;"
            tab.label.styleClass.add("tablabel")
            tab.content = node

            notebook.tabs.add(tab)
            notebook.selectionModel.select(tab)

            val controller = tabController
            controller?.setFile(file.absolutePath)
            files[file.absolutePath] = tab

            file.close()
    }

    @FXML
    fun demo(even: ActionEvent?) {
        val node = FXMLLoader.load<Parent>(Main::class.java.getResource("/tab.fxml"))
        val tab = DraggableTab("      demo      ")
        tab.isClosable = true
        tab.detachable = true
        tab.label.style = "-fx-background-color: #ffffbf;"
        tab.style = "-fx-background-color: #ffffbf;"
        tab.label.styleClass.add("tablabel")
        tab.content = node

        notebook.tabs.add(tab)
        notebook.selectionModel.select(tab)
    }

    val nameOfTab: String
        get() {
            val tab = notebook.selectionModel.selectedItem as DraggableTab
            val text = tab.label.text.trim { it <= ' ' }
            return text.substring(0, text.length - 5)
        }

    fun generateStage(fxml: String, key: String?) {
            val stage = Stage()
            val root = FXMLLoader.load<Parent>(javaClass.getResource(fxml))
            stage.scene = Scene(root)
            stage.isResizable = false
            stage.title = key
            stage.icons.add(icon)
            stage.show()
    }

    @FXML
    fun findOut(event: MouseEvent?) {
        generateStage("/about/about.fxml", "About")
    }

    @FXML
    fun about(event: ActionEvent?) {
        generateStage("/about/about.fxml", "About")
    }

    @FXML
    fun aboutMe(event: ActionEvent?) {
        generateStage("/about/aboutMe.fxml", "About Me")
    }
    override fun initialize(location: URL, resources: ResourceBundle) {
        notebook.selectionModel.selectedItemProperty()
            .addListener { _: ObservableValue<out Tab?>?, oldValue: Tab?, newValue: Tab? ->
                if (oldValue != null) {
                    val old = oldValue as DraggableTab
                    old.setStyles("-fx-background-color: #ffffbf;")
                }
                if (newValue != null) {
                    val newTab = newValue as DraggableTab
                    newTab.setStyleClasses("focusedTab")
                    newTab.setStyles("-fx-background-color: #add8e6;")
                }
            }

        currentOccurrence = this
        demo(ActionEvent())

    }

    val tabController: TabController?
        get() {
            val node = notebook.selectionModel.selectedItem.content
            return TabController.getController(node)
        }
    val tabControllers: ArrayList<TabController>
        get() {
            val controllers = ArrayList<TabController>()
            for (tab in notebook.tabs) {
                val controller = TabController.getController(tab.content)
                if(controller != null) controllers.add(controller)
            }
            return controllers
        }

    fun getByFile(filename: String): DraggableTab? {
        val selTab: ObjectProperty<DraggableTab?> = SimpleObjectProperty(null)
        files.forEach { (file: String, tab: DraggableTab?) -> if (file == filename) selTab.set(tab) }
        return selTab.get()
    }


    companion object {
        var currentOccurrence: MainframeController? = null
    }


}
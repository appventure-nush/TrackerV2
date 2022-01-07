package com.thepyprogrammer.fxtools.draggable

import com.thepyprogrammer.fxtools.point.Point
import com.thepyprogrammer.fxtools.methods.absoluteRect
import com.thepyprogrammer.fxtools.methods.hide
import com.thepyprogrammer.fxtools.methods.point
import com.thepyprogrammer.fxtools.methods.screenPoint
import com.thepyprogrammer.fxtools.resizable.addResizeListener
import com.thepyprogrammer.fxtools.util.toInt
import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle


/**
 * A draggable tab that can optionally be detached from its tab pane and shown
 * in a separate window. This can be added to any normal TabPane, however a
 * TabPane with draggable tabs must *only* have DraggableTabs, normal tabs and
 * DrragableTabs mixed will cause issues!
 *
 *
 *
 * @author Michael Berry
 * edited by: Prannaya Gupta
 */
class DraggableTab(text: String? = "") : Tab() {
    companion object {
        private val tabPanes: MutableSet<TabPane> = HashSet()
        private var markerStage: Stage = Stage()

        init {
            with(markerStage) {
                initStyle(StageStyle.UNDECORATED)
                markerStage.scene = Scene( StackPane().apply { children.add(Rectangle(3.0, 10.0, Color.web("#555555"))) })
            }
        }
    }

    val label: Label = Label(text)
    private val dragText: Text
    private val dragStage: Stage
    private var detachable: Boolean = true

    private var labelText: String?
        get() = label.text
        set(text) {
            label.text = text
            dragText.text = text
        }

    private fun getInsertData(screenPoint: Point2D): InsertData? {
        for (tabPane in tabPanes) {
            val tabAbsolute = tabPane.absoluteRect
            if (tabAbsolute.contains(screenPoint)) {
                var tabInsertIndex = 0
                if (!tabPane.tabs.isEmpty()) {
                    val firstTabRect = getAbsoluteRect(tabPane.tabs[0])
                    if (firstTabRect.maxY + 60 < screenPoint.y || firstTabRect.minY > screenPoint.y) return null
                    val lastTabRect = getAbsoluteRect(tabPane.tabs[tabPane.tabs.size - 1])
                    when {
                        screenPoint.x < firstTabRect.minX + firstTabRect.width / 2 -> tabInsertIndex = 0
                        screenPoint.x > lastTabRect.maxX - lastTabRect.width / 2 -> tabInsertIndex = tabPane.tabs.size
                        else -> {
                            for (i in 0 until tabPane.tabs.size - 1) {
                                val leftTab = tabPane.tabs[i]
                                val rightTab = tabPane.tabs[i + 1]
                                if (leftTab is DraggableTab && rightTab is DraggableTab) {
                                    val leftTabRect = getAbsoluteRect(leftTab)
                                    val rightTabRect = getAbsoluteRect(rightTab)
                                    if (betweenX(leftTabRect, rightTabRect, screenPoint.x)) {
                                        tabInsertIndex = i + 1
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
                return InsertData(tabInsertIndex, tabPane)
            }
        }
        return null
    }

    private fun getInsertData(screenPoint: Point): InsertData? {
        return getInsertData(screenPoint.toPoint2d())
    }

    private fun getAbsoluteRect(tab: Tab): Rectangle2D {
        val node: Control = (tab as DraggableTab).label
        return node.absoluteRect
    }

    private fun betweenX(r1: Rectangle2D, r2: Rectangle2D, xPoint: Double): Boolean {
        val lowerBound = r1.minX + r1.width / 2
        val upperBound = r2.maxX - r2.width / 2
        return xPoint in lowerBound..upperBound
    }

    fun setStyles(style: String?) {
        setStyle(style)
        label.style = style
    }

    fun setStyleClasses(styleclass: String?) {
        styleClass.add(styleclass)
        label.styleClass.add(styleclass)
    }

    private data class InsertData(val index: Int, val insertPane: TabPane)

    /**
     * Create a new draggable tab. This can be added to any normal TabPane,
     * however a TabPane with draggable tabs must *only* have DraggableTabs,
     * normal tabs and DraggableTabs mixed will cause issues!
     *
     *
     *
     * @param text the text to appear on the tag label.
     */
    init {
        graphic = label
        detachable = true
        dragStage = Stage().apply {
            initStyle(StageStyle.UNDECORATED)
            val dragStagePane = StackPane().apply {
                style = "-fx-background-color:#DDDDDD;"
            }
            dragText = Text(text)

            StackPane.setAlignment(dragText, Pos.CENTER)
            dragStagePane.children.add(dragText)
            scene = Scene(dragStagePane)
            label.setOnMouseDragged {
                width = label.width + 10
                height = label.height + 10
                point = it.screenPoint
                show()
                tabPanes.add(tabPane)
                getInsertData(point).apply {
                    if(this == null || insertPane.tabs.isEmpty()) markerStage.hide()
                    else {
                        val rect = getAbsoluteRect(insertPane.tabs[index - (index == insertPane.tabs.size).toInt()])
                        markerStage.apply {
                            point = Point(
                                if(index == insertPane.tabs.size) rect.maxX + 13 else rect.minX,
                                rect.maxY + 10
                            )
                            show()
                        }
                    }

                }

            }

            label.setOnMouseReleased EventHandler@ {
                listOf(markerStage, this).hide()
                if(!it.isStillSincePress) {
                    val pt = it.screenPoint
                    tabPane.apply oldTabPane@ {
                        val oldIndex = tabs.indexOf(this@DraggableTab)
                        tabPanes.add(this)
                        val insertData = getInsertData(pt).apply insertData@ {
                            if(this != null) {
                                if(this@oldTabPane !== this.insertPane && this@oldTabPane.tabs.size != 1) {
                                    this@oldTabPane.tabs.remove(this@DraggableTab)
                                    var addIndex = index
                                    addIndex = maxOf(addIndex - (oldIndex < addIndex && this@oldTabPane === this@insertData.insertPane).toInt(), insertPane.tabs.size)
                                    insertPane.tabs.add(addIndex, this@DraggableTab)
                                    insertPane.selectionModelProperty().get().select(addIndex)
                                }
                                return@EventHandler
                            }
                            if(!detachable) return@EventHandler

                            val newStage = Stage().apply newStage@ {
                                val pane = TabPane()
                                tabPanes.add(pane)
                                setOnHiding { tabPanes.remove(pane) }
                                tabPane.tabs.remove(this@DraggableTab)
                                pane.tabs.add(this@DraggableTab)
                                pane.tabs.addListener(ListChangeListener {
                                    if (pane.tabs.isEmpty()) this@newStage.hide()
                                })
                                scene = Scene(pane)
                                minHeight = (content as VBox).minHeight
                                minWidth = (content as VBox).minWidth
                                addResizeListener(this)
                                point = pt
                                title = label.text
                                show()
                                pane.requestLayout()
                                pane.requestFocus()
                            }
                        }
                    }
                }
            }
        }
    }
}

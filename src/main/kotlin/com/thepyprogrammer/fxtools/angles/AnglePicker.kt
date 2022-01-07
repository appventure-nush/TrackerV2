package com.thepyprogrammer.fxtools.angles

import javafx.beans.DefaultProperty
import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.DoublePropertyBase
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.effect.BlurType
import javafx.scene.effect.InnerShadow
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.*
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.transform.Rotate
import javafx.util.StringConverter
import java.util.*
import kotlin.math.atan2
import kotlin.math.sqrt

/* Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




/**
 * User: hansolo
 * Date: 07.11.18
 * Time: 12:30
 */
@DefaultProperty("children")
open class AnglePicker : Region() {
    private var size = 0.0
    private var widthProp = 0.0
    private var heightProp = 0.0
    private var background: Circle? = null
    private var foreground: Circle? = null
    private var indicator: Rectangle? = null
    private var text: Text? = null
    private var pane: Pane? = null
    private val rotate: Rotate
    private var _angle: Double
    private var angle: DoubleProperty? = null
    private var _backgroundPaint: Paint?
    private var backgroundPaint: ObjectProperty<Paint?>? = null
    private var _foregroundPaint: Paint?
    private var foregroundPaint: ObjectProperty<Paint?>? = null
    private var indicatorPaint: ObjectProperty<Paint?>? = null
    private var _indicatorPaint: Paint?
    private var _textPaint: Paint?
    private var textPaint: ObjectProperty<Paint?>? = null
    private var innerShadow: InnerShadow? = null
    private var textField: TextField? = null
    private val converter: StringConverter<Double?>
    private val mouseFilter: EventHandler<MouseEvent>

    // ******************** Initialization ************************************
    private fun initGraphics() {
        if (prefWidth.compareTo(0.0) <= 0 || prefHeight.compareTo(0.0) <= 0 || getWidth().compareTo(0.0) <= 0 || getHeight().compareTo(0.0) <= 0) {
            if (prefWidth > 0 && prefHeight > 0) {
                setPrefSize(prefWidth, prefHeight)
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
            }
        }
        styleClass.add("angle-picker")
        rotate.angle = 0.0
        innerShadow = InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(255, 255, 255, 0.3), 1.0, 0.0, 0.0, 0.5)
        background = Circle()
        background!!.fill = _backgroundPaint
        background!!.isMouseTransparent = true
        foreground = Circle()
        foreground!!.fill = _foregroundPaint
        foreground!!.effect = innerShadow
        indicator = Rectangle()
        indicator!!.transforms.add(rotate)
        indicator!!.isMouseTransparent = true
        text = Text(String.format(Locale.US, "%.0f\u00b0", _angle))
        text!!.textOrigin = VPos.CENTER
        text!!.isMouseTransparent = true
        textField = TextField(String.format(Locale.US, "%.0f\u00b0", _angle))
        //textField.setRegex("\\d{0,3}([\\.]\\d{0,1})?");
        textField!!.textFormatterProperty().value = TextFormatter(converter, getAngle())
        textField!!.padding = Insets(2.0)
        textField!!.alignment = Pos.CENTER
        textField!!.isVisible = false
        textField!!.isManaged = false
        pane = Pane(background, foreground, indicator, text, textField)
        children.setAll(pane)
    }

    private fun registerListeners() {
        widthProperty().addListener( InvalidationListener{ resize() })
        heightProperty().addListener( InvalidationListener{ resize() })
        foreground!!.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseFilter)
        foreground!!.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseFilter)
        textField!!.onKeyPressed = EventHandler { e: KeyEvent ->
            if (e.code == KeyCode.ENTER) {
                updateTextField()
            }
        }
        textField!!.focusedProperty().addListener { o: ObservableValue<out Boolean?>?, _: Boolean?, nv: Boolean? ->
            if (!nv!!) {
                updateTextField()
            }
        }
        focusedProperty().addListener { o: ObservableValue<out Boolean?>?, _: Boolean?, nv: Boolean? ->
            if (!nv!!) {
                updateTextField()
            }
        }
    }

    // ******************** Methods *******************************************
    public override fun layoutChildren() {
        super.layoutChildren()
    }

    override fun computeMinWidth(HEIGHT: Double): Double {
        return MINIMUM_WIDTH
    }

    override fun computeMinHeight(WIDTH: Double): Double {
        return MINIMUM_HEIGHT
    }

    override fun computeMaxWidth(HEIGHT: Double): Double {
        return MAXIMUM_WIDTH
    }

    override fun computeMaxHeight(WIDTH: Double): Double {
        return MAXIMUM_HEIGHT
    }

    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    fun getAngle(): Double {
        return if (null == angle) _angle else angle!!.get()
    }

    fun setAngle(angle: Double) {
        if (null == this.angle) {
            _angle = angle % 360.0
            rotate.angle = _angle
            text!!.text = String.format(Locale.US, "%.0f\u00b0", 360 - _angle)
            text!!.relocate((size - text!!.layoutBounds.width) * 0.5, (size - text!!.layoutBounds.height) * 0.5)
            textField!!.text = String.format(Locale.US, "%.0f\u00b0", 360 - _angle)
        } else {
            this.angle!!.set(angle)
        }
    }

    fun angleProperty(): DoubleProperty {
        if (null == angle) {
            angle = object : DoublePropertyBase(_angle) {
                override fun invalidated() {
                    rotate.angle = get() % 360.0
                    text!!.text = String.format(Locale.US, "%.0f\u00b0", 360 - get())
                    text!!.relocate((size - text!!.layoutBounds.width) * 0.5, (size - text!!.layoutBounds.height) * 0.5)
                    textField!!.text = String.format(Locale.US, "%.0f\u00b0", 360 - get())
                }

                override fun getBean(): Any {
                    return this@AnglePicker
                }

                override fun getName(): String {
                    return "angle"
                }
            }
        }
        return angle!!
    }

    fun getBackgroundPaint(): Paint? {
        return if (null == backgroundPaint) _backgroundPaint else backgroundPaint!!.get()
    }

    fun setBackgroundPaint(backgroundPaint: Paint) {
        if (null == this.backgroundPaint) {
            _backgroundPaint = backgroundPaint
            redraw()
        } else {
            this.backgroundPaint!!.set(backgroundPaint)
        }
    }

    fun backgroundPaintProperty(): ObjectProperty<Paint?>? {
        if (null == backgroundPaint) {
            backgroundPaint = object : ObjectPropertyBase<Paint?>(_backgroundPaint) {
                override fun invalidated() {
                    redraw()
                }

                override fun getBean(): Any {
                    return this@AnglePicker
                }

                override fun getName(): String {
                    return "backgroundPaint"
                }
            }
            _backgroundPaint = null
        }
        return backgroundPaint
    }

    fun getForegroundPaint(): Paint? {
        return if (null == foregroundPaint) _foregroundPaint else foregroundPaint!!.get()
    }

    fun setForegroundPaint(foregroundPaint: Paint) {
        if (null == this.foregroundPaint) {
            _foregroundPaint = foregroundPaint
            redraw()
        } else {
            this.foregroundPaint!!.set(foregroundPaint)
        }
    }

    fun foregroundPaintProperty(): ObjectProperty<Paint?>? {
        if (null == foregroundPaint) {
            foregroundPaint = object : ObjectPropertyBase<Paint?>(_foregroundPaint) {
                override fun invalidated() {
                    redraw()
                }

                override fun getBean(): Any {
                    return this@AnglePicker
                }

                override fun getName(): String {
                    return "foregroundPaint"
                }
            }
            _foregroundPaint = null
        }
        return foregroundPaint
    }

    fun getIndicatorPaint(): Paint? {
        return if (null == indicatorPaint) _indicatorPaint else indicatorPaint!!.get()
    }

    fun setIndicatorPaint(indicatorPaint: Paint) {
        if (null == this.indicatorPaint) {
            _indicatorPaint = indicatorPaint
            redraw()
        } else {
            this.indicatorPaint!!.set(indicatorPaint)
        }
    }

    fun indicatorPaintProperty(): ObjectProperty<Paint?>? {
        if (null == indicatorPaint) {
            indicatorPaint = object : ObjectPropertyBase<Paint?>(_indicatorPaint) {
                override fun invalidated() {
                    redraw()
                }

                override fun getBean(): Any {
                    return this@AnglePicker
                }

                override fun getName(): String {
                    return "indicatorPaint"
                }
            }
            _indicatorPaint = null
        }
        return indicatorPaint
    }

    fun getTextPaint(): Paint? {
        return if (null == textPaint) _textPaint else textPaint!!.get()
    }

    fun setTextPaint(textPaint: Paint) {
        if (null == this.textPaint) {
            _textPaint = textPaint
            redraw()
        } else {
            this.textPaint!!.set(textPaint)
        }
    }

    fun textPaintProperty(): ObjectProperty<Paint?>? {
        if (null == textPaint) {
            textPaint = object : ObjectPropertyBase<Paint?>(_textPaint) {
                override fun invalidated() {
                    redraw()
                }

                override fun getBean(): Any {
                    return this@AnglePicker
                }

                override fun getName(): String {
                    return "textPaint"
                }
            }
            _textPaint = null
        }
        return textPaint
    }

    private fun updateTextField() {
        val text = textField!!.text.replace("\\n", "").replace("\u00b0", "")
        if (!text.matches(Regex("\\d{0,3}([.]\\d?)?"))) {
            return
        }
        if (text.isNotEmpty()) {
            setAngle(textField!!.text.toDouble())
        }
        textField!!.isVisible = false
        textField!!.isManaged = false
    }

    private fun getAngleFromXY(
        x: Double,
        y: Double,
        centerX: Double,
        centerY: Double,
        angleOffset: Double
    ): Double {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        val deltaX = x - centerX
        val deltaY = y - centerY
        val radius = sqrt(deltaX * deltaX + deltaY * deltaY)
        val nx = deltaX / radius
        val ny = deltaY / radius
        var theta = atan2(ny, nx)
        theta = if (theta.compareTo(0.0) >= 0) Math.toDegrees(theta) else Math.toDegrees(theta) + 360.0
        return (theta + angleOffset) % 360
    }

    // ******************** Resizing ******************************************
    private fun resize() {
        widthProp = getWidth() - insets.left - insets.right
        heightProp = getHeight() - insets.top - insets.bottom
        size = if (widthProp < heightProp) widthProp else heightProp
        if (widthProp > 0 && heightProp > 0) {
            pane!!.setMaxSize(size, size)
            pane!!.setPrefSize(size, size)
            pane!!.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5)
            rotate.pivotX = indicator!!.x - size * 0.27777778
            rotate.pivotY = indicator!!.height * 0.5
            innerShadow!!.radius = size * 0.0212766
            innerShadow!!.offsetY = size * 0.0106383
            background!!.radius = size * 0.5
            background!!.relocate(0.0, 0.0)
            foreground!!.radius = size * 0.4787234
            foreground!!.relocate(size * 0.0212766, size * 0.0212766)
            indicator!!.width = size * 0.20
            indicator!!.height = size * 0.01587302
            indicator!!.relocate(size * 0.77777778, (size - indicator!!.height) * 0.5)
            text!!.font = Font.font(size * 0.19148936)
            text!!.relocate((size - text!!.layoutBounds.width) * 0.5, (size - text!!.layoutBounds.height) * 0.5)
            textField!!.prefWidth = size * 0.6
            textField!!.prefHeight = size * 0.22
            textField!!.font = Font.font(size * 0.19148936)
            textField!!.relocate((size - textField!!.prefWidth) * 0.5, (size - textField!!.prefHeight) * 0.5)
            redraw()
        }
    }

    private fun redraw() {
        background!!.fill = getBackgroundPaint()
        foreground!!.fill = getForegroundPaint()
        indicator!!.fill = getIndicatorPaint()
        text!!.fill = getTextPaint()
    }

    companion object {
        private const val PREFERRED_WIDTH = 63.0
        private const val PREFERRED_HEIGHT = 63.0
        private const val MINIMUM_WIDTH = 20.0
        private const val MINIMUM_HEIGHT = 20.0
        private const val MAXIMUM_WIDTH = 1024.0
        private const val MAXIMUM_HEIGHT = 1024.0
    }

    // ******************** Constructors **************************************
    init {

        stylesheets.add(Thread.currentThread().contextClassLoader.getResource("style/angle-picker.css")?.toExternalForm())
        rotate = Rotate()
        _angle = 0.0
        _backgroundPaint = Color.rgb(32, 32, 32)
        _foregroundPaint = LinearGradient(
            0.0, .0, .0, 1.0, true, CycleMethod.NO_CYCLE,
            Stop(0.0, Color.rgb(61, 61, 61)),
            Stop(0.5, Color.rgb(50, 50, 50)),
            Stop(1.0, Color.rgb(42, 42, 42))
        )
        _indicatorPaint = Color.rgb(159, 159, 159)
        _textPaint = Color.rgb(230, 230, 230)
        converter = object : StringConverter<Double?>() {
            override fun toString(number: Double?): String {
                return String.format(Locale.US, "%.1f", 360 - number!!)
            }

            override fun fromString(string: String?): Double? {
                if (string.isNullOrBlank()) return null
                val numberString = string.replace("\\n", "").replace("\u00b0", "")
                return if (numberString.matches(Regex("\\d{0,3}([.]\\d?)?"))) {
                    360 - java.lang.Double.valueOf(numberString)
                } else {
                    0.0
                }
            }
        }
        mouseFilter = EventHandler { evt: MouseEvent ->
            val type = evt.eventType
            if (type == MouseEvent.MOUSE_DRAGGED) {
                val angle = getAngleFromXY(evt.x + size * 0.5, evt.y + size * 0.5, size * 0.5, size * 0.5, 0.0)
                setAngle(angle)
            } else if (type == MouseEvent.MOUSE_CLICKED) {
                val clicks = evt.clickCount
                if (clicks == 2) {
                    textField!!.isManaged = true
                    textField!!.isVisible = true
                    textField!!.isFocusTraversable = true
                }
            }
        }
        initGraphics()
        registerListeners()
    }
}
package com.thepyprogrammer.fxtools.angles

import javafx.scene.layout.Pane
import javafx.scene.paint.*



class AngleChooser : AnglePicker() {
    private var node: Pane? = null
    fun bind(node: Pane) {
        this.node?.rotateProperty()?.unbindBidirectional(angleProperty())
        this.node = node.apply {
            setAngle(rotate)
            if(rotate == 0.0) setAngle(360.0)
            rotateProperty().bindBidirectional(angleProperty())
        }
    }

    init {
        setForegroundPaint(Color.WHITE as Paint)
        setBackgroundPaint(
            LinearGradient(
                .0, .0, .0, 1.0, true, CycleMethod.NO_CYCLE,
                Stop(0.0, Color.rgb(214, 214, 214)),
                Stop(0.5, Color.rgb(206, 206, 206)),
                Stop(1.0, Color.rgb(195, 195, 195))
            )
        )
        setIndicatorPaint(Color.rgb(97, 97, 97))
        setTextPaint(Color.rgb(26, 26, 26))
    }
}
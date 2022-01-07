package com.thepyprogrammer.fxtools.point

import javafx.geometry.Rectangle2D

fun Rectangle2D.contains(screenPoint: Point) = contains(screenPoint.toPoint2d())


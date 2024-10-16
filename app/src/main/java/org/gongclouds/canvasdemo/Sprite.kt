package org.gongclouds.canvasdemo

import androidx.compose.ui.graphics.ImageBitmap

open class Sprite() {
    var x = 0
    var y = 0
    var width = 0
    var height = 0
    var time = 0
    var die = false
    lateinit var imageBitmap: ImageBitmap
    lateinit var settingFunction: () -> Unit
    lateinit var loopingFunction: () -> Unit
    lateinit var data:Object

    fun set(fn: () -> Unit) {
        settingFunction = fn
        settingFunction()
    }

    fun loop(fn: () -> Unit) {
        loopingFunction = fn
    }

    fun reset() {
        die = false
        settingFunction()
    }

    fun run() {
        if (!die) {
            loopingFunction()
        }
    }
}
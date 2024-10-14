package org.gongclouds.canvasdemo

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.math.absoluteValue

data class Location(val x: Int, val y: Int)
data class Size(val w: Int, val h: Int)

class Sprite(val name: String) {
    private var status: SpriteStatus = SpriteStatus.Living
    private lateinit var images: ArrayList<ImageBitmap>
    private var imageIndex = 0
    private var time = 0

    private var left = 0
    private var top = 0
    private var width = 0
    private var height = 0

    private lateinit var onSetupEvent: () -> Unit
    private lateinit var onUpdateEvent: () -> Unit

    companion object Factory {
        fun create(name: String): Sprite = Sprite(name)
    }

    fun setImages(imageBitmapArrayList: ArrayList<ImageBitmap>) {
        images = imageBitmapArrayList
        imageIndex = 0
    }

    fun getImage(): ImageBitmap {
        return images[imageIndex]
    }

    private fun updateRender() {
        time++
        if (time % 5 == 0) {
            if (imageIndex < images.size - 1) {
                imageIndex += 1
            } else {
                imageIndex = 0
            }
        }
    }

    fun setup(event: () -> Unit) {
        onSetupEvent = event
        reset()
    }

    fun loop(event: () -> Unit) {
        onUpdateEvent = event
    }

    fun getTime(): Int {
        return time
    }

    fun reset() {
        time = 0
        status = SpriteStatus.Living
        onSetupEvent()
    }

    fun run() {
        updateRender()
        onUpdateEvent()
    }

    fun getCenter(): Location {
        return Location(width + width / 2, height + height / 2)
    }

    fun getLocation(): Location {
        return Location(x = left, y = top)
    }

    fun setLocation(x: Int, y: Int) {
        left = x
        top = y
    }

    fun setLocationX(x: Int) {
        left = x
    }

    fun setLocationY(y: Int) {
        top = y
    }

    fun getSize(): Size {
        return Size(w = width, h = height)
    }

    fun setSize(w: Int, h: Int) {
        width = w
        height = h
    }

    fun setSizeX(w: Int) {
        width = w
    }

    fun setSizeY(h: Int) {
        height = h
    }


    fun setStatus(spriteStatus: SpriteStatus) {
        status = spriteStatus
    }

    fun getStatus(): SpriteStatus {
        return status
    }

    fun intersects(target: Sprite): Boolean {
        val (selfX, selfY) = getLocation()
        val (targetX, targetY) = target.getLocation()
        return (selfX - targetX).absoluteValue < width / 2 && (selfY - targetY).absoluteValue < height / 2
    }
}
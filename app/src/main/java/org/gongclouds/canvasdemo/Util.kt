package org.gongclouds.canvasdemo

import kotlin.math.sqrt

fun isCollision(sprite1: Sprite, sprite2: Sprite): Boolean {
    val dx = sprite1.x - sprite2.x
    val dy = sprite1.y - sprite2.y
    val distance = sqrt((dx * dx + dy * dy).toDouble())
    return distance <= sprite1.width / 3 + sprite2.width / 3
}
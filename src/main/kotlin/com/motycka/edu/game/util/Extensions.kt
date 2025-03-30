package com.motycka.edu.game.util

fun Int.inRange(min: Int, max: Int): Boolean {
    return this in min..max
}

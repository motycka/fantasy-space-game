package com.motycka.edu.game.util

import kotlin.random.Random

object RandomUtil {
    fun randomInt(min: Int, max: Int): Int {
        return Random.nextInt(min, max + 1)
    }
}

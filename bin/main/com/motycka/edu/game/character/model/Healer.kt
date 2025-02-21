package com.motycka.edu.game.character.model

interface Healer {
    val mana: Int
    val healingPower: Int
    fun heal()
}

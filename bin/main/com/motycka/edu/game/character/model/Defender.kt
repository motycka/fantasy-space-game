package com.motycka.edu.game.character.model

interface Defender {
    val name: String
    val stamina: Int
    val defensePower: Int
    fun defend(attackPower: Int): Int
}

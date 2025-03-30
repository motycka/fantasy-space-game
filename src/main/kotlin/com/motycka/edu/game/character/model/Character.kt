package com.motycka.edu.game.character.model

data class Character(
    val id: Long,
    val accountId: Long,
    val name: String,
    val characterClass: String,
    val health: Int,
    val attack: Int,
    val defense: Int,
    val stamina: Int,
    val healing: Int,
    val mana: Int,
    var experience: Int
)

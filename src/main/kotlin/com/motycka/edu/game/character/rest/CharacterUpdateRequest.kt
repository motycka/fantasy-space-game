package com.motycka.edu.game.character.rest

data class CharacterUpdateRequest(
    val attackPower: Int,
    val characterClass: String,
    val healingPower: Int,
    val health: Int,
    val mana: Int,
    val name: String
)

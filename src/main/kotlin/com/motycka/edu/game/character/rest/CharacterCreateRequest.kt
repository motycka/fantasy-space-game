package com.motycka.edu.game.character.rest

data class CharacterCreateRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: String,
)
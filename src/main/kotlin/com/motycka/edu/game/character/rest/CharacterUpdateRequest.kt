package com.motycka.edu.game.character.rest

data class CharacterUpdateRequest(
    val health: Int,
    val attack: Int,
    val stamina: Int?,
    val defense: Int?,
    val mana: Int?,
    val healing: Int?
)

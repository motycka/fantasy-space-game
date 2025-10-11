package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.CharacterLevel

typealias CharacterId = Long

data class CharacterResponse(
    val id: CharacterId,
    val name: String,
    val health: Int,
    val attack: Int,
    val stamina: Int?,
    val defense: Int?,
    val mana: Int?,
    val healing: Int?,
    val characterClass: CharacterClass,
    val level: CharacterLevel,
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)

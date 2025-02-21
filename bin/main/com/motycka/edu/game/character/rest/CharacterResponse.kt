package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.CharacterLevel

typealias CharacterId = Long

data class CharacterResponse(
    val id: CharacterId,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: CharacterClass,
    val level: CharacterLevel,
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)

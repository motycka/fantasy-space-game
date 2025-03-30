package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.Character

data class CharacterResponse(
    val id: Long,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: String,
    val level: Int,
    val experience: Int
)

fun Character.toCharacterResponse(): CharacterResponse {
    return CharacterResponse(
        id = this.id,
        name = this.name,
        health = this.health,
        attackPower = this.attackPower,
        stamina = this.stamina,
        defensePower = this.defensePower,
        mana = this.mana,
        healingPower = this.healingPower,
        characterClass = this.characterClass.name,
        level = this.level,
        experience = this.experience
    )
}

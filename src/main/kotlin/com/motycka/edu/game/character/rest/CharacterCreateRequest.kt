package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterClass

data class CharacterCreateRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: CharacterClass
) {
    fun toCharacter(): Character {
        return Character(
            name = this.name,
            health = this.health,
            attackPower = this.attackPower,
            stamina = this.stamina,
            defensePower = this.defensePower,
            mana = this.mana,
            healingPower = this.healingPower,
            characterClass = this.characterClass,
            accountId = 0
        )
    }
}

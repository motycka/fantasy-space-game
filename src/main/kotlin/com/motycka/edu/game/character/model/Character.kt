package com.motycka.edu.game.character.model


data class Character (
    val id: CharacterId,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: String,
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)

fun Character.toGameCharacter(): GameCharacter {
    return when (this.characterClass.uppercase()) {
        "WARRIOR" -> Warrior(
            id = this.id,
            name = this.name,
            stamina = this.stamina ?: 0,
            defensePower = this.defensePower ?: 0,
            attackPower = this.attackPower,
            health = this.health,
            exp = this.experience,
        )
        "SORCERER" -> Sorcerer(
            id = this.id,
            name = this.name,
            mana = this.mana ?: 0,
            healingPower = this.healingPower ?: 0,
            attackPower = this.attackPower,
            health = this.health,
            exp = this.experience,
        )
        else -> throw IllegalArgumentException("Unknown character class: ${this.characterClass}")
    }
}
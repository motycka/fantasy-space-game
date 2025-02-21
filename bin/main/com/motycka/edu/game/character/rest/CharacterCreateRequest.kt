package com.motycka.edu.game.character.rest


data class CharacterCreateRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: CharacterClass
) {
    init {
        when (characterClass) {
            CharacterClass.WARRIOR -> {
                require(stamina != null) { "Stamina is required for a warrior" }
                require(defensePower != null) { "Defense power is required for a warrior" }
            }
            CharacterClass.SORCERER -> {
                require(mana != null) { "Mana is required for a sorcerer" }
                require(healingPower != null) { "Healing power is required for a sorcerer" }
            }
        }
    }
}

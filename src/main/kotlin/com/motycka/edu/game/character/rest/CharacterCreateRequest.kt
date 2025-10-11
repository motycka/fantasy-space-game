package com.motycka.edu.game.character.rest


data class CharacterCreateRequest(
    val name: String,
    val health: Int,
    val attack: Int,
    val stamina: Int? = null,
    val defense: Int? = null,
    val mana: Int? = null,
    val healing: Int? = null,
    val characterClass: CharacterClass
) {
    init {
        when (characterClass) {
            CharacterClass.WARRIOR -> {
                require(stamina != null) { "Stamina is required for a warrior" }
                require(defense != null) { "Defense power is required for a warrior" }
            }
            CharacterClass.SORCERER -> {
                require(mana != null) { "Mana is required for a sorcerer" }
                require(healing != null) { "Healing power is required for a sorcerer" }
            }
        }
    }
}

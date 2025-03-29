package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.CharacterStats

data class CharacterCreationRequest(
    val name: String,
    val characterClass: CharacterClass,
    override val health: Int,
    override val attackPower: Int,
    override val defensePower: Int = 0, // Warrior
    override val stamina: Int = 0,
    override val healingPower: Int = 0, // Sorcerer
    override val mana: Int = 0,
): CharacterStats {
    private val totalPoints = health + attackPower + (defensePower ?: 0) + (stamina ?: 0) + (healingPower ?: 0) + (mana ?: 0)
    private val regularStats = listOfNotNull(health, attackPower)
    private val sorcererStats = regularStats + listOfNotNull(healingPower, mana)
    private val warriorStats = regularStats + listOfNotNull(defensePower, stamina)

    init {
        require(totalPoints == CharacterLevel.LEVEL_1.totalPoints) { "Total points must be equal to ${CharacterLevel.LEVEL_1.totalPoints}" }
        // Check that the stats are correctly distributed
        require(characterClass != CharacterClass.SORCERER || sorcererStats.sum() == totalPoints) { "Sorcerer stats must sum to total points" }
        require(characterClass != CharacterClass.WARRIOR || warriorStats.sum() == totalPoints) { "Warrior stats must sum to total points" }
    }
}
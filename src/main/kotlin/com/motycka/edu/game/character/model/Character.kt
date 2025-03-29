package com.motycka.edu.game.character.model

import com.motycka.edu.game.account.model.AccountId

/**
 * This is a class that represents a data entity for an account.
 * It is used internally within the application and is not supposed to be exposed to the outside world.
 */
data class Character(
    val id: CharacterId? = null,
    val accountId: AccountId?,
    val name: String,
    val characterClass: CharacterClass,
    override val experience: Int,
    override var health: Int,
    override val attackPower: Int,
    override val defensePower: Int = 0, // Warrior
    override val stamina: Int = 0,
    override val healingPower: Int = 0, // Sorcerer
    override val mana: Int = 0,
) : CharacterStats, Levelable {
    val totalStats = health + attackPower + defensePower + stamina + healingPower + mana
    private val sorcererStats = listOfNotNull(health, attackPower, healingPower, mana)
    private val warriorStats = listOfNotNull(health, attackPower, defensePower, stamina)

    override val level = CharacterLevel.fromTotalPoints(totalStats)
    override val shouldLevelUp = CharacterLevel.fromExp(experience).level > level.level


    init {
        require(totalStats <= level.totalPoints) { "Total points must not exceed ${level.totalPoints}" }
        // Check that the stats are correctly distributed
        require(characterClass != CharacterClass.SORCERER || sorcererStats.sum() == totalStats) { "Sorcerer with non sorcerer stats" }
        require(characterClass != CharacterClass.WARRIOR || warriorStats.sum() == totalStats) { "Warrior with non warrior stats" }
    }
}

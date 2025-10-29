package com.motycka.edu.game.character.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.rest.CharacterClass
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

abstract class Character(
    val id: CharacterId?,
    val accountId: AccountId?,
    val name: String,
    val health: Int,
    val attack: Int,
    val energy: Int,
    val ability: Int,
    val level: CharacterLevel,
    val experience: Int,
    val characterClass: CharacterClass,
): Recoverable {

    init {
        val pointsAssigned = health + attack + energy + ability
        require(pointsAssigned <= level.points) { "Character $name attributes can not exceed ${level.points} level points (assigned $pointsAssigned)" }
    }

    var currentHealth: Int = health
        protected set

    // this is null-checked id
    val characterId: CharacterId get() = requireNotNull(id) { "characterId must not be null" }

    val allPoints = health + attack + energy + ability

    fun isAlive() = currentHealth > 0

    abstract fun attack(target: Character)

    open fun receiveAttack(attackPower: Int) {
        when {
            currentHealth - attackPower > 0 -> {
                currentHealth -= attackPower
                logger.info { "$name has $currentHealth health remaining." }
            }

            else -> {
                currentHealth = 0
                logger.info { "$name has been defeated" }
            }
        }
    }
}


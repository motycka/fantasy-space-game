package com.motycka.edu.game.character.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

abstract class Character(
    val id: CharacterId?, // TODO new
    val accountId: AccountId?, // TODO new
    val name: String,
    val health: Int,
    val attackPower: Int,
    val experience: Int // TODO new
): Recoverable {

    protected var currentHealth: Int = health // new

    // this is null-checked id
    val characterId: CharacterId get() = requireNotNull(id) { "characterId must not be null" }

    abstract val level: CharacterLevel

    abstract fun attack(target: Character)

    // TODO new
    abstract fun getStats(): CharacterStats

    // TODO new
    abstract fun getPoints(): Int

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


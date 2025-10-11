package com.motycka.edu.game.character.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.rest.CharacterClass
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class Warrior(
    id: CharacterId?,
    accountId: AccountId?,
    name: String,
    health: Int,
    attack: Int,
    level: CharacterLevel,
    experience: Int,
    val stamina: Int,
    val defense: Int,
) : Character(
    id = id,
    accountId = accountId,
    name = name,
    health = health,
    attack = attack,
    energy = stamina,
    ability = defense,
    level = level,
    experience = experience,
    characterClass = CharacterClass.WARRIOR
), Defender {

    private var currentStamina = stamina

    override fun attack(target: Character) {
        when {
            currentHealth <= 0 -> logger.info { "$name is dead and cannot attack" }
            currentStamina <= 0 -> logger.info { "$name is too tired to attack" }
            else -> {
                logger.info { "$name swings a sword at ${target.name}" }
                target.receiveAttack(attack)
                currentStamina--
            }
        }
    }

    override fun receiveAttack(attackPower: Int) {
        super.receiveAttack(defend(health - attackPower))
    }

    override fun defend(attack: Int): Int {
        return if (currentStamina > 0) {
            logger.info { "$name raises shield and defends against $defense damage" }
            attack - defense
        } else {
            logger.info { "$name is too tired to defend" }
            attack
        }
    }

    override fun beforeRound() {
        if (currentStamina < stamina) {
            val regenerates = (level.ordinal + 1)
            logger.info { "$name regenerates $regenerates stamina" }
            currentStamina += regenerates
        }
    }

    override fun afterRound() {
        // no-op
    }
}

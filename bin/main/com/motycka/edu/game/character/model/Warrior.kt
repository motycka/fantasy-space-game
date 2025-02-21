package com.motycka.edu.game.character.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class Warrior(
    id: CharacterId?, // TODO new
    accountId: AccountId?, // TODO new
    name: String,
    health: Int,
    attackPower: Int,
    experience: Int, // TODO new
    override val stamina: Int,
    override val defensePower: Int,
    override val level: CharacterLevel, // new
) : Character(
    id = id, // TODO new
    accountId = accountId, // TODO new
    name = name,
    health = health,
    attackPower = attackPower,
    experience = experience // TODO new
), Defender {

    private var currentStamina = stamina

    // new
    init {
//        val pointsAssigned = health + attackPower + stamina + defensePower
//        require(pointsAssigned <= level.points) { "Attributes can not exceed ${level.points} level points (assigned $pointsAssigned)" }
//        require(pointsAssigned == level.points) { "All ${level.points} level points must be assigned (assigned $pointsAssigned)." }
    }

    override fun attack(target: Character) {
        when {
            health <= 0 -> logger.info { "$name is dead and cannot attack" }
            stamina <= 0 -> logger.info { "$name is too tired to attack" }
            else -> {
                logger.info { "$name swings a sword at ${target.name}" }
                target.receiveAttack(attackPower)
                currentStamina--
            }
        }
    }

    override fun getStats(): CharacterStats {
        return CharacterStats(
            health = currentHealth,
            stamina = currentStamina,
            mana = 0
        )
    }

    override fun getPoints() = health + attackPower + stamina + defensePower

    override fun receiveAttack(attackPower: Int) {
        super.receiveAttack(defend(health - attackPower))
    }

    override fun defend(attackPower: Int): Int {
        return if (stamina > 0) {
            logger.info { "$name raises shield and defends against $defensePower damage" }
            attackPower - defensePower
        } else {
            logger.info { "$name is too tired to defend" }
            attackPower
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

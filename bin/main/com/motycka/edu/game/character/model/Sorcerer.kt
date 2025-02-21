package com.motycka.edu.game.character.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class Sorcerer(
    id: CharacterId?, // TODO new
    accountId: AccountId?, // TODO new
    name: String,
    health: Int,
    attackPower: Int,
    experience: Int, // TODO new
    override val mana: Int,
    override val healingPower: Int,
    override val level: CharacterLevel, // new
) : Character(
    id = id, // TODO new
    accountId = accountId, // TODO new
    name = name,
    health = health,
    attackPower = attackPower,
    experience = experience // TODO new
), Healer {

    private var currentMana: Int = mana

    // new
//    init {
//        val pointsAssigned = health + attackPower + mana + healingPower
//        require(pointsAssigned <= level.points) { "Character $name attributes can not exceed ${level.points} level points (assigned $pointsAssigned)" }
//        require(pointsAssigned == level.points) { "All ${level.points} level points must be assigned to $name (assigned $pointsAssigned)." }
//    }

    override fun attack(target: Character) {
        heal()
        when {
            health <= 0 -> logger.info { "$name is dead and cannot attack" }
            mana <= 0 -> logger.info { "$name out of mana" }
            else -> {
                logger.info { "$name casts a spell at ${target.name}" }
                target.receiveAttack(attackPower)
                currentMana--
            }
        }
    }

    override fun getStats(): CharacterStats {
        return CharacterStats(
            health = currentHealth,
            stamina = 0,
            mana = currentMana
        )
    }

    override fun getPoints() = health + attackPower + mana + healingPower

    override fun heal() {
        when {
            health <= 0 -> logger.info { "$name is dead and cannot heal"}
            mana <= 0 -> logger.info { "$name is out of mana" }
            else -> {
                if (health + healingPower > health) {
                    currentHealth = health
                } else {
                    currentHealth += healingPower
                }
                println("$name heals self to $health health")
            }
        }
    }

    override fun beforeRound() {
        if (currentMana < mana) {
            val regenerates = (level.ordinal + 1)
            logger.info { "$name regenerates $regenerates mana" }
            currentMana += regenerates
        }
    }

    override fun afterRound() {
        // no-op
    }

}

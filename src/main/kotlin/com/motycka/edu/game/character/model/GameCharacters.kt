package com.motycka.edu.game.character.model

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

abstract class GameCharacter(
    val id: Long,
    val name: String,
    var health: Int,
    val attackPower: Int,
    val exp: Int
) {
    val characterClass: String get() = this::class.simpleName ?: "Unknown"

    open fun receiveAttack(attackPower: Int): String {
        health -= attackPower
        if (health <= 0) {
            health = 0
            val action = "$name has been defeated, health: $health"
            logger.info { action }
            return action
        } else {
            val action = "$name has $health health remaining"
            logger.info { action }
            return action
        }
    }

    abstract fun attack(target: GameCharacter): String
}

data class DefendResult(val attackPower: Int, val action: String)

interface Defender {
    val name: String
    var stamina: Int
    val defensePower: Int
    fun defend(attackPower: Int): DefendResult
}

interface Healer {
    var mana: Int
    val healingPower: Int
    fun heal(): String
}

class Warrior(
    id: Long,
    name: String,
    health: Int,
    attackPower: Int,
    exp: Int,
    override var stamina: Int = 3,
    override val defensePower: Int = 5,
) : GameCharacter(id, name, health, attackPower, exp), Defender {
    override fun defend(attackPower: Int): DefendResult {
        return if (stamina <= 0) {
            val action = "$name is too tired to defend"
            logger.info { action }
            return DefendResult(attackPower, action)
        } else {
            val action = "$name raises shield and defends against $defensePower damage"
            stamina -= 1
            val atkPwd = (attackPower - defensePower).coerceAtLeast(0)
            logger.info { action }
            DefendResult(atkPwd, action)
        }
    }

    override fun receiveAttack(attackPower: Int): String {
        return super.receiveAttack(defend(attackPower).attackPower)
    }

    override fun attack(target: GameCharacter): String {
        if (health <= 0) {
            val action = "$name is dead and cannot attack"
            logger.info { action }
            return action
        } else if (stamina <= 0) {
            val action = "$name is too tired to attack"
            logger.info { action }
            return action
        } else {
            target.receiveAttack(attackPower)
            stamina -= 1
            val action = "$name swings a sword at ${target.name}. $stamina left."
            logger.info { action }
            return action
        }
    }
}

class Sorcerer(
    id: Long,
    name: String,
    health: Int,
    attackPower: Int,
    exp: Int,
    override var mana: Int = 3,
    override val healingPower: Int = 10,
) : GameCharacter(id, name, health, attackPower, exp), Healer {
    override fun heal(): String {
        if (mana <= 0) {
            val action = "$name is out of mana"
            logger.info { action }
            return action
        } else {
            mana -= 1
            health += healingPower
            val action = "$name heals self to $health health"
            logger.info { action }
            return action
        }
    }

    override fun attack(target: GameCharacter): String {
        if (health <= 0) {
            val action = "$name is dead and cannot attack"
            logger.info { action }
            return action
        } else {
            // Heal before attacking
            heal()
            if (mana <= 0) {
                val action = "$name is out of mana"
                logger.info { action }
                return action
            } else {
                val action = "$name casts a spell at ${target.name}"
                target.receiveAttack(attackPower)
                mana -= 1
                logger.info { action }
                return action
            }
        }
    }
}
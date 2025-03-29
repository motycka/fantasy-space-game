package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.model.CharacterStats
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

data class CharacterInBattle (
    override val health: Int,
    override val attackPower: Int,
    override val defensePower: Int,
    override val stamina: Int,
    override val healingPower: Int,
    override val mana: Int,
): CharacterStats {
    fun isAlive() : Boolean {
        return health > 0
    }

    // general functions

    // return the target character after the attack
    fun attack(target: CharacterInBattle) : Pair<CharacterInBattle, CharacterInBattle> {
        if (!isAlive()) {
            logger.debug { "Cannot perform actions on a dead character" }
            return Pair(this, target)
        }

        if (!target.isAlive()) {
            logger.debug { "Cannot attack a dead character" }
            return Pair(this, target)
        }

        var attacker = this

        return when {
            stamina > 0 -> {
                attacker = attacker.copy(stamina = attacker.stamina - 1)
                Pair(attacker, target.receiveAttack(attacker))
            }
            mana > 0 -> {
                attacker = attacker.copy(mana = attacker.mana - 1)
                Pair(attacker, target.receiveAttack(attacker))
            }
            else -> {
                logger.debug { "Cannot attack without stamina" }
                Pair(attacker, target)
            }
        }
    }

    // return self after the attack
    fun receiveAttack(attacker: CharacterInBattle) : CharacterInBattle {
        if (!isAlive()) {
            logger.debug { "Cannot perform actions on a dead character" }
            return this
        }

        var stamina = this.stamina
        var damage = attacker.attackPower

        if (stamina > 0) {
            stamina -= 1
            damage = (attacker.attackPower - defensePower).coerceAtLeast(0)
        }
        else{
            logger.debug { "Cannot defend without stamina" }
        }

        return copy(health = (health - damage).coerceAtLeast(0), stamina = stamina)
    }

    fun beforeRound() : CharacterInBattle {
        if (!isAlive()) {
            logger.debug { "Cannot perform actions on a dead character" }
            return this
        }

        var beforeRoundCharacter = this
        beforeRoundCharacter = beforeRoundCharacter.heal()
        return beforeRoundCharacter
    }

    fun afterRound() : CharacterInBattle {
        if (!isAlive()) {
            logger.debug { "Cannot perform actions on a dead character" }
            return this
        }
        // does nothing for now
        return this
    }

    // specific functions: Sorcerer
    fun heal() : CharacterInBattle {
        if (!isAlive()){
            logger.debug { "Cannot heal a dead character" }
            return this
        }

        if (mana == 0) {
            logger.debug { "Cannot heal without mana" }
            return this
        }
        return copy(health = health + healingPower, mana = mana - 1)
    }


}


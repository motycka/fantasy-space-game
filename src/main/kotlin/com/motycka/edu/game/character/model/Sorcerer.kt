package com.motycka.edu.game.character.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.rest.CharacterClass
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class Sorcerer(
    id: CharacterId?,
    accountId: AccountId?,
    name: String,
    health: Int,
    attack: Int,
    level: CharacterLevel,
    experience: Int,
    val mana: Int,
    val healing: Int,
) : Character(
    id = id,
    accountId = accountId,
    name = name,
    health = health,
    attack = attack,
    energy = mana,
    ability = healing,
    level = level,
    experience = experience,
    characterClass = CharacterClass.SORCERER
), Healer {

    private var currentMana: Int = mana

    override fun attack(target: Character) {
        heal()
        when {
            currentHealth <= 0 -> logger.info { "$name is dead and cannot attack" }
            currentMana <= 0 -> logger.info { "$name out of mana" }
            else -> {
                logger.info { "$name casts a spell at ${target.name}" }
                target.receiveAttack(attack)
                currentMana--
            }
        }
    }

    override fun heal() {
        when {
            currentHealth <= 0 -> logger.info { "$name is dead and cannot heal"}
            currentMana <= 0 -> logger.info { "$name is out of mana" }
            else -> {
                if (currentHealth + healing > health) {
                    currentHealth = health
                } else {
                    currentHealth += healing
                }
                logger.info { "$name heals self to $currentHealth health" }
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

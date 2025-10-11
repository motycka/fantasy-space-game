package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.error.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService,
) {

    fun createCharacter(character: Character): Character {
        return characterRepository.insertCharacters(
            accountId = accountService.getCurrentAccountId(),
            character = character
        ) ?: error(CREATE_ERROR)
    }

    fun getCharacters(filter: CharactersFilter): List<Character> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectWithFilter(accountId, filter)
    }

    fun getCharacter(characterId: CharacterId): Character {
        return getCharacters(
            CharactersFilter(
                ids = setOf(characterId),
                includeChallengers = true,
                includeOpponents = true
            )
        ).firstOrNull() ?: throw NotFoundException()
    }

    @Transactional
    fun updateCharacter(character: Character): Character {
        val existing = getCharacter(requireNotNull(character.id))

        if (existing.canLevelUp()) {

            // Validate attributes don't decrease
            validateAttributesNotDecreased(existing, character)

            // Validate all points are assigned for new level
            validateAllPointsAssigned(character)
        }

        return characterRepository.updateCharacter(character) ?: error(UPDATE_ERROR)
    }

    @Transactional
    fun updateExperience(characterId: CharacterId, gainedExperience: Int): Character? {
        return characterRepository.updateExperience(characterId, gainedExperience)
    }

    private fun validateAttributesNotDecreased(existing: Character, updated: Character) {
        require(updated.health >= existing.health) {
            "Health cannot be decreased during level up (was ${existing.health}, attempted ${updated.health})"
        }
        require(updated.attack >= existing.attack) {
            "Attack power cannot be decreased during level up (was ${existing.attack}, attempted ${updated.attack})"
        }

        when {
            existing is Warrior && updated is Warrior -> {
                require(updated.stamina >= existing.stamina) {
                    "Stamina cannot be decreased during level up (was ${existing.stamina}, attempted ${updated.stamina})"
                }
                require(updated.defense >= existing.defense) {
                    "Defense power cannot be decreased during level up (was ${existing.defense}, attempted ${updated.defense})"
                }
            }
            existing is Sorcerer && updated is Sorcerer -> {
                require(updated.mana >= existing.mana) {
                    "Mana cannot be decreased during level up (was ${existing.mana}, attempted ${updated.mana})"
                }
                require(updated.healing >= existing.healing) {
                    "Healing power cannot be decreased during level up (was ${existing.healing}, attempted ${updated.healing})"
                }
            }
        }
    }

    private fun validateAllPointsAssigned(character: Character) {
        val expectedPoints = character.shouldBeLevel().points
        val assignedPoints = character.allPoints

        require(assignedPoints == expectedPoints) {
            "All points must be assigned for level ${character.shouldBeLevel().name}. Expected: $expectedPoints, Assigned: $assignedPoints"
        }
    }

    companion object {
        const val CREATE_ERROR = "Character could not be created."
        const val UPDATE_ERROR = "Character could not be updated."
    }

}


/**
 * Checks if this character has enough experience to level up.
 *
 * A character can level up when their current experience qualifies them for a higher level
 * than their assigned level property.
 *
 * @return true if the character should be at a higher level based on their experience
 *
 * Example:
 * ```
 * val character = Warrior(..., level = LEVEL_1, experience = 1500)
 * character.canLevelUp() // Returns true (1500 XP qualifies for LEVEL_2)
 * ```
 */
fun Character.canLevelUp(): Boolean {
    return level.ordinal < shouldBeLevel().ordinal
}

/**
 * Determines the level this character should be at based on their current experience.
 *
 * This is a domain-focused convenience method that provides a character-centric API for
 * level calculation. It wraps [CharacterLevel.getByExperience] to improve code readability
 * when working with Character instances.
 *
 * @return The CharacterLevel this character qualifies for based on their experience
 *
 * @see CharacterLevel.getByExperience Low-level utility for experience-to-level conversion
 *
 * Example:
 * ```
 * val character = Warrior(..., level = LEVEL_1, experience = 3500)
 * character.shouldBeLevel() // Returns LEVEL_3
 *
 * // Compare with low-level API:
 * CharacterLevel.getByExperience(3500) // Also returns LEVEL_3, but less readable in character context
 * ```
 */
fun Character.shouldBeLevel(): CharacterLevel {
    return CharacterLevel.getByExperience(experience)
}

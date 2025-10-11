package com.motycka.edu.game.character

import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.character.rest.CharacterClass
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.error.GameException
import kotlin.random.Random

/**
 * Factory object for creating Character instances.
 *
 * This demonstrates the Factory Design Pattern, which provides an interface
 * for creating objects without specifying their exact classes.
 *
 * ## Design Pattern: Factory Method
 * The Factory pattern is a creational pattern that:
 * - Encapsulates object creation logic
 * - Provides a common interface for creating different types
 * - Allows for easy extension without modifying existing code
 *
 * ## SOLID Principles Demonstrated:
 * - **Single Responsibility**: Only responsible for character creation
 * - **Open/Closed**: New character types can be added without modifying existing methods
 * - **Dependency Inversion**: Clients depend on Character abstraction, not concrete classes
 *
 * ## Teaching Points:
 * - Object declaration creates a singleton in Kotlin
 * - Factory methods hide complex initialization logic
 * - Pattern reduces coupling between client code and concrete classes
 *
 * @see Character
 * @see <a href="../lessons/lesson-10.md">Lesson 10: Design Patterns - Factory Pattern</a>
 */
object CharacterFactory {

    /**
     * Creates a character based on the specified class type.
     *
     * This is the main factory method that delegates to specific creation methods.
     *
     * @param characterClass The type of character to create
     * @param attributes The attributes for the character
     * @return A new Character instance of the appropriate type
     * @throws GameException.InvalidCharacterStateException if attributes are invalid
     */
    fun createCharacter(
        characterClass: CharacterClass,
        attributes: CharacterAttributes
    ): Character = when (characterClass) {
        CharacterClass.WARRIOR -> createWarrior(attributes)
        CharacterClass.SORCERER -> createSorcerer(attributes)
    }

    /**
     * Creates a Warrior character with validated attributes.
     */
    private fun createWarrior(attributes: CharacterAttributes): Warrior {
        requireNotNull(attributes.stamina) { "Stamina is required for Warriors" }
        requireNotNull(attributes.defense) { "Defense is required for Warriors" }

        return Warrior(
            id = attributes.id,
            accountId = attributes.accountId,
            name = attributes.name,
            health = attributes.health,
            attack = attributes.attack,
            level = attributes.level,
            experience = attributes.experience,
            stamina = attributes.stamina,
            defense = attributes.defense
        )
    }

    /**
     * Creates a Sorcerer character with validated attributes.
     */
    private fun createSorcerer(attributes: CharacterAttributes): Sorcerer {
        requireNotNull(attributes.mana) { "Mana is required for Sorcerers" }
        requireNotNull(attributes.healing) { "Healing power is required for Sorcerers" }

        return Sorcerer(
            id = attributes.id,
            accountId = attributes.accountId,
            name = attributes.name,
            health = attributes.health,
            attack = attributes.attack,
            level = attributes.level,
            experience = attributes.experience,
            mana = attributes.mana,
            healing = attributes.healing
        )
    }

    /**
     * Creates a random character for testing or NPC generation.
     *
     * This demonstrates a factory method variation that encapsulates
     * complex creation logic and randomization.
     *
     * @param name The character's name
     * @param level The character's level
     * @param characterClass Optional class specification (random if not provided)
     * @return A randomly generated character
     */
    fun createRandomCharacter(
        name: String,
        level: CharacterLevel = CharacterLevel.LEVEL_1,
        characterClass: CharacterClass? = null
    ): Character {
        val selectedClass = characterClass ?: CharacterClass.entries.random()
        val totalPoints = level.points

        // Distribute points randomly but ensure minimum values
        val basePoints = 10
        val remainingPoints = totalPoints - (basePoints * 4)

        val health = basePoints + Random.nextInt(0, remainingPoints / 2)
        val attack = basePoints + Random.nextInt(0, remainingPoints / 3)
        val energyPoints = basePoints + Random.nextInt(0, remainingPoints / 3)
        val abilityPoints = totalPoints - health - attack - energyPoints

        val attributes = when (selectedClass) {
            CharacterClass.WARRIOR -> CharacterAttributes(
                name = name,
                health = health,
                attack = attack,
                level = level,
                experience = level.minimumExperience,
                stamina = energyPoints,
                defense = abilityPoints
            )
            CharacterClass.SORCERER -> CharacterAttributes(
                name = name,
                health = health,
                attack = attack,
                level = level,
                experience = level.minimumExperience,
                mana = energyPoints,
                healing = abilityPoints
            )
        }

        return createCharacter(selectedClass, attributes)
    }

    /**
     * Creates a character with balanced attributes for the given level.
     *
     * This factory method provides pre-balanced characters suitable for
     * fair gameplay, demonstrating encapsulation of balance logic.
     *
     * @param name The character's name
     * @param characterClass The type of character to create
     * @param level The character's level
     * @return A balanced character for the specified level
     */
    fun createBalancedCharacter(
        name: String,
        characterClass: CharacterClass,
        level: CharacterLevel = CharacterLevel.LEVEL_1,
        accountId: AccountId? = null
    ): Character {
        val totalPoints = level.points
        val pointsPerAttribute = totalPoints / 4

        val attributes = when (characterClass) {
            CharacterClass.WARRIOR -> CharacterAttributes(
                id = null,
                accountId = accountId,
                name = name,
                health = pointsPerAttribute + (totalPoints % 4), // Extra points go to health
                attack = pointsPerAttribute,
                level = level,
                experience = level.minimumExperience,
                stamina = pointsPerAttribute,
                defense = pointsPerAttribute
            )
            CharacterClass.SORCERER -> CharacterAttributes(
                id = null,
                accountId = accountId,
                name = name,
                health = pointsPerAttribute + (totalPoints % 4), // Extra points go to health
                attack = pointsPerAttribute,
                level = level,
                experience = level.minimumExperience,
                mana = pointsPerAttribute,
                healing = pointsPerAttribute
            )
        }

        return createCharacter(characterClass, attributes)
    }

    /**
     * Creates a character optimized for a specific play style.
     *
     * This demonstrates the Strategy pattern within the Factory pattern,
     * where creation strategy varies based on the intended play style.
     */
    fun createOptimizedCharacter(
        name: String,
        characterClass: CharacterClass,
        playStyle: PlayStyle,
        level: CharacterLevel = CharacterLevel.LEVEL_1
    ): Character {
        val totalPoints = level.points

        val attributes = when (characterClass) {
            CharacterClass.WARRIOR -> when (playStyle) {
                PlayStyle.AGGRESSIVE -> CharacterAttributes(
                    name = name,
                    health = totalPoints * 2 / 10,
                    attack = totalPoints * 4 / 10,
                    level = level,
                    experience = level.minimumExperience,
                    stamina = totalPoints * 3 / 10,
                    defense = totalPoints * 1 / 10
                )
                PlayStyle.DEFENSIVE -> CharacterAttributes(
                    name = name,
                    health = totalPoints * 3 / 10,
                    attack = totalPoints * 1 / 10,
                    level = level,
                    experience = level.minimumExperience,
                    stamina = totalPoints * 2 / 10,
                    defense = totalPoints * 4 / 10
                )
                PlayStyle.BALANCED -> createBalancedCharacter(name, characterClass, level).let {
                    CharacterAttributes.fromCharacter(it as Warrior)
                }
            }
            CharacterClass.SORCERER -> when (playStyle) {
                PlayStyle.AGGRESSIVE -> CharacterAttributes(
                    name = name,
                    health = totalPoints * 2 / 10,
                    attack = totalPoints * 4 / 10,
                    level = level,
                    experience = level.minimumExperience,
                    mana = totalPoints * 3 / 10,
                    healing = totalPoints * 1 / 10
                )
                PlayStyle.DEFENSIVE -> CharacterAttributes(
                    name = name,
                    health = totalPoints * 3 / 10,
                    attack = totalPoints * 1 / 10,
                    level = level,
                    experience = level.minimumExperience,
                    mana = totalPoints * 2 / 10,
                    healing = totalPoints * 4 / 10
                )
                PlayStyle.BALANCED -> createBalancedCharacter(name, characterClass, level).let {
                    CharacterAttributes.fromCharacter(it as Sorcerer)
                }
            }
        }

        return createCharacter(characterClass, attributes)
    }
}

/**
 * Data class holding character attributes for factory creation.
 *
 * This demonstrates the use of data classes as parameter objects,
 * reducing method parameter count and improving readability.
 */
data class CharacterAttributes(
    val id: CharacterId? = null,
    val accountId: AccountId? = null,
    val name: String,
    val health: Int,
    val attack: Int,
    val level: CharacterLevel,
    val experience: Int,
    val stamina: Int? = null,
    val defense: Int? = null,
    val mana: Int? = null,
    val healing: Int? = null
) {
    companion object {
        /**
         * Creates attributes from an existing Warrior.
         */
        fun fromCharacter(warrior: Warrior) = CharacterAttributes(
            id = warrior.id,
            accountId = warrior.accountId,
            name = warrior.name,
            health = warrior.health,
            attack = warrior.attack,
            level = warrior.level,
            experience = warrior.experience,
            stamina = warrior.stamina,
            defense = warrior.defense
        )

        /**
         * Creates attributes from an existing Sorcerer.
         */
        fun fromCharacter(sorcerer: Sorcerer) = CharacterAttributes(
            id = sorcerer.id,
            accountId = sorcerer.accountId,
            name = sorcerer.name,
            health = sorcerer.health,
            attack = sorcerer.attack,
            level = sorcerer.level,
            experience = sorcerer.experience,
            mana = sorcerer.mana,
            healing = sorcerer.healing
        )
    }
}

/**
 * Enum representing different play styles for character optimization.
 */
enum class PlayStyle {
    AGGRESSIVE,
    DEFENSIVE,
    BALANCED
}
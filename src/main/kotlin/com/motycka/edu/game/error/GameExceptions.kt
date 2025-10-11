package com.motycka.edu.game.error

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.model.CharacterLevel

/**
 * Base sealed class for all game-specific exceptions.
 *
 * This demonstrates the use of sealed classes for creating a closed hierarchy
 * of exceptions, ensuring exhaustive handling in when expressions.
 *
 * ## Design Principles:
 * - **Single Responsibility**: Each exception has one clear error condition
 * - **Open/Closed**: New exceptions can be added without modifying existing ones
 * - **Liskov Substitution**: All game exceptions can be caught as GameException
 *
 * ## Teaching Points:
 * - Sealed classes provide compile-time exhaustiveness checking
 * - Custom exceptions improve error handling clarity
 * - Hierarchical exceptions allow both specific and general error handling
 *
 * @see <a href="../lessons/lesson-05.md">Lesson 5: Exception Handling</a>
 */
sealed class GameException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    /**
     * Thrown when a requested character cannot be found.
     *
     * @property characterId The ID of the character that was not found
     */
    data class CharacterNotFoundException(
        val characterId: CharacterId
    ) : GameException("Character with ID $characterId not found")

    /**
     * Thrown when attempting to assign more points than available for a character's level.
     *
     * @property required The number of points attempted to assign
     * @property available The maximum points available at the character's level
     * @property level The character's current level
     */
    data class InsufficientPointsException(
        val required: Int,
        val available: Int,
        val level: CharacterLevel
    ) : GameException(
        "Cannot assign $required points. Only $available points available at $level"
    )

    /**
     * Thrown when a match cannot be created due to invalid conditions.
     *
     * @property reason Detailed explanation of why the match is invalid
     */
    data class InvalidMatchException(
        val reason: String
    ) : GameException("Invalid match: $reason")

    /**
     * Thrown when a character cannot level up due to insufficient experience or max level.
     *
     * @property currentLevel The character's current level
     * @property currentExperience The character's current experience points
     * @property reason Additional context about why leveling failed
     */
    data class LevelUpException(
        val currentLevel: CharacterLevel,
        val currentExperience: Int,
        val reason: String
    ) : GameException(
        "Cannot level up from $currentLevel with $currentExperience XP: $reason"
    )

    /**
     * Thrown when trying to perform an action with insufficient resources.
     *
     * @property resourceType The type of resource (e.g., "mana", "stamina")
     * @property required Amount of resource required
     * @property available Current amount available
     */
    data class InsufficientResourceException(
        val resourceType: String,
        val required: Int,
        val available: Int
    ) : GameException(
        "Insufficient $resourceType: required $required, but only $available available"
    )

    /**
     * Thrown when an invalid character state transition is attempted.
     *
     * @property characterName The name of the character
     * @property action The action that was attempted
     * @property state The current state preventing the action
     */
    data class InvalidCharacterStateException(
        val characterName: String,
        val action: String,
        val state: String
    ) : GameException(
        "$characterName cannot $action: $state"
    )
}

/**
 * Extension function to safely handle game exceptions with logging.
 *
 * This demonstrates Kotlin's extension functions and functional error handling.
 *
 * Example:
 * ```kotlin
 * runCatching { findCharacter(id) }
 *     .handleGameException { exception ->
 *         logger.error { "Failed to find character: $exception" }
 *         null
 *     }
 * ```
 */
inline fun <T> Result<T>.handleGameException(
    handler: (GameException) -> T
): T = this.getOrElse { throwable ->
    when (throwable) {
        is GameException -> handler(throwable)
        else -> throw throwable
    }
}
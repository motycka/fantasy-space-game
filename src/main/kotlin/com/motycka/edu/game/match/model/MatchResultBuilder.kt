package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.error.GameException

/**
 * Builder class for constructing MatchResult instances.
 *
 * This demonstrates the Builder Design Pattern, which separates the construction
 * of complex objects from their representation, allowing the same construction
 * process to create different representations.
 *
 * ## Design Pattern: Builder
 * The Builder pattern is a creational pattern that:
 * - Handles the creation of complex objects step by step
 * - Provides a fluent interface for better readability
 * - Encapsulates validation logic during construction
 * - Allows optional parameters without telescoping constructors
 *
 * ## SOLID Principles Demonstrated:
 * - **Single Responsibility**: Only responsible for building MatchResult
 * - **Open/Closed**: Can be extended for new match types without modification
 * - **Interface Segregation**: Each method has a single, clear purpose
 *
 * ## Teaching Points:
 * - Fluent interface design with method chaining
 * - Validation during build process prevents invalid objects
 * - Alternative to telescoping constructors or large parameter lists
 * - Kotlin's apply scope function for cleaner builder usage
 *
 * ## Usage Example:
 * ```kotlin
 * val matchResult = MatchResultBuilder()
 *     .withChallenger(challengerId, challengerXP)
 *     .withOpponent(opponentId, opponentXP)
 *     .withOutcome(MatchOutcome.CHALLENGER_WON)
 *     .build()
 * ```
 *
 * @see MatchResult
 * @see <a href="../lessons/lesson-10.md">Lesson 10: Design Patterns - Builder Pattern</a>
 */
class MatchResultBuilder {
    private var id: MatchId? = null
    private var challengerId: CharacterId? = null
    private var challengerExperience: Int? = null
    private var opponentId: CharacterId? = null
    private var opponentExperience: Int? = null
    private var matchOutcome: MatchOutcome? = null

    /**
     * Sets the match ID (optional, usually for updates).
     *
     * @param id The unique identifier for the match
     * @return This builder instance for chaining
     */
    fun withId(id: MatchId) = apply {
        this.id = id
    }

    /**
     * Sets the challenger information.
     *
     * This demonstrates method overloading in the builder pattern,
     * providing multiple ways to set related data.
     *
     * @param challengerId The ID of the challenging character
     * @param experience Experience points gained by the challenger
     * @return This builder instance for chaining
     */
    fun withChallenger(challengerId: CharacterId, experience: Int) = apply {
        this.challengerId = challengerId
        this.challengerExperience = experience
    }

    /**
     * Sets only the challenger ID.
     *
     * @param challengerId The ID of the challenging character
     * @return This builder instance for chaining
     */
    fun withChallengerId(challengerId: CharacterId) = apply {
        this.challengerId = challengerId
    }

    /**
     * Sets only the challenger's experience.
     *
     * @param experience Experience points gained by the challenger
     * @return This builder instance for chaining
     */
    fun withChallengerExperience(experience: Int) = apply {
        this.challengerExperience = experience
    }

    /**
     * Sets the opponent information.
     *
     * @param opponentId The ID of the opponent character
     * @param experience Experience points gained by the opponent
     * @return This builder instance for chaining
     */
    fun withOpponent(opponentId: CharacterId, experience: Int) = apply {
        this.opponentId = opponentId
        this.opponentExperience = experience
    }

    /**
     * Sets only the opponent ID.
     *
     * @param opponentId The ID of the opponent character
     * @return This builder instance for chaining
     */
    fun withOpponentId(opponentId: CharacterId) = apply {
        this.opponentId = opponentId
    }

    /**
     * Sets only the opponent's experience.
     *
     * @param experience Experience points gained by the opponent
     * @return This builder instance for chaining
     */
    fun withOpponentExperience(experience: Int) = apply {
        this.opponentExperience = experience
    }

    /**
     * Sets the match outcome.
     *
     * @param outcome The result of the match
     * @return This builder instance for chaining
     */
    fun withOutcome(outcome: MatchOutcome) = apply {
        this.matchOutcome = outcome
    }

    /**
     * Builds the MatchResult with validation.
     *
     * This method demonstrates the importance of validation in builders,
     * ensuring that only valid objects can be created.
     *
     * @return A validated MatchResult instance
     * @throws GameException.InvalidMatchException if required fields are missing
     * @throws IllegalArgumentException if validation fails
     */
    fun build(): MatchResult {
        // Validate required fields
        val validChallengerId = challengerId
            ?: throw GameException.InvalidMatchException("Challenger ID is required")
        val validOpponentId = opponentId
            ?: throw GameException.InvalidMatchException("Opponent ID is required")
        val validOutcome = matchOutcome
            ?: throw GameException.InvalidMatchException("Match outcome is required")

        // Validate experience points
        val validChallengerXP = challengerExperience
            ?: throw GameException.InvalidMatchException("Challenger experience is required")
        val validOpponentXP = opponentExperience
            ?: throw GameException.InvalidMatchException("Opponent experience is required")

        // Business rule validations
        require(validChallengerId != validOpponentId) {
            "Challenger and opponent must be different characters"
        }
        require(validChallengerXP >= 0) {
            "Challenger experience cannot be negative"
        }
        require(validOpponentXP >= 0) {
            "Opponent experience cannot be negative"
        }

        return MatchResult(
            id = id,
            challengerId = validChallengerId,
            challengerExperience = validChallengerXP,
            opponentId = validOpponentId,
            opponentExperience = validOpponentXP,
            matchOutcome = validOutcome
        )
    }

    /**
     * Creates a copy of the current builder state.
     *
     * This demonstrates the prototype pattern within the builder,
     * allowing for creating variations of similar objects.
     *
     * @return A new builder with the same state as this one
     */
    fun copy(): MatchResultBuilder = MatchResultBuilder().apply {
        this.id = this@MatchResultBuilder.id
        this.challengerId = this@MatchResultBuilder.challengerId
        this.challengerExperience = this@MatchResultBuilder.challengerExperience
        this.opponentId = this@MatchResultBuilder.opponentId
        this.opponentExperience = this@MatchResultBuilder.opponentExperience
        this.matchOutcome = this@MatchResultBuilder.matchOutcome
    }

    /**
     * Resets the builder to its initial state.
     *
     * Useful for reusing the same builder instance for multiple objects.
     *
     * @return This builder instance for chaining
     */
    fun reset() = apply {
        id = null
        challengerId = null
        challengerExperience = null
        opponentId = null
        opponentExperience = null
        matchOutcome = null
    }

    companion object {
        /**
         * Creates a builder pre-populated for a draw match.
         *
         * This demonstrates static factory methods in builders,
         * providing convenient starting points for common scenarios.
         *
         * @param challengerId The challenger's ID
         * @param opponentId The opponent's ID
         * @param experiencePoints Experience awarded to both participants
         * @return A pre-configured builder
         */
        fun drawMatch(
            challengerId: CharacterId,
            opponentId: CharacterId,
            experiencePoints: Int = 50
        ): MatchResultBuilder = MatchResultBuilder().apply {
            withChallenger(challengerId, experiencePoints)
            withOpponent(opponentId, experiencePoints)
            withOutcome(MatchOutcome.DRAW)
        }

        /**
         * Creates a builder from an existing MatchResult.
         *
         * Useful for creating modified copies of existing matches.
         *
         * @param matchResult The match result to copy
         * @return A builder pre-populated with the match data
         */
        fun from(matchResult: MatchResult): MatchResultBuilder = MatchResultBuilder().apply {
            matchResult.id?.let { withId(it) }
            withChallenger(matchResult.challengerId, matchResult.challengerExperience)
            withOpponent(matchResult.opponentId, matchResult.opponentExperience)
            withOutcome(matchResult.matchOutcome)
        }
    }
}

/**
 * Extension function to convert a MatchResult to a builder.
 *
 * This demonstrates Kotlin's extension functions for adding
 * functionality to existing classes without inheritance.
 *
 * Example:
 * ```kotlin
 * val modifiedMatch = existingMatch
 *     .toBuilder()
 *     .withChallengerExperience(150)
 *     .build()
 * ```
 */
fun MatchResult.toBuilder(): MatchResultBuilder = MatchResultBuilder.from(this)

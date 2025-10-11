package com.motycka.edu.game.match.model

/**
 * Sealed class representing the possible outcomes of a match.
 *
 * This demonstrates the use of sealed classes in Kotlin, which provide:
 * - Type-safe restricted class hierarchies
 * - Exhaustive when expressions
 * - Data associated with each outcome type
 * - Better than enums when you need to associate state with each value
 *
 * ## Design Pattern: State Pattern Variation
 * Sealed classes can represent different states with their own properties,
 * similar to the State pattern but with compile-time guarantees.
 *
 * ## SOLID Principles:
 * - **Open/Closed**: New outcome types can be added without modifying existing code
 * - **Single Responsibility**: Each outcome type encapsulates its own data
 *
 * ## Teaching Points:
 * - Sealed classes are abstract by default
 * - All subclasses must be defined in the same file (or as nested classes)
 * - When expressions on sealed classes don't need else branch if exhaustive
 * - Each subclass can have different properties and behaviors
 *
 * ## Usage Example:
 * ```kotlin
 * when (outcome) {
 *     is MatchOutcome.ChallengerWon -> println("Winner: ${outcome.victoryMessage}")
 *     is MatchOutcome.OpponentWon -> println("Winner: ${outcome.victoryMessage}")
 *     is MatchOutcome.Draw -> println("Draw after ${outcome.rounds} rounds")
 * }
 * ```
 *
 * @see <a href="../lessons/lesson-05.md">Lesson 5: Sealed Classes in Kotlin</a>
 */
sealed class MatchOutcome {
    /**
     * Outcome when the challenger wins the match.
     *
     * @property healthRemaining The challenger's remaining health
     * @property roundsWon Number of rounds won by the challenger
     * @property perfectVictory True if challenger won without taking damage
     */
    data class ChallengerWon(
        val healthRemaining: Int = 0,
        val roundsWon: Int = 0,
        val perfectVictory: Boolean = false
    ) : MatchOutcome() {
        /**
         * Generates a victory message based on the win conditions.
         */
        val victoryMessage: String
            get() = when {
                perfectVictory -> "Flawless victory! The challenger dominated!"
                healthRemaining > 50 -> "Decisive victory for the challenger!"
                else -> "The challenger emerges victorious!"
            }
    }

    /**
     * Outcome when the opponent wins the match.
     *
     * @property healthRemaining The opponent's remaining health
     * @property roundsWon Number of rounds won by the opponent
     * @property comeback True if opponent won after being behind
     */
    data class OpponentWon(
        val healthRemaining: Int = 0,
        val roundsWon: Int = 0,
        val comeback: Boolean = false
    ) : MatchOutcome() {
        /**
         * Generates a victory message based on the win conditions.
         */
        val victoryMessage: String
            get() = when {
                comeback -> "Incredible comeback victory by the opponent!"
                healthRemaining > 50 -> "The opponent claims a decisive victory!"
                else -> "The opponent stands victorious!"
            }
    }

    /**
     * Outcome when the match ends in a draw.
     *
     * @property rounds Total number of rounds fought
     * @property reason Why the match ended in a draw
     */
    data class Draw(
        val rounds: Int = 0,
        val reason: DrawReason = DrawReason.TIME_LIMIT
    ) : MatchOutcome() {
        /**
         * Generates a draw message based on the reason.
         */
        val drawMessage: String
            get() = when (reason) {
                DrawReason.TIME_LIMIT -> "Match ended in a draw after $rounds rounds!"
                DrawReason.MUTUAL_DEFEAT -> "Both fighters fell simultaneously!"
                DrawReason.STALEMATE -> "Neither fighter could land a decisive blow!"
            }
    }

    /**
     * Converts this sealed class instance to the legacy enum value.
     *
     * This is provided for backward compatibility with existing code
     * that expects the enum values. Should be removed once all code
     * is migrated to use the sealed class directly.
     *
     * @deprecated Use the sealed class directly for new code
     */
    @Deprecated(
        message = "Use sealed class properties directly",
        replaceWith = ReplaceWith("this"),
        level = DeprecationLevel.WARNING
    )
    fun toLegacyEnum(): String = when (this) {
        is ChallengerWon -> "CHALLENGER_WON"
        is OpponentWon -> "OPPONENT_WON"
        is Draw -> "DRAW"
    }

    /**
     * Checks if this outcome represents a victory for either party.
     */
    val isVictory: Boolean
        get() = this is ChallengerWon || this is OpponentWon

    /**
     * Checks if this outcome represents a draw.
     */
    val isDraw: Boolean
        get() = this is Draw

    /**
     * Gets the experience multiplier for this outcome type.
     *
     * This demonstrates polymorphic behavior in sealed classes,
     * where each subclass can override properties differently.
     */
    open val experienceMultiplier: Double
        get() = when (this) {
            is ChallengerWon -> if (perfectVictory) 2.0 else 1.5
            is OpponentWon -> if (comeback) 1.8 else 1.5
            is Draw -> 0.5
        }

    companion object {
        /**
         * Creates a simple challenger win outcome.
         */
        fun challengerWon(
            healthRemaining: Int = 0,
            roundsWon: Int = 0
        ): MatchOutcome = ChallengerWon(
            healthRemaining = healthRemaining,
            roundsWon = roundsWon,
            perfectVictory = healthRemaining >= 100
        )

        /**
         * Creates a simple opponent win outcome.
         */
        fun opponentWon(
            healthRemaining: Int = 0,
            roundsWon: Int = 0
        ): MatchOutcome = OpponentWon(
            healthRemaining = healthRemaining,
            roundsWon = roundsWon,
            comeback = false
        )

        /**
         * Creates a draw outcome.
         */
        fun draw(
            rounds: Int = 0,
            reason: DrawReason = DrawReason.TIME_LIMIT
        ): MatchOutcome = Draw(rounds, reason)

        /**
         * Parses a legacy enum string to the new sealed class.
         *
         * @deprecated For migration purposes only
         */
        @Deprecated("For migration from enum only")
        fun fromLegacyEnum(value: String): MatchOutcome = when (value) {
            "CHALLENGER_WON" -> ChallengerWon()
            "OPPONENT_WON" -> OpponentWon()
            "DRAW" -> Draw()
            else -> throw IllegalArgumentException("Unknown legacy outcome: $value")
        }
    }
}

/**
 * Enum representing reasons why a match ended in a draw.
 *
 * This shows that enums are still useful within sealed class hierarchies
 * for representing fixed sets of values without additional state.
 */
enum class DrawReason {
    /** Match reached the maximum number of rounds */
    TIME_LIMIT,

    /** Both fighters were defeated in the same round */
    MUTUAL_DEFEAT,

    /** Neither fighter could deal effective damage */
    STALEMATE
}

/**
 * Extension function to get a user-friendly description of the outcome.
 *
 * This demonstrates how extension functions can add behavior to
 * sealed class hierarchies without modifying the classes themselves.
 */
fun MatchOutcome.describe(): String = when (this) {
    is MatchOutcome.ChallengerWon -> victoryMessage
    is MatchOutcome.OpponentWon -> victoryMessage
    is MatchOutcome.Draw -> drawMessage
}
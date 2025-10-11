package com.motycka.edu.game.match.strategy

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.match.model.MatchOutcome
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.math.max

private val logger = KotlinLogging.logger {}

/**
 * Level-based experience calculation strategy that implements a risk/reward system.
 *
 * ## Design Philosophy:
 * - **Challengers** (active participants) get variable XP based on level difference and outcome
 * - **Opponents** (passive participants) get a flat minimal XP regardless of outcome
 * - Fighting higher-level opponents yields greater rewards
 * - Fighting lower-level opponents yields diminished rewards
 * - This discourages "farming" weaker opponents and encourages strategic matchmaking
 *
 * ## Formula:
 * ```
 * Opponent XP: Always 20 XP (flat fee for being selected as opponent)
 *
 * Challenger Base XP = BASE_XP * (1 + (opponentLevel - challengerLevel) * LEVEL_MULTIPLIER)
 *                    = 100 * (1 + levelDiff * 0.2)
 *
 * Challenger Final XP:
 *   - Win:  Base * WIN_MULTIPLIER  = Base * 1.5
 *   - Loss: LOSS_XP                = 15 XP (fixed)
 *   - Draw: Base * DRAW_MULTIPLIER = Base * 0.5
 * ```
 *
 * ## Examples:
 * - Level 1 vs Level 1 (equal):
 *   Win: 150 XP | Loss: 15 XP | Draw: 50 XP | Opponent: 20 XP
 *
 * - Level 1 vs Level 3 (+2 levels):
 *   Win: 210 XP | Loss: 15 XP | Draw: 70 XP | Opponent: 20 XP
 *
 * - Level 3 vs Level 1 (-2 levels):
 *   Win: 90 XP | Loss: 15 XP | Draw: 30 XP | Opponent: 20 XP
 *
 * @see ExperienceCalculationStrategy
 */
@Component
class LevelBasedExperienceStrategy : ExperienceCalculationStrategy {

    companion object {
        /** Base experience points before level adjustment */
        private const val BASE_XP = 100

        /** Multiplier applied per level difference (0.2 = 20% per level) */
        private const val LEVEL_MULTIPLIER = 0.2

        /** Multiplier applied to base XP for wins */
        private const val WIN_MULTIPLIER = 1.5

        /** Fixed XP awarded to challenger for losses (participation reward) */
        private const val LOSS_XP = 15

        /** Multiplier applied to base XP for draws */
        private const val DRAW_MULTIPLIER = 0.5

        /** Flat XP awarded to opponent (passive participant) */
        private const val OPPONENT_FLAT_XP = 20

        /** Minimum base XP to prevent negative values when fighting much lower levels */
        private const val MIN_BASE_XP = 60
    }

    override fun calculateExperience(
        outcome: MatchOutcome,
        challenger: Character,
        opponent: Character
    ): Pair<Int, Int> {
        // Opponent always gets flat XP
        val opponentExperience = OPPONENT_FLAT_XP

        // Calculate level difference (positive = fighting higher level)
        val levelDifference = opponent.level.ordinal - challenger.level.ordinal

        // Calculate base XP adjusted for level difference
        val baseExperience = max(
            MIN_BASE_XP,
            (BASE_XP * (1 + levelDifference * LEVEL_MULTIPLIER)).toInt()
        )

        // Calculate challenger XP based on outcome
        val challengerExperience = when (outcome) {
            is MatchOutcome.ChallengerWon -> (baseExperience * WIN_MULTIPLIER).toInt()
            is MatchOutcome.OpponentWon -> LOSS_XP
            is MatchOutcome.Draw -> (baseExperience * DRAW_MULTIPLIER).toInt()
        }

        logger.debug {
            "Experience calculation: " +
                "Challenger Lv${challenger.level.ordinal + 1} vs Opponent Lv${opponent.level.ordinal + 1}, " +
                "LevelDiff=$levelDifference, Base=$baseExperience, " +
                "Outcome=$outcome -> Challenger=$challengerExperience XP, Opponent=$opponentExperience XP"
        }

        return Pair(challengerExperience, opponentExperience)
    }
}

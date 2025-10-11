package com.motycka.edu.game.match.strategy

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.match.model.MatchOutcome

/**
 * Strategy interface for calculating experience gained by characters in a match.
 *
 * This allows different experience calculation algorithms to be plugged in,
 * making the system flexible and testable.
 */
interface ExperienceCalculationStrategy {

    /**
     * Calculates experience gained by both challenger and opponent based on match outcome.
     *
     * @param outcome The result of the match (CHALLENGER_WON, OPPONENT_WON, or DRAW)
     * @param challenger The character who initiated the match
     * @param opponent The character who was challenged (passive participant)
     * @return Pair of (challengerExperience, opponentExperience)
     */
    fun calculateExperience(
        outcome: MatchOutcome,
        challenger: Character,
        opponent: Character
    ): Pair<Int, Int>
}

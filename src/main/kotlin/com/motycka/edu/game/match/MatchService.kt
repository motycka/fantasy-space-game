package com.motycka.edu.game.match

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.Recoverable
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.leaderboard.LeaderboardService
import com.motycka.edu.game.match.model.MatchResult
import com.motycka.edu.game.match.model.MatchResultWithCharacters
import com.motycka.edu.game.match.model.MatchRoundResult
import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.match.model.DrawReason
import com.motycka.edu.game.match.model.MatchOutcome
import com.motycka.edu.game.match.strategy.ExperienceCalculationStrategy
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterService: CharacterService,
    private val accountService: AccountService,
    private val leaderboardService: LeaderboardService,
    private val experienceCalculationStrategy: ExperienceCalculationStrategy
) {

    fun getMatches(): List<MatchResultWithCharacters> {
        val matches = matchRepository.selectMatches()
        val characters = characterService.getCharacters(
            CharactersFilter(
                ids = matches.flatMap { listOf(it.challengerId, it.opponentId) }.toSet(),
                includeChallengers = true,
                includeOpponents = true
            )
        )
        return matchRepository.selectMatches().map { match ->
            val challenger = characters.find { it.characterId == match.challengerId }!! // TODO
            val opponent = characters.find { it.characterId == match.opponentId }!! // TODO

            MatchResultWithCharacters(
                challenger = challenger,
                challengerExperience = match.challengerExperience,
                opponent = opponent,
                opponentExperience = match.opponentExperience,
                match = match,
                rounds = matchRepository.selectRounds(match.id!!), // TODO
                currentAccountId = accountService.getCurrentAccountId()
            )
        }
    }

    @Transactional
    fun doMatch(
        rounds: Int,
        challengerId: CharacterId,
        opponentId: CharacterId
    ): MatchResultWithCharacters {
        return match(
            rounds = rounds,
            challenger = characterService.getCharacter(challengerId),
            opponent = characterService.getCharacter(opponentId)
        )
    }

    private fun match(
        rounds: Int,
        challenger: Character,
        opponent: Character
    ): MatchResultWithCharacters {
        var round = 0

        // TODO collect while condition is true
        val roundResults = (0 until rounds).mapNotNull {
            if (challenger.currentHealth > 0 && opponent.currentHealth > 0) {
                round(round++, challenger, opponent)
            } else null
        }.flatten()

        val matchOutcome = when {
            challenger.currentHealth <= 0 && opponent.currentHealth > 0 -> {
                logger.info { ("${opponent.name} is the victor in round $round!") }
                MatchOutcome.OpponentWon(
                    healthRemaining = opponent.currentHealth,
                    roundsWon = round
                )
            }
            opponent.currentHealth <= 0 && challenger.currentHealth > 0 -> {
                logger.info { "${challenger.name} is the victor in round $round!" }
                MatchOutcome.ChallengerWon(
                    healthRemaining = challenger.currentHealth,
                    roundsWon = round,
                    perfectVictory = challenger.currentHealth == challenger.health
                )
            }
            else -> {
                logger.info { "\nIt's a draw!" }
                MatchOutcome.Draw(
                    rounds = round,
                    reason = DrawReason.TIME_LIMIT
                )
            }
        }

        // Calculate experience using strategy pattern
        val (challengerExperience, opponentExperience) = experienceCalculationStrategy.calculateExperience(
            outcome = matchOutcome,
            challenger = challenger,
            opponent = opponent
        )

        val matchResult = matchRepository.insertMatch(
            MatchResult(
                challengerId = challenger.characterId,
                challengerExperience = challengerExperience,
                opponentId = opponent.characterId,
                opponentExperience = opponentExperience,
                matchOutcome = matchOutcome
            )
        )

        val rounds = roundResults.flatMap { roundResult ->
            matchRepository.insertRound(matchResult.id!!, roundResult)
        }

        updateCharacter(
            characterId = challenger.characterId,
            win = matchOutcome is MatchOutcome.ChallengerWon,
            loss = matchOutcome is MatchOutcome.OpponentWon,
            gainedExperience = challengerExperience
        )
        updateCharacter(
            characterId = opponent.characterId,
            win = matchOutcome is MatchOutcome.OpponentWon,
            loss = matchOutcome is MatchOutcome.ChallengerWon,
            gainedExperience = opponentExperience
        )

        return MatchResultWithCharacters(
            challenger = challenger,
            opponent = opponent,
            match = matchResult,
            rounds = rounds,
            challengerExperience = challengerExperience,
            opponentExperience = opponentExperience,
            currentAccountId = accountService.getCurrentAccountId()
        )
    }

    private fun round(round: Int, challenger: Character, opponent: Character): List<MatchRoundResult> {
        val challengerStartingState = challenger
        val opponentStartingState = opponent

        challenger.beforeRound()
        opponent.beforeRound()

        challenger.attack(opponent)
        opponent.attack(challenger)

        challenger.afterRound()
        opponent.afterRound()

        logger.info { "[Round $round] Challenger: $challengerStartingState -> $challenger" }
        logger.info { "[Round $round] Opponent: $challengerStartingState -> $opponent" }

        return listOf(
            MatchRoundResult(
                round = round,
                characterId = challenger.characterId,
                healthDelta = challengerStartingState.currentHealth - challenger.currentHealth,
                energyDelta = challengerStartingState.energy - challenger.energy
            ),
            MatchRoundResult(
                round = round,
                characterId = opponent.characterId,
                healthDelta = opponentStartingState.currentHealth - opponent.currentHealth,
                energyDelta = opponentStartingState.energy - opponent.energy
            )
        )
    }

    private fun updateCharacter(characterId: CharacterId, win: Boolean, loss: Boolean, gainedExperience: Int) {
        leaderboardService.updateLeaderboard(
            characterId = characterId,
            win = win,
            loss = loss
        )
        characterService.updateExperience(
            characterId = characterId,
            gainedExperience = gainedExperience
        )
    }

}


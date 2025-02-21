package com.motycka.edu.game.match

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.leaderboard.LeaderboardService
import com.motycka.edu.game.match.model.MatchResult
import com.motycka.edu.game.match.model.MatchResultWithCharacters
import com.motycka.edu.game.match.model.MatchRoundResult
import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.match.model.MatchOutcome
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterService: CharacterService,
    private val accountService: AccountService,
    private val leaderboardService: LeaderboardService
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
                challengerExperience = 100,
                opponent = opponent,
                opponentExperience = 100,
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
            if (challenger.getStats().health > 0 && opponent.getStats().health > 0) {
                round(round++, challenger, opponent)
            } else null
        }.flatten()

        val matchOutcome = when {
            challenger.getStats().health <= 0 && opponent.getStats().health > 0 -> {
                logger.info { ("${opponent.name} is the victor in round $round!") }
                MatchOutcome.OPPONENT_WON
            }
            opponent.getStats().health <= 0 && challenger.getStats().health > 0 -> {
                logger.info { "${challenger.name} is the victor in round $round!" }
                MatchOutcome.CHALLENGER_WON
            }
            else -> {
                logger.info { "\nIt's a draw!" }
                MatchOutcome.DRAW
            }
        }

        val challengerExperience = 100
        val opponentExperience = 100

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
            win = matchOutcome == MatchOutcome.CHALLENGER_WON,
            loss = matchOutcome == MatchOutcome.OPPONENT_WON,
            gainedExperience = challengerExperience
        )
        updateCharacter(
            characterId = opponent.characterId,
            win = matchOutcome == MatchOutcome.OPPONENT_WON,
            loss = matchOutcome == MatchOutcome.CHALLENGER_WON,
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
        val challengerStatsBefore = challenger.getStats()
        val opponentStatsBefore = opponent.getStats()

        challenger.beforeRound()
        opponent.beforeRound()


        challenger.attack(opponent)
        opponent.attack(challenger)

        // new
        challenger.afterRound()
        opponent.afterRound()

        val challengerStatsAfter = challenger.getStats()
        val opponentStatsAfter = opponent.getStats()

        logger.info { "[Round $round] Challenger: $challengerStatsBefore -> $challengerStatsAfter" }
        logger.info { "[Round $round] Opponent: $opponentStatsBefore -> $opponentStatsAfter" }

        return listOf(
            MatchRoundResult(
                round = round,
                characterId = challenger.characterId,
                healthDelta = challengerStatsBefore.health - challengerStatsAfter.health,
                staminaDelta = challengerStatsBefore.stamina - challengerStatsAfter.stamina,
                manaDelta = challengerStatsBefore.mana - challengerStatsAfter.mana
            ),
            MatchRoundResult(
                round = round,
                characterId = opponent.characterId,
                healthDelta = opponentStatsBefore.health - opponentStatsAfter.health,
                staminaDelta = opponentStatsBefore.stamina - opponentStatsAfter.stamina,
                manaDelta = opponentStatsBefore.mana - opponentStatsAfter.mana
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


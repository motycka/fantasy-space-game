package com.motycka.edu.game.match

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.match.model.Match
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val accountService: AccountService,
    private val characterService: com.motycka.edu.game.character.CharacterService
) {

    fun createMatch(rounds: Int, challengerId: Long, opponentId: Long): Match {

        require(challengerId != opponentId) { "Challenger and opponent must be different characters." }

        val challengerInfo = characterService.findById(challengerId)
            ?: error("Opponent not found.")
        val opponentInfo = characterService.findOpponentById(opponentId)
            ?: error("Opponent not found.")

        val currentAccountId = accountService.getCurrentAccountId()

        println("challenger: $challengerInfo")
        println("opponent: $opponentInfo")
        println("challengerInfo: $challengerInfo")

        var challengerHealth = challengerInfo.health
        var opponentHealth = opponentInfo.health
        val roundsList = mutableListOf<MatchRepository.Round>()
        var currentRound = 1

        while (currentRound <= rounds && challengerHealth > 0 && opponentHealth > 0) {
            println("ROUND $currentRound:")
            opponentHealth -= challengerInfo.attack
            roundsList.add(
                MatchRepository.Round(
                    round = currentRound,
                    characterId = opponentId,
                    healthDelta = -challengerInfo.attack,
                    staminaDelta = 0,
                    manaDelta = 0
                )
            )
            if (opponentHealth <= 0) {
                println("${opponentInfo.name} has been defeated in round $currentRound")
                break
            }
            challengerHealth -= opponentInfo.attack
            roundsList.add(
                MatchRepository.Round(
                    round = currentRound,
                    characterId = challengerId,
                    healthDelta = -opponentInfo.attack,
                    staminaDelta = 0,
                    manaDelta = 0
                )
            )
            if (challengerHealth <= 0) {
                println("${challengerInfo.name} has been defeated in round $currentRound")
                break
            }
            currentRound++
        }

        val outcome: String
        var challengerXP = 0
        var opponentXP = 0
        var challengerWinDelta = 0
        var challengerLossDelta = 0
        var challengerDrawDelta = 0
        var opponentWinDelta = 0
        var opponentLossDelta = 0
        var opponentDrawDelta = 0

        if (challengerHealth > 0 && opponentHealth <= 0) {
            outcome = "CHALLENGER_WON"
            challengerXP = 100
            opponentXP = 50
            challengerWinDelta = 1
            opponentLossDelta = 1
        } else if (opponentHealth > 0 && challengerHealth <= 0) {
            outcome = "OPPONENT_WON"
            challengerXP = 50
            opponentXP = 100
            challengerLossDelta = 1
            opponentWinDelta = 1
        } else {
            outcome = "DRAW"
            challengerXP = 50
            opponentXP = 50
            challengerDrawDelta = 1
            opponentDrawDelta = 1
        }

        val newChallengerExperience = challengerInfo.experience + challengerXP
        val newOpponentExperience = opponentInfo.experience + opponentXP

        matchRepository.updateCharacterExperience(challengerId, newChallengerExperience)
        matchRepository.updateCharacterExperience(opponentId, newOpponentExperience)

        matchRepository.updateLeaderboard(challengerId, challengerWinDelta, challengerLossDelta, challengerDrawDelta)
        matchRepository.updateLeaderboard(opponentId, opponentWinDelta, opponentLossDelta, opponentDrawDelta)

        val match = Match(
            id = 0,
            challengerId = challengerId,
            opponentId = opponentId,
            matchOutcome = outcome,
            challengerXp = challengerXP,
            opponentXp = opponentXP,
        )

        matchRepository.insertMatch(currentAccountId, match)
            ?: error("Failed to insert match")

        return match
    }

    fun getAllMatches(): List<Match> {
        return matchRepository.getAllMatches()
    }

    fun getRoundsForMatch(matchId: Long): List<MatchRepository.Round> {
        return matchRepository.getRoundsByMatchId(matchId)
    }
}
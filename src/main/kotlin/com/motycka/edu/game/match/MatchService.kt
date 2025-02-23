package com.motycka.edu.game.match

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.character.model.toGameCharacter
import com.motycka.edu.game.leaderboard.LeaderboardRepository
import com.motycka.edu.game.leaderboard.model.LeaderboardCreate
import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchCreate
import com.motycka.edu.game.match.model.MatchSelect
import com.motycka.edu.game.match.rest.MatchCreateRequest
import com.motycka.edu.game.rounds.RoundRepository
import com.motycka.edu.game.rounds.rest.RoundCreate
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class MatchService(
    private val characterService: CharacterService,
    private val matchRepository: MatchRepository,
    private val roundRepository: RoundRepository,
    private val leaderboardRepository: LeaderboardRepository,
) {
    fun getMatches(): List<Match> {
        logger.info { "Getting matches" }
        return matchRepository.getMatches()
    }

    @Async
    @Transactional
    fun simulateMatches(
        matchRequest: MatchCreateRequest,
    ): MatchSelect {
        val challenger = characterService
            .getCharacter(matchRequest.challengerId)
            .toGameCharacter()
        val opponent = characterService
            .getCharacter(matchRequest.opponentId)
            .toGameCharacter()

        val challengerInitialHealth = challenger.health
        val opponentInitialHealth = opponent.health
        val matchRecord = mutableListOf<RoundCreate>()

        var currentRound = 1
        while (
            challenger.health > 0 &&
            opponent.health > 0 &&
            currentRound <= matchRequest.rounds
        ) {
            println("\nROUND $currentRound:")
            challenger.attack(opponent)

            // only allow opponent attack if it's still alive
            if (opponent.health > 0) opponent.attack(challenger)

            val challengerStaminaDelta = if (challenger is Warrior) challenger.stamina else 0
            val challengerManaDelta = if (challenger is Sorcerer) challenger.mana else 0

            val opponentStaminaDelta = if (opponent is Warrior) opponent.stamina else 0
            val opponentManaDelta = if (opponent is Sorcerer) opponent.mana else 0

            val challengerRound = RoundCreate(
                matchId = -1,
                roundNumber = currentRound,
                characterId = challenger.id,
                healthDelta = challenger.health,
                staminaDelta = challengerStaminaDelta,
                manaDelta = challengerManaDelta,
            )
            val opponentRound = RoundCreate(
                matchId = -1,
                roundNumber = currentRound,
                characterId = opponent.id,
                healthDelta = opponent.health,
                staminaDelta = opponentStaminaDelta,
                manaDelta = opponentManaDelta,
            )
            matchRecord.add(challengerRound)
            matchRecord.add(opponentRound)

            currentRound++
        }

        val winner = when {
            challenger.health > opponent.health -> challenger
            opponent.health > challenger.health -> opponent
            else -> null
        }
        val looser = when (winner) {
            challenger -> opponent
            opponent -> challenger
            else -> null
        }

        val matchOutcome = when (winner) {
            challenger -> "CHALLENGER_WON"
            opponent -> "OPPONENT_WON"
            else -> "DRAW"
        }

        // Calculate XP gained:
        // XP is computed as the damage inflicted: (initial health - final health)
        val challengerXpGain = (opponentInitialHealth - opponent.health).coerceAtLeast(0)
        val opponentXpGain = (challengerInitialHealth - challenger.health).coerceAtLeast(0)

        val matchData = MatchCreate(
            matchOutcome = matchOutcome,
            challengerId = challenger.id,
            opponentId = opponent.id,
            challengerXp = challengerXpGain,
            opponentXp = opponentXpGain,
        )

        // Record match to match table
        val match = matchRepository.createMatch(matchData)

        // Record rounds to round table
        matchRecord.forEach {
            it.matchId = match.id
            println("Recording round ${it.roundNumber} of match ${it.matchId}")
            roundRepository.recordRound(it)
        }

        // Update character XP
        characterService.addExp(challenger.id, challengerXpGain)
        characterService.addExp(opponent.id, opponentXpGain)

        // Record score to leaderboard table
        leaderboardRepository.createLeaderboard(
            LeaderboardCreate(
                characterId = challenger.id,
                wins = winner == challenger,
                losses = looser == challenger,
                draws = matchOutcome == "DRAW",
            )
        )
        leaderboardRepository.createLeaderboard(
            LeaderboardCreate(
                characterId = opponent.id,
                wins = winner == opponent,
                losses = looser == opponent,
                draws = matchOutcome == "DRAW",
            )
        )

        return match
    }
}

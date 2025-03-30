package com.motycka.edu.game.account

import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchOutcome
import com.motycka.edu.game.match.rest.MatchRepository
import com.motycka.edu.game.match.rest.MatchRequest
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharacterRepository
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterRepository: CharacterRepository
) {

    fun createMatch(matchRequest: MatchRequest): Match {
        val challenger = characterRepository.findById(matchRequest.challengerId)
            .orElseThrow { RuntimeException("Challenger not found") }
        val opponent = characterRepository.findById(matchRequest.opponentId)
            .orElseThrow { RuntimeException("Opponent not found") }

        val match = Match(
            challenger = challenger,
            opponent = opponent,
            matchOutcome = determineOutcome(challenger, opponent)
        )

        return matchRepository.save(match)
    }

    fun getAllMatches(): List<Match> {
        return matchRepository.findAll()
    }

    private fun determineOutcome(challenger: Character, opponent: Character): MatchOutcome {
        return when {
            challenger.attackPower > opponent.attackPower -> MatchOutcome.CHALLENGER_WON
            challenger.attackPower < opponent.attackPower -> MatchOutcome.OPPONENT_WON
            else -> MatchOutcome.DRAW
        }
    }
}

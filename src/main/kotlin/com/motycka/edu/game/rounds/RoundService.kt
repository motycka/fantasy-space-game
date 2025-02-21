package com.motycka.edu.game.rounds

import com.motycka.edu.game.match.model.MatchId
import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.rounds.rest.RoundCreate
import org.springframework.stereotype.Service

@Service
class RoundService(
    private val roundRepository: RoundRepository,
) {
    fun getRoundsByMatchId(matchId: MatchId): List<Round> {
        return roundRepository.getRoundsByMatchId(matchId)
    }

    fun recordRound(roundRecord: RoundCreate) {
        roundRepository.recordRound(roundRecord)
    }
}
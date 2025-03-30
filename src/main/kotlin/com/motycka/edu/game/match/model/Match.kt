package com.motycka.edu.game.match.model

import com.motycka.edu.game.match.MatchRepository

data class Match(
    val id: Long = 0,
    val challengerId: Long,
    val opponentId: Long,
    val matchOutcome: String, // Internally "WIN", "LOSS", or "DRAW"
    val challengerXp: Int,
    val opponentXp: Int,
    val rounds: List<Round> = emptyList()
)

data class CreateMatchRequest(
    val rounds: Int,
    val challengerId: Long,
    val opponentId: Long
)


data class Round(
    val round: Int,
    val characterId: String,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
) {
    init {
        require(round > 0) { "Round number must be positive." }
        require(healthDelta <= 0) { "Health delta must be negative." }
    }
}





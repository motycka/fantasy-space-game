package com.motycka.edu.game.match.model

data class MatchUpdate (
    val matchId: MatchId,
    val matchOutcome: String,
    val challengerXp: Int,
    val opponentXp: Int,
)
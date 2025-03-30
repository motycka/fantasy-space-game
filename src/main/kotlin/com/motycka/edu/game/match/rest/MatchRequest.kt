package com.motycka.edu.game.match.rest

data class MatchRequest(
    val challengerId: Long,
    val opponentId: Long
)

package com.motycka.edu.game.match.rest

import com.motycka.edu.game.character.rest.CharacterId

data class MatchRequest(
    val rounds: Int,
    val challengerId: CharacterId,
    val opponentId: CharacterId
)


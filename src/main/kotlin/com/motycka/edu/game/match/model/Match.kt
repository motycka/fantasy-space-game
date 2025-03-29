package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.model.CharacterId

data class Match (
    val id: MatchId? = null,
    val challengerId: CharacterId,
    val opponentId: CharacterId,
    val matchOutcome: String,
    val challengerXp: Int,
    val opponentXp: Int
)
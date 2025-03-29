package com.motycka.edu.game.match.rest

import com.motycka.edu.game.character.model.CharacterId

data class MatchBattleRequest(
    val rounds: Int,
    val challengerId: CharacterId,
    val opponentId: CharacterId
)
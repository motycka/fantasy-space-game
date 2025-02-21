package com.motycka.edu.game.rounds.rest

import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.match.model.MatchId

data class RoundCreate (
    var matchId: MatchId,
    val roundNumber: Int,
    val characterId: CharacterId,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int,
)
package com.motycka.edu.game.match.rest

import com.motycka.edu.game.character.rest.CharacterId

data class MatchRoundResponse(
    val round: Int,
    val characterId: CharacterId,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)

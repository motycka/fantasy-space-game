package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.rest.CharacterId

data class MatchRoundResult(
    val id: RoundId? = null,
    val round: Int,
    val characterId: CharacterId,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)

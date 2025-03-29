package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.model.CharacterId

data class Round (
    val id: RoundId? = null,
    val matchId: MatchId? = null,
    val round: Int,
    val characterId: CharacterId,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)
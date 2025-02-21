package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.rest.CharacterId

data class MatchResult(
    val id: MatchId? = null,
    val challengerId: CharacterId,
    val challengerExperience: Int,
    val opponentId: CharacterId,
    val opponentExperience: Int,
    val matchOutcome: MatchOutcome,
)


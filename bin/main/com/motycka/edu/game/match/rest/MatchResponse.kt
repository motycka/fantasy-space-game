package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.model.MatchId
import com.motycka.edu.game.match.model.MatchOutcome

data class MatchResponse(
    val id: MatchId,
    val challenger: MatchCharacterResponse,
    val opponent: MatchCharacterResponse,
    val rounds: List<MatchRoundResponse>,
    val matchOutcome: MatchOutcome
)

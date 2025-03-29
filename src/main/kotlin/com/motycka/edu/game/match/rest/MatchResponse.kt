package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.model.MatchId
import com.motycka.edu.game.match.model.Round

data class MatchResponse (
    val id: MatchId,
    val challenger: CharacterMatchResponse,
    val opponent: CharacterMatchResponse,
    val rounds: List<Round>,
    val matchOutcome: String
)
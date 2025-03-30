package com.motycka.edu.game.match

data class GetMatchResponse(
    val id: String,
    val challenger: CharacterMatchInfo,
    val opponent: CharacterMatchInfo,
    val rounds: List<RoundResponse>,
    val matchOutcome: String  // Added match outcome field
)
package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchOutcome
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.rest.toCharacterResponse

data class MatchResponse(
    val id: Long,
    val challenger: CharacterResponse,
    val opponent: CharacterResponse,
    val matchOutcome: MatchOutcome
)

fun Match.toMatchResponse(): MatchResponse {
    return MatchResponse(
        id = this.id,
        challenger = this.challenger.toCharacterResponse(),
        opponent = this.opponent.toCharacterResponse(),
        matchOutcome = this.matchOutcome
    )
}

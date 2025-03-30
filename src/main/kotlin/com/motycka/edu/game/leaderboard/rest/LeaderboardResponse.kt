package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.model.LeaderboardEntry
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.rest.toCharacterResponse

data class LeaderboardResponse(
    val character: CharacterResponse,
    val wins: Int,
    val losses: Int,
    val draws: Int
)

fun LeaderboardEntry.toLeaderboardResponse(): LeaderboardResponse {
    return LeaderboardResponse(
        character = this.character.toCharacterResponse(),
        wins = this.wins,
        losses = this.losses,
        draws = this.draws
    )
}

package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.rest.CharacterResponse

data class CharacterLeaderboardResponse (
    val position: Int,
    val character: CharacterResponse,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
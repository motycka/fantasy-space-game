package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.rest.CharacterId

data class LeaderboardEntry(
    val position: Int,
    val characterId: CharacterId,
    val wins: Int,
    val losses: Int,
    val draws: Int
)

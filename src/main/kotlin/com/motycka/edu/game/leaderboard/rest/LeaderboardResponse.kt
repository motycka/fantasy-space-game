package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.character.model.Character

data class LeaderboardResponse(
    val position: Int,
    val character: Character,
    val wins: Int,
    val losses: Int,
    val draws: Int,
)
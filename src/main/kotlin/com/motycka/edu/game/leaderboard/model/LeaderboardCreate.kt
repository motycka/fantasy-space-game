package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.model.CharacterId

data class LeaderboardCreate(
    val characterId: CharacterId,
    val wins: Boolean?,
    val losses: Boolean?,
    val draws: Boolean?,
)
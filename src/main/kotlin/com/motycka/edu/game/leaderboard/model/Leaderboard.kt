package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.model.CharacterId

data class Leaderboard(
    val characterId: CharacterId,
    val wins: Int,
    val losses: Int,
    val draws: Int,
)
package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.rest.CharacterId

data class Leaderboard(
    val position: Int,
    val characterId: CharacterId,
    val wins: Int,
    val losses: Int,
    val draws: Int
)

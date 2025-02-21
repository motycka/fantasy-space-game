package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.model.Character

data class Leaderboard (
    val position: Position,
    val character: Character,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
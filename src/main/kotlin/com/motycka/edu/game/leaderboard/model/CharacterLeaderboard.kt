package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.rest.CharacterResponse

data class CharacterLeaderboard(
    val position: Int,
    val characterId: CharacterId,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
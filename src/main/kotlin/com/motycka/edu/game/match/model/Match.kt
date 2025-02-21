package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.model.CharacterId

data class Player(
    val id: CharacterId,
    val name: String,
    val characterClass: String,
    val level: String,
    val experienceTotal: Int,
    val experienceGained: Int,
    val isVictor: Boolean
)

data class Round(
    val round: Int,
    val characterId: CharacterId,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)

data class Match (
    val id: MatchId? = null,
    val challenger: Player,
    val opponent: Player,
    val rounds: List<Round>
)
package com.motycka.edu.game.match.rest

import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.model.CharacterLevel

data class CharacterMatchResponse(
    val id: CharacterId,
    val name: String,
    val characterClass: CharacterClass,
    val level: CharacterLevel,
    val experienceTotal: Int,
    val experienceGained: Int
)
package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.model.CharacterStats

data class CharacterLevelUpRequest(
    val name: String,
    val characterClass: CharacterClass,
    override val health: Int,
    override val attackPower: Int,
    override val defensePower: Int = 0, // Warrior
    override val stamina: Int = 0,
    override val healingPower: Int = 0, // Sorcerer
    override val mana: Int = 0,
): CharacterStats
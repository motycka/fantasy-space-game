package com.motycka.edu.game.character.rest

import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.CharacterStats

data class CharacterResponse (
    val id: CharacterId,
    val name: String,
    val characterClass: CharacterClass,
    val level: CharacterLevel,
    val experience: Int,
    override val health: Int,
    override val attackPower: Int,
    override val defensePower: Int, // Warrior
    override val stamina: Int,
    override val healingPower: Int, // Sorcerer
    override val mana: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
): CharacterStats
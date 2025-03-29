package com.motycka.edu.game.character.rest

import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterClass

fun Character.toCharacterResponse(accountId: AccountId) = CharacterResponse(
    id = requireNotNull(id) { "Character ID must not be null" },
    characterClass = characterClass,
    name = name,
    level = level,
    experience = experience,
    health = health,
    attackPower = attackPower,
    defensePower = defensePower, // Warrior
    stamina = stamina,
    healingPower = healingPower, // Sorcerer
    mana = mana,
    shouldLevelUp = shouldLevelUp,
    isOwner = (accountId == this.accountId)
)


fun CharacterCreationRequest.toCharacter() = Character(
    id = null,
    accountId = null,
    name = name,
    characterClass = characterClass,
    experience = 0,
    health = health,
    attackPower = attackPower,
    defensePower = defensePower, // Warrior
    stamina = stamina,
    healingPower = healingPower, // Sorcerer
    mana = mana
)

fun CharacterLevelUpRequest.toCharacter() = Character(
    id = null,
    accountId = null,
    name = name,
    characterClass = characterClass,
    experience = 0,
    health = health,
    attackPower = attackPower,
    defensePower = defensePower, // Warrior
    stamina = stamina,
    healingPower = healingPower, // Sorcerer
    mana = mana
)

package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.error.UnknownCharacterClassException
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.account.model.AccountId

fun List<Character>.toCharacterResponses(currentAccountId: AccountId) = map {
    it.toCharacterResponse(currentAccountId)
}

fun Character.toCharacterResponse(currentAccountId: AccountId): CharacterResponse {
    return when (this) {
        is Sorcerer -> toCharacterResponse(currentAccountId)
        is Warrior -> toCharacterResponse(currentAccountId)
        else -> {
            throw error("Unknown character class")
        }
    }
}

fun Character.isOwnedBy(currentAccountId: AccountId): Boolean {
    return this.accountId == currentAccountId
}

fun Sorcerer.toCharacterResponse(currentAccountId: AccountId) = CharacterResponse(
    id = requireNotNull(id) { "Character id must not be null." },
    name = name,
    health = health,
    attackPower = attackPower,
    stamina = null,
    defensePower = null,
    mana = mana,
    healingPower = healingPower,
    characterClass = CharacterClass.SORCERER,
    level = level,
    experience = experience,
    shouldLevelUp = getPoints() < level.points,
    isOwner = isOwnedBy(currentAccountId)
)

fun Warrior.toCharacterResponse(currentAccountId: AccountId) = CharacterResponse(
    id = requireNotNull(id) { "Character id must not be null." },
    name = name,
    health = health,
    attackPower = attackPower,
    stamina = stamina,
    defensePower = defensePower,
    mana = null,
    healingPower = null,
    characterClass = CharacterClass.WARRIOR,
    level = level,
    experience = experience,
    shouldLevelUp = getPoints() < level.points,
    isOwner = isOwnedBy(currentAccountId)
)

fun CharacterCreateRequest.toCharacter(accountId: AccountId): Character {
    return when (characterClass) {
        CharacterClass.WARRIOR -> toWarrior(accountId)
        CharacterClass.SORCERER -> toSorcerer(accountId)
    }
}

private fun CharacterCreateRequest.toSorcerer(accountId: AccountId) = Sorcerer(
    id = null,
    accountId = accountId,
    name = name,
    health = health,
    attackPower = attackPower,
    mana = requireNotNull(mana) { "Mana must not be null." },
    healingPower = requireNotNull(healingPower) { "Mana must not be null." },
    level = CharacterLevel.LEVEL_1,
    experience = 0
)

private fun CharacterCreateRequest.toWarrior(accountId: AccountId) = Warrior(
    id = null,
    accountId = accountId,
    name = name,
    health = health,
    attackPower = attackPower,
    stamina = requireNotNull(stamina) { "Stamina must not be null." },
    defensePower = requireNotNull(defensePower) { "Defense power must not be null." },
    level = CharacterLevel.LEVEL_1,
    experience = 0
)

fun CharacterUpdateRequest.toCharacter(id: CharacterId, existing: Character): Character {
    return when (existing) {
        is Warrior -> toWarrior(id, existing)
        is Sorcerer -> toSorcerer(id, existing)
        else -> throw UnknownCharacterClassException()
    }
}

fun CharacterUpdateRequest.toSorcerer(id: CharacterId, existing: Character) = Sorcerer(
    id = id,
    accountId = existing.accountId,
    name = name,
    health = health,
    attackPower = attackPower,
    mana = requireNotNull(mana) { "Mana must not be null." },
    healingPower = requireNotNull(healingPower) { "Mana must not be null." },
    level = existing.level,
    experience = existing.experience
)

fun CharacterUpdateRequest.toWarrior(id: CharacterId, existing: Character) = Warrior(
    id = id,
    accountId = existing.accountId,
    name = name,
    health = health,
    attackPower = attackPower,
    stamina = requireNotNull(stamina) { "Stamina must not be null." },
    defensePower = requireNotNull(defensePower) { "Defense power must not be null." },
    level = existing.level,
    experience = existing.experience
)

fun Character.getClass(): CharacterClass {
    return when (this) {
        is Sorcerer -> CharacterClass.SORCERER
        is Warrior -> CharacterClass.WARRIOR
        else -> throw UnknownCharacterClassException()
    }
}

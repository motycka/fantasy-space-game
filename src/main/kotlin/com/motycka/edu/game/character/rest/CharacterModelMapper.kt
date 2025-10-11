package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.error.UnknownCharacterClassException
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.canLevelUp
import com.motycka.edu.game.character.shouldBeLevel

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
    attack = attack,
    stamina = null,
    defense = null,
    mana = mana,
    healing = healing,
    characterClass = CharacterClass.SORCERER,
    level = level,
    experience = experience,
    shouldLevelUp = canLevelUp(),
    isOwner = isOwnedBy(currentAccountId)
)

fun Warrior.toCharacterResponse(currentAccountId: AccountId) = CharacterResponse(
    id = requireNotNull(id) { "Character id must not be null." },
    name = name,
    health = health,
    attack = attack,
    stamina = stamina,
    defense = defense,
    mana = null,
    healing = null,
    characterClass = CharacterClass.WARRIOR,
    level = level,
    experience = experience,
    shouldLevelUp = canLevelUp(),
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
    attack = attack,
    mana = requireNotNull(mana) { "Mana must not be null." },
    healing = requireNotNull(healing) { "Mana must not be null." },
    level = CharacterLevel.LEVEL_1,
    experience = 0
)

private fun CharacterCreateRequest.toWarrior(accountId: AccountId) = Warrior(
    id = null,
    accountId = accountId,
    name = name,
    health = health,
    attack = attack,
    stamina = requireNotNull(stamina) { "Stamina must not be null." },
    defense = requireNotNull(defense) { "Defense power must not be null." },
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
    name = existing.name,
    health = health,
    attack = attack,
    mana = requireNotNull(mana) { "Mana must not be null." },
    healing = requireNotNull(healing) { "Healing power must not be null." },
    level = existing.shouldBeLevel(),
    experience = existing.experience
)

fun CharacterUpdateRequest.toWarrior(id: CharacterId, existing: Character) = Warrior(
    id = id,
    accountId = existing.accountId,
    name = existing.name,
    health = health,
    attack = attack,
    stamina = requireNotNull(stamina) { "Stamina must not be null." },
    defense = requireNotNull(defense) { "Defense power must not be null." },
    level = existing.shouldBeLevel(),
    experience = existing.experience
)

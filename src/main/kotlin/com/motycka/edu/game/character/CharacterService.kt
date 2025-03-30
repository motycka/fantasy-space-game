package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.account.AccountService
import org.springframework.stereotype.Service

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {

    // fun createCharacter(character: Character): Character {
    //     return characterRepository.insertCharacters(accountId = accountService.getCurrentAccountId(), character = character)
    //         ?: error("Character could not be created.")
    // }

    fun getCharacters(filter: CharactersFilter): List<Character> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectWithFilter(accountId, filter)
    }

    fun getCharacter(id: Long): Character {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectCharacter(accountId, id)
            ?: error("Character not found.")
    }

//            name = request.name,
//            health = request.health,
//            attackPower = request.attackPower,
//            stamina = request.stamina,
//            defensePower = request.defensePower,
//            mana = request.mana,
//            healingPower = request.healingPower,
//            characterClass = request.characterClass

    fun createCharacter(
        name: String,
        health: Int,
        attackPower: Int,
        stamina: Int?,
        defensePower: Int?,
        mana: Int?,
        healingPower: Int?,
        characterClass: String
    ): Character {
        val accountId = accountService.getCurrentAccountId()
        val character = Character(
            id = 0,
            accountId = accountId,
            name = name,
            characterClass = characterClass,
            health = health,
            attack = attackPower,
            defense = defensePower ?: 0,
            stamina = stamina ?: 0,
            healing = healingPower ?: 0,
            mana = mana ?: 0,
            experience = 0
        )
        return characterRepository.insertCharacters(accountId, character)
            ?: error("Character could not be created.")
    }

    fun updateCharacter(
        id: Long,
        name: String,
        health: Int,
        attackPower: Int,
        stamina: Int?,
        defensePower: Int?,
        mana: Int?,
        healingPower: Int?,
        characterClass: String
    ): Character {
        val accountId = accountService.getCurrentAccountId()
        val character = Character(
            id = id,
            accountId = accountId,
            name = name,
            characterClass = characterClass,
            health = health,
            attack = attackPower,
            defense = defensePower ?: 0,
            stamina = stamina ?: 0,
            healing = healingPower ?: 0,
            mana = mana ?: 0,
            experience = 0
        )
        return characterRepository.updateCharacter(accountId, character)
            ?: error("Character could not be updated.")
    }


    fun getChallengers(): List<Character> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectChallengers(accountId)
    }

    fun getOpponents(): List<Character> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectOpponents(accountId)
    }

    fun getCharacterById(id: Long): Character {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectCharacter(accountId, id)
            ?: error("Character not found.")
    }

    fun findById(id: Long): Character? {
        val accountId = accountService.getCurrentAccountId()
        println("accountId: $accountId, id: $id")
        return characterRepository.selectCharacter(accountId, id)
    }

    fun findOpponentById(id: Long): Character? {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectOpponentCharacter(accountId, id)
    }

    fun isOwner(characterId: Long, accountId: Long): Boolean {
        return characterRepository.isOwner(characterId, accountId)
    }

    fun addExperience(characterId: Long, xp: Int) {
        val character = getCharacter(characterId)
        val updatedCharacter = character.copy(experience = character.experience + xp)
        characterRepository.updateCharacter(character.accountId, updatedCharacter)
    }


}

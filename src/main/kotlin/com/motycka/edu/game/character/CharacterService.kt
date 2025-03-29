package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.model.CharacterLevel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * This is example of service implementation with repository dependency injection.
 */
@Service
class CharacterService(
    private val accountService: AccountService,
    private val characterRepository: CharacterRepository
) {

    fun getAllCharacters(): List<Character> {
        logger.debug { "Getting all characters" }
        return characterRepository.selectAll() ?: emptyList()
    }

    fun getCharacterById(id: CharacterId): Character {
        logger.debug { "Getting character by id $id" }
        return characterRepository.selectById(id) ?: error("No character found for id $id.")
    }

    fun getCharactersByAccountId(accountId: AccountId): List<Character> {
        logger.debug { "Getting characters by account id $accountId" }
        return characterRepository.selectByAccountId(accountId) ?: emptyList()
    }

    fun getOpponentsByAccountId(accountId: AccountId): List<Character> {
        logger.debug { "Getting opponents by account id $accountId" }
        return characterRepository.selectOpponentsByAccountId(accountId)
            ?: error("No opponents found for account id $accountId.")
    }

    fun createCharacter(character: Character): Character {
        val currentAccountId = accountService.getCurrentAccountId()
        val characterWithAccountId = character.copy(accountId = currentAccountId)
        logger.debug { "Creating new character: $characterWithAccountId" }
        return characterRepository.insertCharacter(characterWithAccountId) ?: error(CREATE_ERROR)
    }

    fun levelUpCharacter(character: Character): Character {
        require(character.id != null) { "Character must have an id." }

        val currentAccountId = accountService.getCurrentAccountId()
        val existingCharacter = getCharacterById(character.id)

        require(existingCharacter.accountId == currentAccountId) { "Character does not belong to the current user." }
        require(character.accountId == currentAccountId) { "Character does not belong to the current user." }

        require(existingCharacter.characterClass == character.characterClass) { "Character cannot change class." }
        require(existingCharacter.name == character.name) { "Character name cannot be changed." }

        require(existingCharacter.shouldLevelUp ) {
            "Character does not have enough points to level up."
        }

        require(CharacterLevel.fromTotalPoints(character.totalStats) == existingCharacter.level.nextLevel()){
            "Character can only level up to the next level."
        }

        logger.debug { "Leveling up character: $character" }
        return characterRepository.levelUpCharacter(character) ?: error(UPDATE_ERROR)
    }

    fun addExperience(character: Character, experience: Int): Character {
        require(character.id != null) { "Character must have an id." }
        require(experience > 0) { "Experience must be positive." }
        logger.debug { "Adding experience to character: $character" }
        return characterRepository.updateExperience(character, experience) ?: error(UPDATE_ERROR)
    }


    companion object {
        const val CREATE_ERROR = "Account could not be created."
        const val UPDATE_ERROR = "Account could not be updated."
    }
}

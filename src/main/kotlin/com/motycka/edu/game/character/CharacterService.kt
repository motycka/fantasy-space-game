package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.*
import com.motycka.edu.game.character.rest.CharacterCreateRequest
import com.motycka.edu.game.character.rest.CharacterUpdateRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CharacterService(
    private val accountService: AccountService,
    private val characterRepository: CharacterRepository,
) {
    fun getCharacters(
        characterClass: String? = null,
        name: String? = null,
    ): List<Character> {
        logger.debug { "Getting characters" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.getCharacters(accId, characterClass, name)
    }

    fun getChallengers(
        characterClass: String? = null,
        name: String? = null,
    ): List<Character> {
        logger.debug { "Getting challengers" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.getMatchCharacters(accId, challenger = true)
    }

    fun getOpponents(
        characterClass: String? = null,
        name: String? = null,
    ): List<Character> {
        logger.debug { "Getting opponents" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.getMatchCharacters(accId, challenger = false)
    }

    fun createCharacter(character: CharacterCreateRequest): Character {
        logger.debug { "Creating character" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.createCharacter(accId, character)
    }

    fun updateCharacter(id: CharacterId, character: CharacterUpdateRequest): Character {
        logger.debug { "Updating character" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.updateCharacter(accId, id, character)
    }

    fun addExp(id: CharacterId, exp: Int): Character {
        logger.debug { "Updating character experience" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.addExp(accId, id, exp)
    }

    fun getCharacter(id: CharacterId): Character {
        logger.debug { "Getting character" }
        val accId = accountService.getCurrentAccountId()
        return characterRepository.getCharacter(accId, id)
    }
}

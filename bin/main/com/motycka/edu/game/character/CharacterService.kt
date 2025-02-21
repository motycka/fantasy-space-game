package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.error.NotFoundException
import com.motycka.edu.game.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService,
) {

    fun createCharacter(character: Character): Character {
        return characterRepository.insertCharacters(
            accountId = accountService.getCurrentAccountId(),
            character = character
        ) ?: error(CREATE_ERROR)
    }

    fun getCharacters(filter: CharactersFilter): List<Character> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.selectWithFilter(accountId, filter)
    }

    fun getCharacter(characterId: CharacterId): Character {
        return getCharacters(
            CharactersFilter(
                ids = setOf(characterId),
                includeChallengers = true,
                includeOpponents = true
            )
        ).firstOrNull() ?: throw NotFoundException()
    }

    fun updateCharacter(character: Character): Character {
        return characterRepository.updateCharacter(character) ?: error(UPDATE_ERROR)
    }

    fun updateExperience(characterId: CharacterId, gainedExperience: Int): Character? {
        return characterRepository.updateExperience(characterId, gainedExperience)
    }

    companion object {
        const val CREATE_ERROR = "Character could not be created."
        const val UPDATE_ERROR = "Character could not be updated."
    }

}

package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.character.rest.CharacterCreateRequest
import com.motycka.edu.game.character.rest.CharacterUpdateRequest
import com.motycka.edu.game.character.rest.toCharacter
import com.motycka.edu.game.character.rest.toCharacterResponse
import com.motycka.edu.game.character.rest.toCharacterResponses
import com.motycka.edu.game.account.AccountService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @PostMapping
    fun postCharacter(
        @RequestBody character: CharacterCreateRequest
    ): CharacterResponse {
        val accountId = accountService.getCurrentAccountId()
        return characterService.createCharacter(
            character = character.toCharacter(
                accountId = accountId
            )
        ).toCharacterResponse(accountId)
    }

    @GetMapping
    fun getCharacters(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getCharacters(
            filter = CharactersFilter.DEFAULT
        ).toCharacterResponses(accountId)
    }

    @GetMapping("/challengers")
    fun getChallengers(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getCharacters(
            filter = CharactersFilter.CHALLENGERS
        ).toCharacterResponses(accountId)
    }

    @GetMapping("/opponents")
    fun getOpponents(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getCharacters(
            filter = CharactersFilter.OPPONENTS
        ).toCharacterResponses(accountId)
    }

    @GetMapping("/{$CHARACTER_ID}")
    fun getCharacter(
        @PathVariable(CHARACTER_ID) characterId: CharacterId
    ): CharacterResponse {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getCharacter(
            characterId = characterId
        ).toCharacterResponse(accountId)
    }

    @PutMapping("/{$CHARACTER_ID}")
    fun putCharacter(
        @PathVariable(CHARACTER_ID) characterId: CharacterId,
        @RequestBody character: CharacterUpdateRequest
    ): CharacterResponse {
        val existing = characterService.getCharacter(characterId)
        return characterService.updateCharacter(
            character = character.toCharacter(
                id = characterId,
                existing = existing
            )
        ).toCharacterResponse(
            currentAccountId = requireNotNull(existing.accountId)
        )
    }

    companion object {
        const val CHARACTER_ID = "characterId"
    }

}

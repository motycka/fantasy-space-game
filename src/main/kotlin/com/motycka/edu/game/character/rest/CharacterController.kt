package com.motycka.edu.game.character.rest

import com.motycka.edu.game.account.service.AccountService
import com.motycka.edu.game.character.service.CharacterService
import com.motycka.edu.game.character.rest.CharacterCreateRequest
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.rest.CharactersFilter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postCharacter(@RequestBody characterRequest: CharacterCreateRequest): CharacterResponse {
        val accountId = accountService.getCurrentAccountId() ?: throw IllegalStateException("Account ID not found")
        return characterService.createCharacter(
            characterRequest.toCharacter().copy(accountId = accountId)
        ).toCharacterResponse()
    }

    @GetMapping
    fun getCharacters(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId() ?: throw IllegalStateException("Account ID not found")
        return characterService.getCharacters(CharactersFilter.DEFAULT).map { it.toCharacterResponse() }
    }
}

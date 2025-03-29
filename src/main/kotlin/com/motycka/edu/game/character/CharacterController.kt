package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.rest.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val accountService: AccountService,
    private val characterService: CharacterService
) {

    @GetMapping
    fun getCharacters(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getAllCharacters().map { it.toCharacterResponse(accountId) }
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: CharacterId): CharacterResponse {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getCharacterById(id).toCharacterResponse(accountId)
    }

    @GetMapping("/challengers")
    fun getChallengers(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getCharactersByAccountId(accountId).map { it.toCharacterResponse(accountId) }
    }

    @GetMapping("/opponents")
    fun getOpponents(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        return characterService.getOpponentsByAccountId(accountId).map { it.toCharacterResponse(accountId) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postCharacter(
        @RequestBody character: CharacterCreationRequest
    ): CharacterResponse {
        val accountId = accountService.getCurrentAccountId()
        return characterService.createCharacter(
            character = character.toCharacter().copy(accountId = accountId)
        ).toCharacterResponse(accountId)
    }

    @PutMapping("/{id}")
    fun characterLevelUp(
        @PathVariable id: CharacterId,
        @RequestBody character: CharacterLevelUpRequest
    ): CharacterResponse {
        val accountId = accountService.getCurrentAccountId()
        return characterService.levelUpCharacter(
            character = character.toCharacter().copy(
                id = id,
                accountId = accountId
            ),
        ).toCharacterResponse(accountId)
    }

}

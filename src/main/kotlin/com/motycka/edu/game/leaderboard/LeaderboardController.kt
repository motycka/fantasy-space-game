package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.AccountResponse
import com.motycka.edu.game.account.rest.toAccount
import com.motycka.edu.game.account.rest.toAccountResponse
import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.rest.toCharacterResponse
import com.motycka.edu.game.leaderboard.model.CharacterLeaderboard
import com.motycka.edu.game.leaderboard.rest.CharacterLeaderboardResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * This is example of a controller class that handles HTTP requests for the account resource with service dependency injection.
 */
@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @GetMapping
    fun getLeaderboard(
        @RequestParam("class", required = false)
        characterClass: String?
    ): List<CharacterLeaderboardResponse> {
        var leaderboard: List<CharacterLeaderboard> = emptyList()
        if (characterClass != null) {
            leaderboard = leaderboardService.getLeaderboardByClass(characterClass)
        } else {
            leaderboard = leaderboardService.getLeaderboard()
        }
        val characters = characterService.getAllCharacters()
        return leaderboard.map { leaderboardT ->
            val character = characters.find { it.id == leaderboardT.characterId }
                ?.toCharacterResponse(
                    accountService.getCurrentAccountId()
                )
                ?: error("Character not found")
            CharacterLeaderboardResponse(
                position = leaderboardT.position,
                character = character,
                wins = leaderboardT.wins,
                losses = leaderboardT.losses,
                draws = leaderboardT.draws
            )
        }
    }
}

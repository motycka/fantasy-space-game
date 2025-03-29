package com.motycka.edu.game.match

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.rest.*
import com.motycka.edu.game.match.rest.MatchBattleRequest
import com.motycka.edu.game.match.rest.MatchResponse
import com.motycka.edu.game.match.rest.toCharacterMatchResponse
import com.motycka.edu.game.match.rest.toMatchResponse
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
@RequestMapping("/api/matches")
class MatchController(
    private val accountService: AccountService,
    private val characterService: CharacterService,
    private val matchService: MatchService

) {

    @GetMapping
    fun getMatches(): List<MatchResponse> {
        val characters = characterService.getAllCharacters()
        val matches = matchService.getAllMatchesAndRounds()
            .map { matchWithRounds ->
                val match = matchWithRounds.match
                val challenger = characters.find { it.id == match.challengerId }
                    ?.toCharacterMatchResponse(match.challengerXp)
                    ?: error("Character not found")
                val opponent = characters.find { it.id == match.opponentId }
                    ?.toCharacterMatchResponse(match.opponentXp)
                    ?: error("Character not found")

                matchWithRounds.toMatchResponse(
                    challenger = challenger,
                    opponent = opponent,
                    matchOutcome = match.matchOutcome
                )
            }

        return matches
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postMatch(
        @RequestBody matchRequest: MatchBattleRequest
    ): MatchResponse {

        // check if the challenger belongs to the current user in service
        val matchWithRounds = matchService.createMatchAndRounds(
            rounds = matchRequest.rounds,
            challengerId = matchRequest.challengerId,
            opponentId = matchRequest.opponentId
        )

        val challenger = characterService.getCharacterById(matchRequest.challengerId)
        val challengerResponse = challenger.toCharacterMatchResponse(matchWithRounds.match.challengerXp)

        val opponent = characterService.getCharacterById(matchRequest.opponentId)
        val opponentResponse = opponent.toCharacterMatchResponse(matchWithRounds.match.opponentXp)

        characterService.addExperience(
            character = challenger,
            experience = matchWithRounds.match.challengerXp
        )

        characterService.addExperience(
            character = opponent,
            experience = matchWithRounds.match.opponentXp
        )

        return matchWithRounds.toMatchResponse(
            challenger = challengerResponse,
            opponent = opponentResponse,
            matchOutcome = matchWithRounds.match.matchOutcome
        )
    }
}

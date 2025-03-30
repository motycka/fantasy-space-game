package com.motycka.edu.game.match

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.match.model.Match
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.motycka.edu.game.character.model.Character


@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val matchService: MatchService,
    private val accountService: AccountService,
    private val characterService: com.motycka.edu.game.character.CharacterService
) {

    @PostMapping
    fun createMatch(@RequestBody matchRequest: MatchRequest): ResponseEntity<Match> {
        val match = matchService.createMatch(
            rounds = matchRequest.rounds,
            challengerId = matchRequest.challengerId,
            opponentId = matchRequest.opponentId
        )
        return ResponseEntity.ok(match)

    }

    @GetMapping
    fun getMatches(): ResponseEntity<List<GetMatchResponse>> {
        val matches = matchService.getAllMatches()
        val responses = matches.map { match ->
            val challengerCharacter: Character = characterService.getCharacterById(match.challengerId)
            val opponentCharacter: Character = characterService.findOpponentById(match.opponentId)
                ?: error("Opponent not found for match id ${match.id}")

            val challengerInfo = CharacterMatchInfo(
                id = challengerCharacter.id.toString(),
                name = challengerCharacter.name,
                characterClass = challengerCharacter.characterClass,
                level = (challengerCharacter.experience / 400).toString(),
                experienceTotal = challengerCharacter.experience,
                experienceGained = match.challengerXp,
                isVictor = match.challengerXp > match.opponentXp
            )
            val opponentInfo = CharacterMatchInfo(
                id = opponentCharacter.id.toString(),
                name = opponentCharacter.name,
                characterClass = opponentCharacter.characterClass,
                level = (opponentCharacter.experience / 400).toString(),
                experienceTotal = opponentCharacter.experience,
                experienceGained = match.opponentXp,
                isVictor = match.opponentXp > match.challengerXp
            )

            val rounds = matchService.getRoundsForMatch(match.id).map { r ->
                RoundResponse(
                    round = r.round,
                    characterId = r.characterId.toString(),
                    healthDelta = r.healthDelta,
                    staminaDelta = r.staminaDelta,
                    manaDelta = r.manaDelta
                )
            }

            GetMatchResponse(
                id = match.id.toString(),
                challenger = challengerInfo,
                opponent = opponentInfo,
                rounds = rounds,
                matchOutcome = match.matchOutcome
            )
        }
        return ResponseEntity.ok(responses)
    }
}
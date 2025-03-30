package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.account.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class CharacterResponse(
    val id: String,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: String,
    val level: String,
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)

data class CreateCharacterRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: String
)


data class updateCharacterRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?
)



data class CreateMatchRequest(
    val rounds: Int,
    val challengerId: Long,
    val opponentId: Long
)


@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @GetMapping
    fun getCharacters(
        @RequestParam(value = "class", required = false) characterClass: String?,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<List<CharacterResponse>> {

        println("CharacterClass: $characterClass, Name: $name")

        val accountId = accountService.getCurrentAccountId()
        val filter = CharactersFilter(
            characterClass = characterClass?.uppercase(),
            name = name
        )
        val characters = characterService.getCharacters(filter)
        val responses = characters.map { character ->

            val level = (character.experience / 400).toString()
            val shouldLevelUp = character.experience % 400 == 0


            CharacterResponse(
                id = character.id.toString(),
                name = character.name,
                health = character.health,
                attackPower = character.attack,
                stamina = character.stamina,
                defensePower = character.defense,
                mana = character.mana,
                healingPower = character.healing,
                characterClass = character.characterClass,
                level = level,
                experience = character.experience,
                shouldLevelUp = shouldLevelUp,
                isOwner = character.accountId == accountId
            )
        }
        return ResponseEntity.ok(responses)
    }


    @PostMapping
    fun createCharacter(@RequestBody request: CreateCharacterRequest): ResponseEntity<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        val character = characterService.createCharacter(
            name = request.name,
            health = request.health,
            attackPower = request.attackPower,
            stamina = request.stamina,
            defensePower = request.defensePower,
            mana = request.mana,
            healingPower = request.healingPower,
            characterClass = request.characterClass
        )

        return ResponseEntity.ok(
            CharacterResponse(
                id = character.id.toString(),
                name = character.name,
                health = character.health,
                attackPower = character.attack,
                stamina = character.stamina,
                defensePower = character.defense,
                mana = character.mana,
                healingPower = character.healing,
                characterClass = character.characterClass,
                level = "0",
                experience = character.experience,
                shouldLevelUp = false,
                isOwner = character.accountId == accountId
            )
        )
    }
}


@RestController
@RequestMapping("/api/characters/{id}")
class CharacterDetailController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @GetMapping
    fun getCharacter(@PathVariable("id") characterId: Long): ResponseEntity<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        val character = characterService.getCharacter(characterId)
        val level = (character.experience / 400).toString()
        val shouldLevelUp = character.experience % 400 == 0

        return ResponseEntity.ok(
            CharacterResponse(
                id = character.id.toString(),
                name = character.name,
                health = character.health,
                attackPower = character.attack,
                stamina = character.stamina,
                defensePower = character.defense,
                mana = character.mana,
                healingPower = character.healing,
                characterClass = character.characterClass,
                level = level,
                experience = character.experience,
                shouldLevelUp = shouldLevelUp,
                isOwner = character.accountId == accountId

            )
        )
    }


    @PutMapping
    fun updateCharacter(
        @PathVariable id: Long,
        @RequestBody request: updateCharacterRequest
    ): ResponseEntity<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        val character = characterService.updateCharacter(
            id = id,
            name = request.name,
            health = request.health,
            attackPower = request.attackPower,
            stamina = request.stamina,
            defensePower = request.defensePower,
            mana = request.mana,
            healingPower = request.healingPower,
            characterClass = ""

        )

        val level = (character.experience / 400).toString()
        val shouldLevelUp = character.experience % 400 == 0

        return ResponseEntity.ok(
            CharacterResponse(
                id = character.id.toString(),
                name = character.name,
                health = character.health,
                attackPower = character.attack,
                stamina = character.stamina,
                defensePower = character.defense,
                mana = character.mana,
                healingPower = character.healing,
                characterClass = character.characterClass,
                level = level,
                experience = character.experience,
                shouldLevelUp = shouldLevelUp,
                isOwner = character.accountId == accountId
            )
        )
    }
}

//Retrieves all challengers (characters owned by the current user).
@RestController
@RequestMapping("/api/characters/challengers")
class ChallengersController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @GetMapping
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        val accountId = accountService.getCurrentAccountId()
        val characters = characterService.getChallengers()
        val responses = characters.map { character ->

            val level = (character.experience / 400).toString()
            val shouldLevelUp = character.experience % 400 == 0

            CharacterResponse(
                id = character.id.toString(),
                name = character.name,
                health = character.health,
                attackPower = character.attack,
                stamina = character.stamina,
                defensePower = character.defense,
                mana = character.mana,
                healingPower = character.healing,
                characterClass = character.characterClass,
                level = level,
                experience = character.experience,
                shouldLevelUp = shouldLevelUp,
                isOwner = character.accountId == accountId
            )
        }
        return ResponseEntity.ok(responses)
    }
}


//Retrieves all opponents (characters not owned by the current user).
///api/characters/opponents

@RestController
@RequestMapping("/api/characters/opponents")
class OpponentsController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @GetMapping
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        val accountId = accountService.getCurrentAccountId()
        val characters = characterService.getOpponents()
        val responses = characters.map { character ->

            val level = (character.experience / 400).toString()
            val shouldLevelUp = character.experience % 400 == 0

            CharacterResponse(
                id = character.id.toString(),
                name = character.name,
                health = character.health,
                attackPower = character.attack,
                stamina = character.stamina,
                defensePower = character.defense,
                mana = character.mana,
                healingPower = character.healing,
                characterClass = character.characterClass,
                level = level,
                experience = character.experience,
                shouldLevelUp = shouldLevelUp,
                isOwner = character.accountId == accountId
            )
        }
        return ResponseEntity.ok(responses)
    }

}


//
//// Creates a new match.
//// /api/matches
////Accepts body:
////{
////    "rounds": 10,
////    "challengerId": 1,
////    "opponentId": 2
////}
//@RestController
//@RequestMapping("/api/matches")
//class MatchController(
//    private val matchService: MatchService
//) {
//
//    @PostMapping
//    fun createMatch(@RequestBody request: CreateMatchRequest): ResponseEntity<MatchResponse> {
//        val match = matchService.createMatch(
//            rounds = request.rounds,
//            challengerId = request.challengerId,
//            opponentId = request.opponentId
//        )
//
//        return ResponseEntity.ok(
//            MatchResponse(
//                id = match.id.toString(),
//                rounds = match.rounds,
//                challengerId = match.challengerId,
//                opponentId = match.opponentId,
//                winnerId = match.winnerId,
//                isDraw = match.isDraw
//            )
//        )
//    }
//}
//

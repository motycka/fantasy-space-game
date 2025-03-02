package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharacterCreateRequest
import com.motycka.edu.game.character.rest.CharacterUpdateRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val characterService: CharacterService
) {
    @GetMapping
    fun getCharacters(
        @RequestParam(required = false) characterClass: String? = null,
        @RequestParam(required = false) name: String? = null,
    ): List<Character> {
       return characterService.getCharacters()
    }

    @PostMapping
    fun createCharacter(
        @RequestBody character: CharacterCreateRequest
    ): Character {
        return characterService.createCharacter(character)
    }

    @PutMapping("/{id}")
    fun updateCharacter(
        @PathVariable id: String,
        @RequestBody character: CharacterUpdateRequest,
    ): Character {
        return characterService.updateCharacter(id.toLong(), character)
    }

    @GetMapping("/{id}")
    fun getCharacter(
        @PathVariable id: String
    ): Character {
       return characterService.getCharacter(id.toLong())
    }

    @GetMapping("/challengers")
    fun getChallengers(): List<Character> {
       return characterService.getChallengers()
    }

    @GetMapping("/opponents")
    fun getOpponents(): List<Character> {
       return characterService.getOpponents()
    }
}
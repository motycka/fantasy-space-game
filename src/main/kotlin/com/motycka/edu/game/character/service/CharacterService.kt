package com.motycka.edu.game.character.service

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharactersFilter
import org.springframework.stereotype.Service

@Service
class CharacterService {

    fun createCharacter(character: Character): Character {
        return character // Replace with database save logic
    }

    fun getCharacters(filter: CharactersFilter): List<Character> {
        return listOf() // Replace with actual logic to fetch characters
    }
}

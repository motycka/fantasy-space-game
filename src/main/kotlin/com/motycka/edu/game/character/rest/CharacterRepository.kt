package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.Character
import org.springframework.data.jpa.repository.JpaRepository

interface CharacterRepository : JpaRepository<Character, Long>

package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.model.LeaderboardEntry
import com.motycka.edu.game.character.model.CharacterClass
import org.springframework.data.jpa.repository.JpaRepository

interface LeaderboardRepository : JpaRepository<LeaderboardEntry, Long> {
    fun findByCharacterCharacterClass(characterClass: CharacterClass): List<LeaderboardEntry>
}

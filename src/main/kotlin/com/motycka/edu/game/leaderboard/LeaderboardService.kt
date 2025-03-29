package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.leaderboard.model.CharacterLeaderboard
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.jackson.JsonMixinModuleEntries
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * This is example of service implementation with repository dependency injection.
 */
@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
    private val entries: JsonMixinModuleEntries
) {
    fun getLeaderboard(): List<CharacterLeaderboard> {
        logger.debug { "Getting leaderboard" }
        return leaderboardRepository.getLeaderboard()
    }

    fun getLeaderboardByClass(characterClass: String): List<CharacterLeaderboard> {
        logger.debug { "Getting leaderboard by class $characterClass" }
        // characterClass in CharacterClass enum
        require(characterClass in CharacterClass.entries.map { it.name }) { "Invalid character class" }
        return leaderboardRepository.getLeaderboardByClass(characterClass)
    }
}

package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.leaderboard.model.LeaderboardCreate
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LeaderboardService(private val leaderboardRepository: LeaderboardRepository) {
    fun getLeaderboards(
        characterClass: String? = null
    ): List<LeaderboardResponse> {
        logger.info { "Getting leaderboards" }
        return leaderboardRepository.getLeaderboards(characterClass)
    }

    fun createLeaderboard(record: LeaderboardCreate): LeaderboardResponse {
        logger.info { "Creating leaderboard" }
        return leaderboardRepository.createLeaderboard(record)
    }
}

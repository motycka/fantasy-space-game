package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.leaderboard.model.Leaderboard
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LeaderboardService {
    fun getLeaderboards(
    ): List<Leaderboard> {
        logger.debug { "Getting matches" }

        return listOf(
            Leaderboard(
                position = 1,
                character = Character(
                    id = 1,
                    name = "Challenger",
                    health = 100,
                    attackPower = 10,
                    stamina = 100,
                    defensePower = 10,
                    mana = 100,
                    healingPower = 10,
                    characterClass = "Warrior",
                    level = "1",
                    experience = 0,
                    shouldLevelUp = false,
                    isOwner = false
                ),
                wins = 1,
                losses = 0,
                draws = 0,
            )
        )
    }
}

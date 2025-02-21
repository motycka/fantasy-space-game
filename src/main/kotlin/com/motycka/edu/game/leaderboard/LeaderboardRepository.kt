package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.model.Character
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Repository
class LeaderboardRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getCharacters(
        characterClass: String? = null,
        name: String? = null,
    ): List<Character> {
        logger.info { "Getting characters" }
        return jdbcTemplate.query(
            "SELECT * FROM character WHERE characterClass = ? AND name = ?;",
            ::rowMapper,
            characterClass,
            name,
        )
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, i: Int): Character {
        return Character(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("health"),
            rs.getInt("attackPower"),
            rs.getInt("stamina"),
            rs.getInt("defensePower"),
            rs.getInt("mana"),
            rs.getInt("healingPower"),
            rs.getString("characterClass"),
            rs.getString("level"),
            rs.getInt("experience"),
            rs.getBoolean("shouldLevelUp"),
            rs.getBoolean("isOwner")
        )
    }
}
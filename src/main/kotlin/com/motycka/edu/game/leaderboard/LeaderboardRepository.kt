package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.leaderboard.model.CharacterLeaderboard
import com.motycka.edu.game.leaderboard.rest.CharacterLeaderboardResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.collections.firstOrNull

private val logger = KotlinLogging.logger {}

/**
 * This is an example of repository implementation using JdbcTemplate.
 */
@Repository
class LeaderboardRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun getLeaderboard(): List<CharacterLeaderboard> {
        logger.debug { "Getting leaderboard" }
        return jdbcTemplate.query(
            """
            SELECT
                ROW_NUMBER() OVER (ORDER BY wins DESC, draws DESC, losses ASC) AS position,
                character_id,
                wins,
                draws,
                losses
            FROM (
                SELECT 
                    c.id AS character_id,
                    SUM(CASE 
                        WHEN (m.challenger_id = c.id AND m.match_outcome = 'CHALLENGER_WON') 
                          OR (m.opponent_id = c.id AND m.match_outcome = 'OPPONENT_WON') 
                        THEN 1 
                        ELSE 0 
                    END) AS wins,
                    SUM(CASE 
                        WHEN m.match_outcome = 'DRAW' 
                        THEN 1 
                        ELSE 0 
                    END) AS draws,
                    SUM(CASE 
                        WHEN (m.challenger_id = c.id AND m.match_outcome = 'OPPONENT_WON') 
                          OR (m.opponent_id = c.id AND m.match_outcome = 'CHALLENGER_WON') 
                        THEN 1 
                        ELSE 0 
                    END) AS losses
                FROM 
                    character c
                LEFT JOIN 
                    match m ON c.id = m.challenger_id OR c.id = m.opponent_id
                GROUP BY 
                    c.id
            ) AS leaderboard_data
            ORDER BY 
                position;
            """.trimIndent(),
            ::rowMapper
        )
    }

    fun getLeaderboardByClass(characterClass: String): List<CharacterLeaderboard> {
        logger.debug { "Getting leaderboard by class $characterClass" }
        return jdbcTemplate.query(
            """
            SELECT
                ROW_NUMBER() OVER (ORDER BY wins DESC, draws DESC, losses ASC) AS position,
                character_id,
                wins,
                draws,
                losses
            FROM (
                SELECT 
                    c.id AS character_id,
                    SUM(CASE 
                        WHEN (m.challenger_id = c.id AND m.match_outcome = 'CHALLENGER_WON') 
                          OR (m.opponent_id = c.id AND m.match_outcome = 'OPPONENT_WON') 
                        THEN 1 
                        ELSE 0 
                    END) AS wins,
                    SUM(CASE 
                        WHEN m.match_outcome = 'DRAW' 
                        THEN 1 
                        ELSE 0 
                    END) AS draws,
                    SUM(CASE 
                        WHEN (m.challenger_id = c.id AND m.match_outcome = 'OPPONENT_WON') 
                          OR (m.opponent_id = c.id AND m.match_outcome = 'CHALLENGER_WON') 
                        THEN 1 
                        ELSE 0 
                    END) AS losses
                FROM 
                    character c
                LEFT JOIN 
                    match m ON c.id = m.challenger_id OR c.id = m.opponent_id
                WHERE 
                    c.class = ?
                GROUP BY 
                    c.id
            ) AS leaderboard_data
            ORDER BY 
                position;
            """.trimIndent(),
            ::rowMapper,
            characterClass
        )
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, i: Int): CharacterLeaderboard {
        return CharacterLeaderboard(
            rs.getInt("position"),
            rs.getLong("character_id"),
            rs.getInt("wins"),
            rs.getInt("draws"),
            rs.getInt("losses")
        )
    }
}

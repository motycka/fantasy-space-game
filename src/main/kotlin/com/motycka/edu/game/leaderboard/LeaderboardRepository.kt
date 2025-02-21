package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.leaderboard.model.LeaderboardCreate
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Repository
class LeaderboardRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val characterService: CharacterService,
) {
    fun getLeaderboards(
        characterClass: String? = null,
    ): List<LeaderboardResponse> {
        logger.info { "Querying leaderboards" }
        val sql = StringBuilder("SELECT * FROM leaderboard ORDER BY wins DESC, draws DESC, losses ASC")
        val params = mutableListOf<Any>()

        if (characterClass != null) {
            sql.append(" WHERE character_id IN (SELECT id FROM character WHERE class = ?)")
            params.add(characterClass)
        }

        return jdbcTemplate.query(
            sql.toString(),
            ::rowMapper,
            *params.toTypedArray(),
        )
    }

    fun createLeaderboard(data: LeaderboardCreate): LeaderboardResponse {
        logger.info { "Inserting leaderboard" }

        val sql = """
            SELECT * FROM FINAL TABLE (
                MERGE INTO leaderboard AS l
                USING (
                  VALUES (
                    ?, 
                    CASE WHEN ? THEN 1 ELSE 0 END, 
                    CASE WHEN ? THEN 1 ELSE 0 END, 
                    CASE WHEN ? THEN 1 ELSE 0 END
                  )
                ) AS vals(character_id, wins_inc, losses_inc, draws_inc)
                ON l.character_id = vals.character_id
                WHEN MATCHED THEN
                    UPDATE SET wins = wins + vals.wins_inc,
                               losses = losses + vals.losses_inc,
                               draws = draws + vals.draws_inc
                WHEN NOT MATCHED THEN
                    INSERT (character_id, wins, losses, draws)
                    VALUES (vals.character_id, vals.wins_inc, vals.losses_inc, vals.draws_inc)
            )
        """.trimIndent()

        return jdbcTemplate.query(
            sql,
            ::rowMapper,
            data.characterId,
            data.wins ?: false,
            data.losses ?: false,
            data.draws ?: false,
        ).first()
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, rowNum: Int): LeaderboardResponse {
        val characterId = rs.getLong("character_id")
        val character = characterService.getCharacter(characterId)

        return LeaderboardResponse(
            position = rowNum,
            character = character,
            wins = rs.getInt("wins"),
            losses = rs.getInt("losses"),
            draws = rs.getInt("draws"),
        )
    }
}
package com.motycka.edu.game.match

import com.motycka.edu.game.match.model.Match
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun insertMatch(accountId: Long, match: Match): Match? {
        logger.debug { "Inserting match $match for account $accountId" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp) 
                    VALUES (?, ?, ?, ?, ?)
                );
            """.trimIndent(),
            ::rowMapper,
            match.challengerId,
            match.opponentId,
            match.matchOutcome,
            match.challengerXp,
            match.opponentXp
        ).firstOrNull()
    }



    fun getAllMatches(): List<Match> {
        return jdbcTemplate.query("SELECT * FROM match", ::rowMapper)
    }


    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, rowNum: Int): Match {
        return Match(
            id = rs.getLong("id"),
            challengerId = rs.getLong("challenger_id"),
            opponentId = rs.getLong("opponent_id"),
            matchOutcome = rs.getString("match_outcome"),
            challengerXp = rs.getInt("challenger_xp"),
            opponentXp = rs.getInt("opponent_xp")
        )
    }

    data class Round(
        val round: Int,
        val characterId: Long,
        val healthDelta: Int,
        val staminaDelta: Int,
        val manaDelta: Int
    )

    fun getRoundsByMatchId(matchId: Long): List<Round> {
        val sql = """
           SELECT round_number AS round, character_id, health_delta, stamina_delta, mana_delta 
           FROM round 
           WHERE match_id = ?
           ORDER BY round_number
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ ->
            Round(
                round = rs.getInt("round"),
                characterId = rs.getLong("character_id"),
                healthDelta = rs.getInt("health_delta"),
                staminaDelta = rs.getInt("stamina_delta"),
                manaDelta = rs.getInt("mana_delta")
            )
        }, matchId)
    }

    fun updateCharacterExperience(characterId: Long, xp: Int) {
        jdbcTemplate.update(
            "UPDATE character SET experience = experience + ? WHERE id = ?",
            xp,
            characterId
        )
    }


    fun updateLeaderboard(characterId: Long, wins: Int, losses: Int, draws: Int) {
        val rowsAffected = jdbcTemplate.update(
            """
                UPDATE leaderboard 
                SET wins = wins + ?, losses = losses + ?, draws = draws + ? 
                WHERE character_id = ?
            """.trimIndent(),
            wins,
            losses,
            draws,
            characterId
        )
        if (rowsAffected == 0) {
            jdbcTemplate.update(
                "INSERT INTO leaderboard (character_id, wins, losses, draws) VALUES (?, ?, ?, ?)",
                characterId, wins, losses, draws
            )
        }
    }

}
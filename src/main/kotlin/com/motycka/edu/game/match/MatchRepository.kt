package com.motycka.edu.game.match

import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchWithRounds
import com.motycka.edu.game.match.model.Round
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getALlMatchAndRounds(): List<MatchWithRounds>? {
        return jdbcTemplate.query(
            """
            SELECT * FROM match
            """.trimIndent(),
            ::matchMapper
        ).map { match ->
            MatchWithRounds(
                match,
                jdbcTemplate.query(
                    """
                    SELECT * FROM round
                    WHERE match_id = ?
                    ORDER BY round_number,
                        CASE
                            WHEN character_id = ? THEN 1  -- Challenger
                            ELSE 2
                        END
                    """.trimIndent(),
                    ::roundMapper,
                    match.id,
                    match.challengerId
                )
            )
        }
    }

    fun createMatchAndRounds(match: Match, rounds: List<Round>): MatchWithRounds? {
        // Insert match and get its ID along with other fields
        val insertedMatch = jdbcTemplate.query(
            """
            SELECT * FROM FINAL TABLE (
                INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp)
                VALUES (?, ?, ?, ?, ?)
            );
             """.trimIndent(),
            ::matchMapper,
            match.challengerId,
            match.opponentId,
            match.matchOutcome,
            match.challengerXp,
            match.opponentXp
        ).firstOrNull() ?: throw SQLException("Failed to insert match")

        // Insert rounds and get the inserted rows as well
        jdbcTemplate.batchUpdate(
            """
            INSERT INTO round (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            rounds.map {
                arrayOf(
                    insertedMatch.id,
                    it.round,
                    it.characterId,
                    it.healthDelta,
                    it.staminaDelta,
                    it.manaDelta
                )
            }
        ) ?: throw SQLException("Failed to insert rounds")

        // After inserting, retrieve the inserted rounds
        val roundsWithMatch = jdbcTemplate.query(
            """
            SELECT * FROM round 
            WHERE match_id = ?
            ORDER BY round_number, 
                CASE
                    WHEN character_id = ? THEN 1  -- Challenger
                    ELSE 2
                END
            """.trimIndent(),
            ::roundMapper,
            insertedMatch.id,
            insertedMatch.challengerId
        )

        // Return the match along with its inserted rounds
        return MatchWithRounds(insertedMatch, roundsWithMatch)
    }


    @Throws(SQLException::class)
    private fun matchMapper(rs: ResultSet, i: Int): Match {
        return Match(
            id = rs.getLong("id"),
            challengerId = rs.getLong("challenger_id"),
            opponentId = rs.getLong("opponent_id"),
            matchOutcome = rs.getString("match_outcome"),
            challengerXp = rs.getInt("challenger_xp"),
            opponentXp = rs.getInt("opponent_xp")
        )
    }

    @Throws(SQLException::class)
    private fun roundMapper(rs: ResultSet, i: Int): Round {
        return Round(
            id = rs.getLong("id"),
            matchId = rs.getLong("match_id"),
            round = rs.getInt("round_number"),
            characterId = rs.getLong("character_id"),
            healthDelta = rs.getInt("health_delta"),
            staminaDelta = rs.getInt("stamina_delta"),
            manaDelta = rs.getInt("mana_delta")
        )
    }
}

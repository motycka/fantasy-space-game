package com.motycka.edu.game.match

import com.motycka.edu.game.match.model.*
import com.motycka.edu.game.rounds.RoundRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val roundRepository: RoundRepository,
) {
    fun getMatches(): List<Match> {
        logger.info { "Querying matches" }

        val sql = """
            SELECT
                match.id as match_id, 
                match.challenger_id as challenger_id,
                match.opponent_id as opponent_id,
                match.match_outcome as match_outcome,
                match.challenger_xp as challenger_xp,
                match.opponent_xp as opponent_xp,
                challenger.id as challenger_id,
                challenger.name as challenger_name,
                challenger.class as challenger_class,
                challenger.level as challenger_level,
                challenger.experience as challenger_xp,
                opponent.id as opponent_id,
                opponent.name as opponent_name,
                opponent.class as opponent_class,
                opponent.level as opponent_level,
                opponent.experience as opponent_xp
            FROM match
            JOIN character as challenger ON match.challenger_id = challenger.id
            JOIN character as opponent ON match.opponent_id = opponent.id
        """.trimIndent()

        return jdbcTemplate.query(
            sql,
            { rs, i ->
                val matchId = rs.getLong("match_id")
                val rounds = roundRepository.getRoundsByMatchId(matchId)
                getMatchesRowMapper(rounds, rs, i)
            }
        )
    }

    fun createMatch(
        match: MatchCreate
    ): MatchSelect {
        logger.info { "Creating match" }

        val sql = """
            SELECT * FROM FINAL TABLE (
                INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp)
                VALUES (?, ?, ?, ?, ?)
            )
        """.trimIndent()

        return jdbcTemplate.query(
            sql,
            ::rowMapper,
            match.challengerId,
            match.opponentId,
            match.matchOutcome,
            match.challengerXp,
            match.opponentXp,
        ).first()
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, i: Int): MatchSelect {
        return MatchSelect(
            id = rs.getLong("id"),
            challengerId = rs.getLong("challenger_id"),
            opponentId = rs.getLong("opponent_id"),
            matchOutcome = rs.getString("match_outcome"),
            challengerXp = rs.getInt("challenger_xp"),
            opponentXp = rs.getInt("opponent_xp"),
        )
    }

    @Throws(SQLException::class)
    private fun getMatchesRowMapper(rounds: List<Round>, rs: ResultSet, i: Int): Match {
        val challenger = Player(
            id = rs.getLong("challenger_id"),
            name = rs.getString("challenger_name"),
            characterClass = rs.getString("challenger_class"),
            level = rs.getString("challenger_level"),
            experienceTotal = rs.getInt("challenger_xp"),
            experienceGained = 0,
            isVictor = false,
        )

        val opponent = Player(
            id = rs.getLong("opponent_id"),
            name = rs.getString("opponent_name"),
            characterClass = rs.getString("opponent_class"),
            level = rs.getString("opponent_level"),
            experienceTotal = rs.getInt("opponent_xp"),
            experienceGained = 0,
            isVictor = false,
        )

        return Match(
            id = rs.getLong("match_id"),
            challenger = challenger,
            opponent = opponent,
            rounds = rounds,
        )
    }
}
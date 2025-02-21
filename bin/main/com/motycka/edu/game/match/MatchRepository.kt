package com.motycka.edu.game.match

import com.motycka.edu.game.match.model.MatchId
import com.motycka.edu.game.match.model.MatchOutcome
import com.motycka.edu.game.match.model.MatchResult
import com.motycka.edu.game.match.model.MatchRoundResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun selectMatches(): List<MatchResult> {
        logger.debug { "Inserting matches: TODO" }
        return jdbcTemplate.query(
            "SELECT * FROM match;",
            ::matchMapper
        )
    }

    fun selectRounds(matchId: MatchId): List<MatchRoundResult> {
        logger.debug { "Selecting match by id $matchId" }
        return jdbcTemplate.query(
            "SELECT * FROM round WHERE match_id = ?;",
            ::roundMapper,
            matchId
        )
    }

    fun insertMatch(match: MatchResult): MatchResult {
        logger.info { "Inserting match $match" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp) 
                    VALUES (?, ?, ?, ?, ?)
                ) LIMIT 1;
            """.trimIndent(),
            ::matchMapper,
            match.challengerId,
            match.opponentId,
            match.matchOutcome.name,
            match.challengerExperience,
            match.opponentExperience
        ).firstOrNull() ?: error("Match could not be created.") // TODO
    }

    fun insertRound(matchId: MatchId, round: MatchRoundResult): List<MatchRoundResult> {
        logger.debug { "Inserting match round $round" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    INSERT INTO round (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta) 
                    VALUES (?, ?, ?, ?, ?, ?)
                );
            """.trimIndent(),
            ::roundMapper,
            matchId,
            round.round,
            round.characterId,
            round.healthDelta,
            round.staminaDelta,
            round.manaDelta
        )
    }

    private fun matchMapper(resultSet: ResultSet, index: Int): MatchResult {
        val matchId = resultSet.getLong("id")
        val challengerId = resultSet.getLong("challenger_id")
        val opponentId = resultSet.getLong("opponent_id")
        val matchOutcome = MatchOutcome.valueOf(resultSet.getString("match_outcome"))
        val challengerExperience = resultSet.getInt("challenger_xp")
        val opponentExperience = resultSet.getInt("opponent_xp")
        return MatchResult(
            id = matchId,
            challengerId = challengerId,
            challengerExperience = challengerExperience,
            opponentId = opponentId,
            opponentExperience = opponentExperience,
            matchOutcome = matchOutcome
        )
    }

    private fun roundMapper(resultSet: ResultSet, index: Int): MatchRoundResult {
        val roundId = resultSet.getLong("id")
        val roundNumber = resultSet.getInt("round_number")
        val characterId = resultSet.getLong("character_id")
        val healthDelta = resultSet.getInt("health_delta")
        val staminaDelta = resultSet.getInt("stamina_delta")
        val manaDelta = resultSet.getInt("mana_delta")
        return MatchRoundResult(
            id = roundId,
            round = roundNumber,
            characterId = characterId,
            healthDelta = healthDelta,
            staminaDelta = staminaDelta,
            manaDelta = manaDelta
        )
    }
}

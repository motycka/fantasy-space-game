package com.motycka.edu.game.rounds

import com.motycka.edu.game.match.model.MatchId
import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.rounds.rest.RoundCreate
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger() {}

@Repository
class RoundRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun getRoundsByMatchId(matchId: MatchId): List<Round> {
        logger.info { "Querying rounds" }

        val sql = "SELECT * FROM round WHERE match_id = ?"

        return jdbcTemplate.query(sql, ::rowMapper, matchId)
    }

    fun getRoundByMatchIdAndRoundNumber(matchId: MatchId, roundNumber: Int): Round {
        logger.info { "Querying rounds" }

        val sql = """
            SELECT
                round.round_number as round_number,
                round.character_id as character_id,
                round.health_delta as health_delta,
                round.stamina_delta as stamina_delta,
                round.mana_delta as mana_delta
            FROM match
            WHERE match.id = ? AND round.round_number = ?
        """.trimIndent()

        return jdbcTemplate.query(sql, ::rowMapper, matchId, roundNumber).first()
    }

    fun recordRound(
        roundRecord: RoundCreate,
    ): Round {
        val sql = """
            INSERT INTO round (
                match_id,
                round_number,
                character_id,
                health_delta,
                stamina_delta,
                mana_delta
            ) VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            roundRecord.matchId,
            roundRecord.roundNumber,
            roundRecord.characterId,
            roundRecord.healthDelta,
            roundRecord.staminaDelta,
            roundRecord.manaDelta,
        )

        return getRoundByMatchIdAndRoundNumber(roundRecord.matchId, roundRecord.roundNumber)
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, i: Int): Round {
        return Round(
            round = rs.getInt("round_number"),
            characterId = rs.getLong("character_id"),
            healthDelta = rs.getInt("health_delta"),
            staminaDelta = rs.getInt("stamina_delta"),
            manaDelta = rs.getInt("mana_delta")
        )
    }
}
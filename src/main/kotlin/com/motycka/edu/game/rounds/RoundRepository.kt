package com.motycka.edu.game.rounds

import com.motycka.edu.game.match.model.MatchId
import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.rounds.rest.RoundCreate
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.math.round

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

    fun recordRound(
        roundRecord: RoundCreate,
    ): Round {
        logger.info { "Inserting round #${roundRecord.roundNumber} of match #${roundRecord.matchId}" }

        val sql = """
            SELECT * FROM FINAL TABLE (
                INSERT INTO round (
                    match_id,
                    round_number,
                    character_id,
                    health_delta,
                    stamina_delta,
                    mana_delta
                ) VALUES (?, ?, ?, ?, ?, ?)
            )
        """.trimIndent()

        return jdbcTemplate.query(
            sql,
            ::rowMapper,
            roundRecord.matchId,
            roundRecord.roundNumber,
            roundRecord.characterId,
            roundRecord.healthDelta,
            roundRecord.staminaDelta,
            roundRecord.manaDelta,
        ).first()
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
package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.rest.CharacterId
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class LeaderboardRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun selectLeaderboard(): List<Leaderboard> {
        return jdbcTemplate.query(
            "SELECT * FROM leaderboard ORDER BY wins - losses DESC",
            ::rowMapper
        )
    }

    fun updateLeaderboard(characterId: CharacterId, win: Boolean, loss: Boolean) {
        val wins = if (win) 1 else 0
        val losses = if (loss) 1 else 0
        val draws = if (win.not() && loss.not()) 1 else 0

        val updated = jdbcTemplate.update(
            "UPDATE leaderboard SET wins = wins + ?, losses = losses + ?, draws = draws + ? WHERE character_id = ?;",
            wins,
            losses,
            draws,
            characterId
        )

        if (updated == 0) {
            jdbcTemplate.update(
                "INSERT INTO leaderboard (character_id, wins, losses, draws) VALUES (?, ?, ?, ?);",
                characterId,
                wins,
                losses,
                draws
            )
        }

    }

    private fun rowMapper(rs: ResultSet, index: Int): Leaderboard {
        return Leaderboard(
            position = index + 1,
            characterId = rs.getLong("character_id"),
            wins = rs.getInt("wins"),
            losses = rs.getInt("losses"),
            draws = rs.getInt("draws")
        )
    }

}

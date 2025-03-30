package com.motycka.edu.game.leaderboard

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

data class LeaderboardRow(
    val characterId: Long,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val name: String,
    val health: Int,
    val attack: Int,
    val defense: Int,
    val stamina: Int,
    val healing: Int,
    val mana: Int,
    val characterClass: String,
    val experience: Int,
    val accountId: Long
)

@Repository
class LeaderboardRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getLeaderboard(filterClass: String?): List<LeaderboardRow> {
        val sql = StringBuilder("""
            SELECT lb.character_id, lb.wins, lb.losses, lb.draws, 
                   c.name, c.health, c.attack, c.defense, c.stamina, c.healing, c.mana, 
                   c.class AS character_class, c.experience, c.account_id
            FROM leaderboard lb
            JOIN character c ON lb.character_id = c.id
        """.trimIndent())
        val params = mutableListOf<Any>()
        if (filterClass != null) {
            sql.append(" WHERE c.class = ?")
            params.add(filterClass)
        }
        sql.append(" ORDER BY lb.wins DESC, lb.losses ASC, lb.draws ASC")
        return jdbcTemplate.query(sql.toString(), { rs, _ -> rowMapper(rs) }, *params.toTypedArray())
    }

    private fun rowMapper(rs: ResultSet): LeaderboardRow {
        return LeaderboardRow(
            characterId = rs.getLong("character_id"),
            wins = rs.getInt("wins"),
            losses = rs.getInt("losses"),
            draws = rs.getInt("draws"),
            name = rs.getString("name"),
            health = rs.getInt("health"),
            attack = rs.getInt("attack"),
            defense = rs.getInt("defense"),
            stamina = rs.getInt("stamina"),
            healing = rs.getInt("healing"),
            mana = rs.getInt("mana"),
            characterClass = rs.getString("character_class"),
            experience = rs.getInt("experience"),
            accountId = rs.getLong("account_id")
        )
    }
}

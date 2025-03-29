package com.motycka.edu.game.character

import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.model.CharacterId
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
class CharacterRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun selectAll(): List<Character>? {
        logger.debug { "Selecting all characters" }
        return jdbcTemplate.query(
            "SELECT * FROM character;",
            ::rowMapper
        )
    }

    fun selectById(id: CharacterId): Character? {
        logger.debug { "Selecting character by id $id" }
        return jdbcTemplate.query(
            "SELECT * FROM character WHERE id = ?;",
            ::rowMapper,
            id
        ).firstOrNull()
    }

    fun selectByAccountId(accountId: AccountId): List<Character>? {
        logger.debug { "Selecting characters by account id $accountId" }
        return jdbcTemplate.query(
            "SELECT * FROM character WHERE account_id = ?;",
            ::rowMapper,
            accountId
        )
    }

    fun selectOpponentsByAccountId(accountId: AccountId): List<Character>? {
        logger.debug { "Selecting opponents by account id $accountId" }
        return jdbcTemplate.query(
            "SELECT * FROM character WHERE account_id != ?;",
            ::rowMapper,
            accountId
        )
    }

    fun insertCharacter(character: Character): Character? {
        logger.debug { "Inserting new character ${character.copy(accountId = null)}" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    INSERT INTO character (account_id, name, class, experience, health, attack, defense, stamina, healing, mana)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)                
                );
            """.trimIndent(),
            ::rowMapper,
            character.accountId,
            character.name,
            character.characterClass.name,
            character.experience,
            character.health,
            character.attackPower,
            character.defensePower,
            character.stamina,
            character.healingPower,
            character.mana
        ).firstOrNull()
    }

    fun levelUpCharacter(character: Character): Character? {
        logger.debug { "Leveling up character ${character.copy(accountId = null)}" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    UPDATE character
                    SET health = ?, attack = ?, defense = ?, stamina = ?, healing = ?, mana = ?
                    WHERE id = ?
                );
            """.trimIndent(),
            ::rowMapper,
            character.health,
            character.attackPower,
            character.defensePower,
            character.stamina,
            character.healingPower,
            character.mana,
            character.id
        ).firstOrNull()
    }

    fun updateExperience(character: Character, experience: Int): Character? {
        logger.debug { "Adding $experience experience to character ${character.copy(accountId = null)}" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    UPDATE character
                    SET experience = experience + ?
                    WHERE id = ?
                );
            """.trimIndent(),
            ::rowMapper,
            experience,
            character.id
        ).firstOrNull()
    }

    fun updateCharacter(character: Character): Character? {
        logger.debug { "Updating character ${character.copy(accountId = null)}" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (
                    UPDATE character
                    SET name = ?, class = ?, experience = ?, health = ?, attack = ?, defense = ?, stamina = ?, healing = ?, mana = ?
                    WHERE id = ?
                );
            """.trimIndent(),
            ::rowMapper,
            character.name,
            character.characterClass.name,
            character.experience,
            character.health,
            character.attackPower,
            character.defensePower,
            character.stamina,
            character.healingPower,
            character.mana,
            character.id
        ).firstOrNull()
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, i: Int): Character {
        return Character(
            id = rs.getLong("id"),
            accountId = rs.getLong("account_id"),
            name = rs.getString("name"),
            characterClass = CharacterClass.valueOf(rs.getString("class")),
            experience = rs.getInt("experience"),
            health = rs.getInt("health"),
            attackPower = rs.getInt("attack"),
            defensePower = rs.getInt("defense"),
            stamina = rs.getInt("stamina"),
            healingPower = rs.getInt("healing"),
            mana = rs.getInt("mana")
        )
    }
}

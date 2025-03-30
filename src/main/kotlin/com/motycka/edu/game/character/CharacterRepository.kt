package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.CharactersFilter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Repository
class CharacterRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun selectWithFilter(accountId: Long, filter: CharactersFilter): List<Character> {
        logger.debug { "Selecting characters for account $accountId with filter: $filter" }
        val sql = StringBuilder("""
            SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience
            FROM character
            WHERE account_id = ?
        """.trimIndent())
        val params = mutableListOf<Any>(accountId)
        filter.characterClass?.let {
            sql.append(" AND class = ?")
            params.add(it)
        }
        filter.name?.let {
            sql.append(" AND name ILIKE ?")
            params.add("%$it%")
        }
        return jdbcTemplate.query(sql.toString(), ::rowMapper, *params.toTypedArray())
    }

    @Throws(SQLException::class)
    private fun rowMapper(rs: ResultSet, i: Int): Character {
        return Character(
            id = rs.getLong("id"),
            accountId = rs.getLong("account_id"),
            name = rs.getString("name"),
            characterClass = rs.getString("character_class"),
            health = rs.getInt("health"),
            attack = rs.getInt("attack"),
            defense = rs.getInt("defense"),
            stamina = rs.getInt("stamina"),
            healing = rs.getInt("healing"),
            mana = rs.getInt("mana"),
            experience = rs.getInt("experience")
        )
    }

    fun selectCharacter(accountId: Long, id: Long): Character? {
        logger.debug { "Selecting character $id for account $accountId" }

        print("Selecting character $id for account $accountId")

        var query = jdbcTemplate.query(
            "SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience FROM character WHERE account_id = ? AND id = ?",
            ::rowMapper,
            accountId,
            id
        ).firstOrNull()

        return query
    }

    fun selectOpponentCharacter(accountId: Long, id: Long): Character? {
        logger.debug { "Selecting character $id for account $accountId" }

        print("Selecting character $id for account $accountId")

        var query = jdbcTemplate.query(
            "SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience FROM character WHERE account_id != ? AND id = ?",
            ::rowMapper,
            accountId,
            id
        ).firstOrNull()

        return query
    }

    fun findById(id: Long): Character? {
        val sql = """
            SELECT
                id, account_id, name, health, attack_power, stamina, defense_power, mana, healing_power,
                character_class, level, experience, should_level_up
            FROM characters
            WHERE id = ?
        """.trimIndent()

        return jdbcTemplate.query(sql, ::rowMapper, id).firstOrNull()
    }


    fun insertCharacters(accountId: Long, character: Character): Character? {
        logger.debug { "Inserting new character $character" }
        return jdbcTemplate.query(
            """
                SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience
                FROM FINAL TABLE (
                    INSERT INTO character (account_id, name, class, health, attack, defense, stamina, healing, mana, experience)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                );
            """.trimIndent(),
            ::rowMapper,
            accountId,
            character.name,
            character.characterClass,
            character.health,
            character.attack,
            character.defense,
            character.stamina,
            character.healing,
            character.mana,
            character.experience
        ).firstOrNull()
    }

    fun updateCharacter(accountId: Long, character: Character): Character? {
        logger.debug { "Updating character $character" }
        return jdbcTemplate.query(
            """
                SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience
                FROM FINAL TABLE (
                    UPDATE character
                    SET name = ?, class = ?, health = ?, attack = ?, defense = ?, stamina = ?, healing = ?, mana = ?, experience = ?
                    WHERE account_id = ? AND id = ?
                );
            """.trimIndent(),
            ::rowMapper,
            character.name,
            character.characterClass,
            character.health,
            character.attack,
            character.defense,
            character.stamina,
            character.healing,
            character.mana,
            character.experience,
            accountId,
            character.id
        ).firstOrNull()
    }

    //Retrieves all challengers (characters owned by the current user).
    fun selectChallengers(accountId: Long): List<Character> {
        logger.debug { "Selecting challengers for account $accountId" }
        return jdbcTemplate.query(
            "SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience FROM character WHERE account_id = ?",
            ::rowMapper,
            accountId
        )
    }

    //Retrieves all opponents (characters not owned by the current user).
    fun selectOpponents(accountId: Long): List<Character> {
        logger.debug { "Selecting opponents for account $accountId" }
        return jdbcTemplate.query(
            "SELECT id, account_id, name, class AS character_class, health, attack, defense, stamina, healing, mana, experience FROM character WHERE account_id != ?",
            ::rowMapper,
            accountId
        )
    }

    fun isOwner(accountId: Long, characterId: Long): Boolean {
        logger.debug { "Checking if account $accountId is the owner of character $characterId" }
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM character WHERE account_id = ? AND id = ?",
            Int::class.java,
            accountId,
            characterId
        ) == 1
    }







}

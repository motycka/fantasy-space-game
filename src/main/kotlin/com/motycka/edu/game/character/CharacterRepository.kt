package com.motycka.edu.game.character

import com.motycka.edu.game.account.model.AccountId
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.rest.CharacterCreateRequest
import com.motycka.edu.game.character.rest.CharacterUpdateRequest
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
    fun getCharacters(
        accId: AccountId,
        characterClass: String? = null,
        name: String? = null,
    ): List<Character> {
        logger.info { "Getting characters" }
        val query = StringBuilder("SELECT * FROM character")
        val params = mutableListOf<Any>()

        if (characterClass != null || name != null) {
            query.append(" WHERE")
            if (characterClass != null) {
                query.append(" class = ?")
                params.add(characterClass)
            }
            if (name != null) {
                if (characterClass != null) query.append(" AND")
                query.append(" name = ?")
                params.add(name)
            }
        }

        val sql = query.toString()
        logger.debug { "Query:\n$sql\nParams:\n$params" }

        return jdbcTemplate.query(
            sql,
            { rs, i -> rowMapper(accId, rs, i) },
            *params.toTypedArray(),
        )
    }

    fun getMatchCharacters(
        accId: AccountId,
        challenger: Boolean,
    ): List<Character> {
        logger.info { "Getting match characters" }
        val query = StringBuilder("SELECT * FROM character")

        if (challenger) {
            query.append(" WHERE account_id = ?")
        } else {
            query.append(" WHERE account_id != ?")
        }

        val sql = query.toString()
        logger.debug { "Query:\n$sql\nParams:\n$accId" }

        return jdbcTemplate.query(
            sql,
            { rs, i -> rowMapper(accId, rs, i) },
            accId,
        )
    }

    fun getCharacter(
        accId: AccountId,
        id: CharacterId
    ): Character {
        logger.info { "Getting character #$id" }
        return jdbcTemplate.query(
            "SELECT * FROM character WHERE id = ?;",
            { rs, i -> rowMapper(accId, rs, i) },
            id,
        ).first()
    }

    fun createCharacter(
        accId: AccountId,
        character: CharacterCreateRequest,
    ): Character {
        logger.info { "Creating character" }

        if (character.characterClass == "WARRIOR") {
            if (character.stamina == null || character.defensePower == null) {
                throw IllegalArgumentException("Warrior must have stamina and defensePower")
            }
        } else if (character.characterClass == "SORCERER") {
            if (character.mana == null || character.healingPower == null) {
                throw IllegalArgumentException("Mage must have mana and healingPower")
            }
        }

        val sql = """
            INSERT INTO character (
                account_id, attack, class, defense, experience, health, healing, mana, name, stamina
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            accId,
            character.attackPower,
            character.characterClass,
            character.defensePower,
            0,
            character.health,
            character.healingPower,
            character.mana,
            character.name,
            character.stamina,
        )
        return getCharacters(accId, character.characterClass, character.name).first()
    }

    fun updateCharacter(
        accId: AccountId,
        id: CharacterId,
        character: CharacterUpdateRequest,
    ): Character {
        logger.info { "Updating character #${id}" }

        val sql = """
            UPDATE character SET
                attack = ?,
                class = ?,
                healing = ?,
                health = ?,
                mana = ?,
                name = ?,
                experience = ?
            WHERE id = ?;
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            character.attackPower,
            character.characterClass,
            character.healingPower,
            character.health,
            character.mana,
            character.name,
            0,
            id,
        )
        return getCharacter(accId, id)
    }

    @Throws(SQLException::class)
    private fun rowMapper(id: AccountId, rs: ResultSet, i: Int): Character {
        val exp = rs.getInt("experience")
        val currentLvl = CharacterLevel.fromExp(exp)
        val shouldLevelUp = currentLvl.canLevelUp(exp)

        return Character(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            health = rs.getInt("health"),
            attackPower = rs.getInt("attack"),
            stamina = rs.getInt("stamina"),
            defensePower = rs.getInt("defense"),
            mana = rs.getInt("mana"),
            healingPower = rs.getInt("healing"),
            characterClass = rs.getString("class"),
            level = currentLvl.name,
            experience = exp,
            shouldLevelUp = shouldLevelUp,
            isOwner = rs.getLong("account_id") == id,
        )
    }
}
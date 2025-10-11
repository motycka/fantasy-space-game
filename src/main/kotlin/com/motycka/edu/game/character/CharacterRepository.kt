package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.character.rest.CharacterClass
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet


private val logger = KotlinLogging.logger {}

@Repository
class CharacterRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun selectWithFilter(accountId: AccountId, filter: CharactersFilter): List<Character> {
        logger.debug { "Selecting characters with filter $filter" }

//        val sql = StringBuilder("SELECT * FROM character WHERE 1=1")
//        val params = mutableListOf<Any>()
//
//        // Add ID filter if present
//        filter.ids?.let { ids ->
//            if (ids.isNotEmpty()) {
//                sql.append(" AND id IN (${ids.joinToString(",") { "?" }})")
//                params.addAll(ids)
//            }
//        }
//
//        // Add account filter based on include flags
//        when {
//            filter.includeChallengers && filter.includeOpponents.not() -> {
//                sql.append(" AND account_id = ?")
//                params.add(accountId)
//            }
//            filter.includeOpponents && filter.includeChallengers.not() -> {
//                sql.append(" AND account_id != ?")
//                params.add(accountId)
//            }
//            filter.includeChallengers.not() && filter.includeOpponents.not() -> {
//                error("At least one of includeChallengers or includeOpponents must be true")
//            }
//            // else: both includeChallengers and includeOpponents are true, no account filter needed
//        }
//
//        return jdbcTemplate.query(
//            sql.toString(),
//            ::rowMapper,
//            *params.toTypedArray()
//        )

        val whereIds = if (filter.ids != null) "id IN (${filter.ids.joinToString(",")})" else null
        val whereAccount = when {
            filter.includeChallengers && filter.includeOpponents.not() -> "account_id = $accountId"
            filter.includeOpponents && filter.includeChallengers.not() -> "account_id != $accountId"
            filter.includeChallengers.not() && filter.includeOpponents.not() ->
                error("At least one of includeChallengers or includeOpponents must be true")
            else -> null
        }

        val where = when {
            whereIds != null && whereAccount == null -> "WHERE $whereIds"
            whereIds != null && whereAccount != null -> "WHERE $whereIds AND $whereAccount"
            whereIds == null && whereAccount != null -> "WHERE $whereAccount"
            else -> ""
        }

        return jdbcTemplate.query(
            "SELECT * FROM character $where",
            ::rowMapper
        )
    }

    fun insertCharacters(accountId: AccountId, character: Character): Character? {
        logger.debug { "Inserting character: $character" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (INSERT INTO character (account_id, name, class, health, attack, energy, ability, experience, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?));
            """.trimIndent(),
            ::rowMapper,
            accountId,
            character.name,
            character.characterClass.name,
            character.health,
            character.attack,
            character.energy,
            character.ability,
            character.experience,
            character.level.ordinal + 1
        ).firstOrNull()
    }

    fun updateCharacter(character: Character): Character? {
        logger.debug { "Updating character: $character" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (UPDATE character SET name = ?, health = ?, attack = ?, energy = ?, ability = ?, experience = ?, level = ? WHERE id = ?);
            """.trimIndent(),
            ::rowMapper,
            character.name,
            character.health,
            character.attack,
            character.energy,
            character.ability,
            character.experience,
            character.level.ordinal + 1, // Database stores level as 1-10, enum is 0-9
            character.id
        ).firstOrNull()
    }

    fun updateExperience(characterId: CharacterId, gainedExperience: Int): Character? {
        logger.debug { "Updating experience for character $characterId" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (UPDATE character SET experience = experience + ? WHERE id = ?);
            """.trimIndent(),
            ::rowMapper,
            gainedExperience,
            characterId
        ).firstOrNull()
    }

    private fun rowMapper(resultSet: ResultSet, index: Int): Character {
        val characterClass = CharacterClass.valueOf(resultSet.getString("class"))
        val id = resultSet.getLong("id")
        val accountId = resultSet.getLong("account_id")
        val name = resultSet.getString("name")
        val health = resultSet.getInt("health")
        val attackPower = resultSet.getInt("attack")
        val experience = resultSet.getInt("experience")
        // Read level from database (1-10) and convert to enum (LEVEL_1 to LEVEL_10)
        val level = CharacterLevel.entries[resultSet.getInt("level") - 1]
        val energy = resultSet.getInt("energy")
        val ability = resultSet.getInt("ability")

        return when (characterClass) {
            CharacterClass.SORCERER -> Sorcerer(
                id = id,
                accountId = accountId,
                name = name,
                health = health,
                attack = attackPower,
                level = level,
                experience = experience,
                mana = energy,
                healing = ability
            )
            CharacterClass.WARRIOR -> Warrior(
                id = id,
                accountId = accountId,
                name = name,
                health = health,
                attack = attackPower,
                level = level,
                experience = experience,
                stamina = energy,
                defense = ability
            )
        }
    }
}


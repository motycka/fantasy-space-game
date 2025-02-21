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
                SELECT * FROM FINAL TABLE (INSERT INTO character (account_id, name, class, health, attack, mana, healing, experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?));
            """.trimIndent(),
            ::rowMapper,
            accountId,
            character.name,
            when (character) {
                is Sorcerer -> CharacterClass.SORCERER
                is Warrior -> CharacterClass.WARRIOR
                else -> error("Unknown character class") // TODO
            }.name,
            character.health,
            character.attackPower,
            when (character) {
                is Sorcerer -> character.mana
                is Warrior -> character.stamina
                else -> error("Unknown character class") // TODO
            },
            when (character) {
                is Sorcerer -> character.healingPower
                is Warrior -> character.defensePower
                else -> error("Unknown character class") // TODO
            },
            character.experience
        ).firstOrNull()
    }

    fun updateCharacter(character: Character): Character? {
        logger.debug { "Updating character: $character" }
        return jdbcTemplate.query(
            """
                SELECT * FROM FINAL TABLE (UPDATE character SET name = ?, health = ?, attack = ?, mana = ?, healing = ?, experience = ? WHERE id = ?);
            """.trimIndent(),
            ::rowMapper,
            character.name,
            character.health,
            character.attackPower,
            when (character) {
                is Sorcerer -> character.mana
                is Warrior -> character.stamina
                else -> error("Unknown character class") // TODO
            },
            when (character) {
                is Sorcerer -> character.healingPower
                is Warrior -> character.defensePower
                else -> error("Unknown character class") // TODO
            },
            character.experience,
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
        val level = CharacterLevel.entries.first { experience <= it.experience }
        return when (characterClass) {
            CharacterClass.SORCERER -> Sorcerer(
                id = id,
                accountId = accountId,
                name = name,
                health = health,
                attackPower = attackPower,
                level = level,
                experience = experience,
                mana = resultSet.getInt("mana"),
                healingPower = resultSet.getInt("healing")
            )
            CharacterClass.WARRIOR -> Warrior(
                id = id,
                accountId = accountId,
                name = name,
                health = health,
                attackPower = attackPower,
                level = level,
                experience = experience,
                stamina = resultSet.getInt("stamina"),
                defensePower = resultSet.getInt("defense")
            )
        }
    }
}


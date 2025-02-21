package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Warrior
import com.motycka.edu.game.character.rest.CharactersFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class CharacterServiceIT {

    @Autowired
    private lateinit var characterRepository: CharacterRepository

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var characterService: CharacterService

    private val accountId = 1L

    @Test
    fun `createCharacter should return created character`() {
        val character = Warrior(
            id = 1,
            accountId = accountId,
            name = "Warrior",
            health = 140,
            attackPower = 20,
            experience = 0,
            stamina = 20,
            defensePower = 20,
            level = CharacterLevel.LEVEL_1
        )

        val result = characterService.createCharacter(character)

        assertEquals(character, result)
        val savedCharacter = characterRepository.selectWithFilter(
            accountId = accountId,
            filter = CharactersFilter.DEFAULT.copy(ids = setOf(character.characterId))
        )
        assertEquals(character, savedCharacter)
    }
}

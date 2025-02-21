package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Warrior
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CharacterServiceTest {

    private val characterRepository: CharacterRepository = mockk()
    private val accountService: AccountService  = mockk()
    private val characterService: CharacterService = CharacterService(
        characterRepository = characterRepository,
        accountService = accountService
    )

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

        every { accountService.getCurrentAccountId() } returns accountId
        every { characterRepository.insertCharacters(accountId = accountId, character = character) } returns character

        val result = characterService.createCharacter(character)

        assertEquals(character, result)
        verify { characterRepository.insertCharacters(accountId = accountId, character = character) }
    }

}

package com.motycka.edu.game.character

import com.fasterxml.jackson.databind.ObjectMapper
import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Warrior
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic

@WebMvcTest(CharacterController::class)
class CharacterControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var characterService: CharacterService

    @MockBean
    private lateinit var accountService: AccountService

    private val accountId = 1L

    @BeforeEach
    fun setUp() {
        characterService = mockk()
        accountService = mockk()
    }

    @Test
    fun `postCharacter should return created character`() {
        val character = Warrior(
            id = 1,
            accountId = accountId,
            name = "Kotlin Warrior",
            health = 140,
            attackPower = 20,
            experience = 0,
            stamina = 20,
            defensePower = 20,
            level = CharacterLevel.LEVEL_1
        )

        every { accountService.getCurrentAccountId() } returns accountId
        every { characterService.createCharacter(any()) } returns character

        mockMvc.perform(post("/api/characters")
            .contentType("application/json")
            .content("""
            {
              "name": "${character.name}",
              "health": ${character.health},
              "attackPower": ${character.attackPower},
              "stamina": ${character.stamina},
              "defensePower": ${character.defensePower},
              "characterClass": "WARRIOR"
            }
            """.trimIndent())
            .with(csrf())
            .with(httpBasic("username", "password")))
            .andExpect(status().isOk)
            .andExpect(content().json(
                """
                {
                  "id": ${character.characterId},
                  "name": "${character.name}",
                  "health": ${character.health},
                  "attackPower": ${character.attackPower},
                  "stamina": ${character.stamina},
                  "defensePower": ${character.defensePower},
                  "characterClass": "WARRIOR",
                  "level": "LEVEL_1",
                  "experience": 0,
                  "shouldLevelUp": false,
                  "isOwner": true,
                  "mana": null,
                  "healingPower": null
                }
            """.trimIndent()
            ))

        verify { characterService.createCharacter(character = character) }
    }
}

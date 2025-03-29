package com.motycka.edu.game.account

import com.fasterxml.jackson.databind.ObjectMapper
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.toAccountResponse
import com.motycka.edu.game.config.SecurityConfiguration
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AccountController::class)
@Import(SecurityConfiguration::class)
class CharacterControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var accountService: AccountService

    private val objectMapper = ObjectMapper()
    private val endpoint = "/api/accounts"

    private val newAccount = AccountFixtures.DEVELOPER
    private val existingAccount = AccountFixtures.TESTER

    @BeforeEach
    fun setUp() {
        every { accountService.createAccount(any()) } returns newAccount
        every { accountService.getByUsername(existingAccount.username) } returns existingAccount
        every { accountService.getCurrentAccountId() } returns existingAccount.id!!
        every { accountService.getAccount() } returns existingAccount
    }

    @Test
    fun `postAccount should create account`() {
        val accountRegistrationRequest = AccountRegistrationRequest(
            name = newAccount.name,
            username = newAccount.username,
            password = newAccount.password
        )

        val expectedResponse = objectMapper.writeValueAsString(
            newAccount.toAccountResponse()
        )

        mockMvc.perform(
            post(endpoint)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRegistrationRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(content().json(expectedResponse))

        verify {
            accountService.createAccount(
                account = newAccount.copy(id = null)
            )
        }
    }

    @Test
    fun `getAccounts should return current account`() {
        val expectedResponse = objectMapper.writeValueAsString(
            existingAccount.toAccountResponse()
        )

        mockMvc.perform(
            get(endpoint)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(existingAccount.username, existingAccount.password))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(expectedResponse))

        verify {
            accountService.getAccount()
        }
    }
}

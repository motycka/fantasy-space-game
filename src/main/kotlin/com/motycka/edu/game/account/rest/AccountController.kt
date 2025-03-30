package com.motycka.edu.game.account.rest

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @GetMapping("/{username}")
    fun getAccount(@PathVariable username: String): ResponseEntity<AccountResponse> {
        val account = accountService.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(account.toAccountResponse())
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postAccount(@RequestBody accountRequest: AccountRegistrationRequest): AccountResponse {
        val account = accountService.createAccount(accountRequest.toAccount())
        return account.toAccountResponse()
    }
}

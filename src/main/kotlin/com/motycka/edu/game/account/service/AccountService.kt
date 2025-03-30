package com.motycka.edu.game.account.service

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {

    fun getCurrentAccountId(): Long? {
        return 1L
    }

    fun findByUsername(username: String): Account? {
        return accountRepository.findByUsername(username)
    }

    fun createAccount(account: Account): Account {
        return accountRepository.save(account)
    }
}

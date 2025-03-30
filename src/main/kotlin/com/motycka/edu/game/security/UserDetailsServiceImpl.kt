package com.motycka.edu.game.security

import com.motycka.edu.game.account.repository.AccountRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val accountRepository: AccountRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val account = accountRepository.selectByUsername(username)
            ?: throw UsernameNotFoundException("User not found")

        return User(account.username, account.password, listOf())
    }
}

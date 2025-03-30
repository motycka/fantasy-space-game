package com.motycka.edu.game.account.rest

data class AccountRegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)

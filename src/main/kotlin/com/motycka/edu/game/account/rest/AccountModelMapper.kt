package com.motycka.edu.game.account.rest

import com.motycka.edu.game.account.model.Account

fun AccountRegistrationRequest.toAccount() = Account(
    username = username,
    email = email,
    password = password
)

fun Account.toAccountResponse() = AccountResponse(
    username = username,
    email = email
)

package com.motycka.edu.game.account.model

import jakarta.persistence.*

@Entity
@Table(name = "account") // Fixed table name to match schema.sql
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    val password: String = ""
) {
    // No-arg constructor required by JPA
    constructor() : this(0, "", "", "")
}

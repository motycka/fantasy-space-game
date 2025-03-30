package com.motycka.edu.game.character.model

import jakarta.persistence.*

@Entity
@Table(name = "characters")
data class Character(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val health: Int,

    @Column(nullable = false)
    val attackPower: Int,

    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val characterClass: CharacterClass,

    @Column(nullable = false)
    val level: Int = 1,

    @Column(nullable = false)
    val experience: Int = 0,

    @Column(nullable = false)
    val accountId: Long
) {
    constructor() : this(0, "", 100, 10, null, null, null, null, CharacterClass.WARRIOR, 1, 0, 0)
}

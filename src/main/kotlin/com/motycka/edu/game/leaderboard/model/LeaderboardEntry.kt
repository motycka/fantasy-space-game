package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.model.Character
import jakarta.persistence.*

@Entity
@Table(name = "leaderboard")
data class LeaderboardEntry(
    @Id
    val characterId: Long,

    @OneToOne
    @MapsId
    @JoinColumn(name = "character_id", referencedColumnName = "id")
    val character: Character,

    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0
) {
    constructor() : this(0, Character(), 0, 0, 0)
}


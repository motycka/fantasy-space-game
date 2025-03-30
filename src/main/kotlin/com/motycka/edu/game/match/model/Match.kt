package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.model.Character
import jakarta.persistence.*

@Entity
@Table(name = "matches")
data class Match(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "challenger_id")
    val challenger: Character,

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    val opponent: Character,

    @Enumerated(EnumType.STRING)
    val matchOutcome: MatchOutcome
) {
    constructor() : this(0, Character(), Character(), MatchOutcome.DRAW)
}

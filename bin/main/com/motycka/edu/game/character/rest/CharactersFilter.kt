package com.motycka.edu.game.character.rest

data class CharactersFilter(
    val ids: Set<CharacterId>?,
    val includeChallengers: Boolean,
    val includeOpponents: Boolean,
) {

    companion object {
        val DEFAULT = CharactersFilter(
            ids = null,
            includeChallengers = true,
            includeOpponents = true,
        )
        val CHALLENGERS = CharactersFilter(
            ids = null,
            includeChallengers = true,
            includeOpponents = false,
        )
        val OPPONENTS = CharactersFilter(
            ids = null,
            includeChallengers = false,
            includeOpponents = true
        )
    }
}

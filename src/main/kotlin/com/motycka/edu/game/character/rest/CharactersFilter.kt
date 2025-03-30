package com.motycka.edu.game.character.rest

data class CharactersFilter(
    val characterClass: String? = null,
    val name: String? = null
) {
    companion object {
        val DEFAULT = CharactersFilter()
    }
}

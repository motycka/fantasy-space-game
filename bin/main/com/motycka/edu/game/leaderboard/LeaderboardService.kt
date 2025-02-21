package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.rest.CharacterClass
import com.motycka.edu.game.character.rest.CharacterId
import com.motycka.edu.game.character.rest.CharactersFilter
import com.motycka.edu.game.character.rest.toCharacterResponse
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import com.motycka.edu.game.account.AccountService
import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    fun getLeaderboard(characterClass: CharacterClass?): List<LeaderboardResponse> {
        val accountId = accountService.getCurrentAccountId()
        val characters = characterService.getCharacters(CharactersFilter.Companion.DEFAULT).associateBy { it.id }
        val leaderboard = leaderboardRepository.selectLeaderboard()

        return leaderboard.map { leaderboard ->
            LeaderboardResponse(
                position = leaderboard.position,
                character = characters[leaderboard.characterId]!!.toCharacterResponse(accountId), // TODO handle
                wins = leaderboard.wins,
                losses = leaderboard.losses,
                draws = leaderboard.draws
            )
        }.filter { it.character.characterClass == characterClass || characterClass == null }
    }

    fun updateLeaderboard(characterId: CharacterId, win: Boolean, loss: Boolean) {
        leaderboardRepository.updateLeaderboard(
            characterId = characterId,
            win = win,
            loss = loss
        )
    }
}

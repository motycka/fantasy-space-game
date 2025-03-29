package com.motycka.edu.game.match

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.CharacterRepository
import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterId
import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchWithRounds
import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.match.model.matchBattle
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}


/**
 * This is example of service implementation with repository dependency injection.
 */
@Service
class MatchService(
    private val accountService: AccountService,
    private val characterRepository: CharacterRepository,
    private val matchRepository: MatchRepository
) {

    fun getAllMatchesAndRounds(): List<MatchWithRounds> {
        logger.debug { "Getting all matches" }
        val matchesAndRounds = matchRepository.getALlMatchAndRounds()
        return matchesAndRounds ?: error("No matches found")
    }

    fun createMatchAndRounds(
        rounds: Int,
        challengerId: CharacterId,
        opponentId: CharacterId
    ): MatchWithRounds {
        val accountId = accountService.getCurrentAccountId()

        val challenger = characterRepository.selectById(challengerId)
            ?: error("No character found for id $challengerId.")

        if (challenger.accountId != accountId) {
            error("Character does not belong to the current user.")
        }

        val opponent = characterRepository.selectById(opponentId)
            ?: error("No character found for id $opponentId.")

        val matchWithRounds = matchBattle(
            nRounds = rounds,
            challenger = challenger,
            opponent = opponent,
            200,
            100,
            100
        )

        val insertedMatchWithRounds = matchRepository.createMatchAndRounds(matchWithRounds.match, matchWithRounds.rounds)
            ?: error("The match couldn't be created")

        return insertedMatchWithRounds
    }
}

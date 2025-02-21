package com.motycka.edu.game.match.rest

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.rest.getClass
import com.motycka.edu.game.match.model.MatchResultWithCharacters

fun MatchResultWithCharacters.toMatchResponse() = MatchResponse(
    id = requireNotNull(match.id) { "Match id is required" },
    challenger = challenger.toMatchCharacterTo(
        experienceGained = challengerExperience
    ),
    opponent = opponent.toMatchCharacterTo(
        experienceGained = opponentExperience
    ),
    rounds = rounds.map { round ->
        MatchRoundResponse(
            round = round.round,
            characterId = round.characterId,
            healthDelta = round.healthDelta,
            staminaDelta = round.staminaDelta,
            manaDelta = round.manaDelta
        )
    },
    matchOutcome = match.matchOutcome
)

fun List<MatchResultWithCharacters>.toMatchResponseTos() = map {
    it.toMatchResponse()
}

fun Character.toMatchCharacterTo(experienceGained: Int) = MatchCharacterResponse(
    id = requireNotNull(id) { "Character id must not be null." },
    name = name,
    characterClass = getClass(),
    level = level,
    experienceTotal = experience,
    experienceGained = experienceGained
)


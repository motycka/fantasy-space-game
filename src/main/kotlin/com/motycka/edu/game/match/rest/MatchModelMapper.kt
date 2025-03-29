package com.motycka.edu.game.match.rest

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchWithRounds

fun Character.toCharacterMatchResponse(
    experienceGained: Int
) = CharacterMatchResponse(
    id = requireNotNull(id) { "Character id is required" },
    name = name,
    characterClass = characterClass,
    level = level,
    experienceTotal = experience,
    experienceGained = experienceGained
)

fun MatchWithRounds.toMatchResponse(
    challenger: CharacterMatchResponse,
    opponent: CharacterMatchResponse,
    matchOutcome: String
) = MatchResponse(
    id = requireNotNull(match.id) { "Match id is required" },
    challenger = challenger,
    opponent = opponent,
    rounds = rounds,
    matchOutcome = matchOutcome
)
package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterId

fun Character.toCharacterInBattle(): CharacterInBattle {
    return CharacterInBattle(
        health = health,
        attackPower = attackPower,
        defensePower = defensePower,
        stamina = stamina,
        healingPower = healingPower,
        mana = mana
    )
}

fun matchBattle(
    nRounds: Int,
    challenger: Character,
    opponent: Character,
    winnerExperience: Int,
    loserExperience: Int,
    drawExperience: Int
): MatchWithRounds {
    require(challenger.id != null) { "Challenger must have an id" }
    require(opponent.id != null) { "Opponent must have an id" }

    var challengerInBattle = challenger.toCharacterInBattle()
    var opponentInBattle = opponent.toCharacterInBattle()

    val rounds = mutableListOf<Round>()

    for (i in 1..nRounds) {

        val beforeRoundChallenger = challengerInBattle
        val beforeRoundOpponent = opponentInBattle

        challengerInBattle = challengerInBattle.beforeRound()
        opponentInBattle = opponentInBattle.beforeRound()

        val (updatedChallenger, updatedOpponent) = challengerInBattle.attack(opponentInBattle)
        challengerInBattle = updatedChallenger
        opponentInBattle = updatedOpponent

        val (updatedOpponent2, updatedChallenger2) = opponentInBattle.attack(challengerInBattle)
        opponentInBattle = updatedOpponent2
        challengerInBattle = updatedChallenger2

        challengerInBattle = challengerInBattle.afterRound()
        opponentInBattle = opponentInBattle.afterRound()

        val challengerRound = Round(
            round = i,
            characterId = challenger.id,
            healthDelta = challengerInBattle.health - beforeRoundChallenger.health,
            staminaDelta = challengerInBattle.stamina - beforeRoundChallenger.stamina,
            manaDelta = challengerInBattle.mana - beforeRoundChallenger.mana
        )

        val opponentRound = Round(
            round = i,
            characterId = opponent.id,
            healthDelta = opponentInBattle.health - beforeRoundOpponent.health,
            staminaDelta = opponentInBattle.stamina - beforeRoundOpponent.stamina,
            manaDelta = opponentInBattle.mana - beforeRoundOpponent.mana
        )

        rounds.add(challengerRound)
        rounds.add(opponentRound)

        if (!challengerInBattle.isAlive() || !opponentInBattle.isAlive()) break
    }


    val matchOutcome = when {
        !challengerInBattle.isAlive() && !opponentInBattle.isAlive() -> "DRAW"
        !challengerInBattle.isAlive() -> "CHALLENGER_WON"
        !opponentInBattle.isAlive() -> "OPPONENT_WON"
        else -> "DRAW"
    }

    val challengerXp = when (matchOutcome) {
        "CHALLENGER_WON" -> winnerExperience
        "OPPONENT_WON" -> loserExperience
        else -> drawExperience
    }

    val opponentXp = when (matchOutcome) {
        "CHALLENGER_WON" -> loserExperience
        "OPPONENT_WON" -> winnerExperience
        else -> drawExperience
    }

    return MatchWithRounds(
        match = Match(
            challengerId = challenger.id,
            opponentId = opponent.id,
            matchOutcome = matchOutcome,
            challengerXp = challengerXp,
            opponentXp = opponentXp
        ),
        rounds = rounds
    )
}

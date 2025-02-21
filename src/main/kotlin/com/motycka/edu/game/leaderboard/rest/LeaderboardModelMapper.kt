package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.model.Leaderboard

fun Leaderboard.toMatchResponse() = LeaderboardResponse(
    position = requireNotNull(position) { "Leaderboard position must not be null" },
)

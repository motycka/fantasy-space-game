package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.service.LeaderboardService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) characterClass: String?): List<LeaderboardResponse> {
        return leaderboardService.getLeaderboard(characterClass).map { it.toLeaderboardResponse() }
    }
}

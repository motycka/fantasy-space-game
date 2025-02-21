package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {
    @GetMapping
    fun getLeaderboards(
        @RequestParam(required = false) characterClass: String?
    ): List<LeaderboardResponse> {
       return leaderboardService.getLeaderboards(characterClass)
    }
}
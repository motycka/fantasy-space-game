package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.leaderboard.model.Leaderboard
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {
    @GetMapping
    fun getLeaderboards(): List<Leaderboard> {
       return leaderboardService.getLeaderboards()
    }
}
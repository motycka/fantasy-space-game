package com.motycka.edu.game.leaderboard

import org.springframework.http.ResponseEntity
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
    fun getLeaderboard(
        @RequestParam(value = "class", required = false) characterClass: String?
    ): ResponseEntity<List<LeaderboardResponse>> {
        val leaderboard = leaderboardService.getLeaderboard(characterClass)
        return ResponseEntity.ok(leaderboard)
    }
}

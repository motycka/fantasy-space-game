package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.rest.CharacterClass
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        @RequestParam(CLASS_PARAM, required = false) characterClass: String?,
    ): List<LeaderboardResponse> {
        return leaderboardService.getLeaderboard(
            characterClass = if (characterClass != null) CharacterClass.valueOf(characterClass) else null
        )
    }

    companion object {
        const val CLASS_PARAM = "class"
    }

}

package com.motycka.edu.game.match

import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.MatchSelect
import com.motycka.edu.game.match.rest.MatchCreateRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val matchService: MatchService
) {
    @GetMapping
    fun getMatches(): List<Match> {
       return matchService.getMatches()
    }

    @PostMapping
    fun createMatch(
        @RequestBody matchRequest: MatchCreateRequest,
    ): MatchSelect {
        return matchService.simulateMatches(matchRequest)
    }
}
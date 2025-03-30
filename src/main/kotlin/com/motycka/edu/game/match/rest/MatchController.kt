package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(private val matchService: MatchService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMatch(@RequestBody matchRequest: MatchRequest): MatchResponse {
        return matchService.createMatch(matchRequest).toMatchResponse()
    }

    @GetMapping
    fun getMatches(): List<MatchResponse> {
        return matchService.getAllMatches().map { it.toMatchResponse() }
    }
}

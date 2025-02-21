package com.motycka.edu.game.match

import com.motycka.edu.game.match.rest.MatchResponse
import com.motycka.edu.game.match.rest.MatchRequest
import com.motycka.edu.game.match.rest.toMatchResponse
import com.motycka.edu.game.match.rest.toMatchResponseTos
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val matchService: MatchService,
) {

    @GetMapping
    fun getMatches(): List<MatchResponse> {
        return matchService.getMatches().toMatchResponseTos()
    }

    @PostMapping
    fun postMatch(
        @RequestBody match: MatchRequest
    ): MatchResponse {
        return matchService.doMatch(
            rounds = match.rounds,
            challengerId = match.challengerId,
            opponentId = match.opponentId
        ).toMatchResponse()
    }
}



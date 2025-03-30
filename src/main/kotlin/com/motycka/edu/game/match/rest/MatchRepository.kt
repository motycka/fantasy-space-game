package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.model.Match
import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository : JpaRepository<Match, Long>

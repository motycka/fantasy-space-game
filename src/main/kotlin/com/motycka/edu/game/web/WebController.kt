package com.motycka.edu.game.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class WebController {

    @GetMapping("/")
    fun home(): String {
        return "redirect:/index.html"
    }
}

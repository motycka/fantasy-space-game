package com.motycka.edu.game.character.model

enum class CharacterLevel(val points: Int, val experience: Long) {
    LEVEL_1(200, 0),
    LEVEL_2(210, 1000), // +1000
    LEVEL_3(230, 3000), // +2000
    LEVEL_4(260, 6000), // +3000
    LEVEL_5(300, 10000), // +4000
    LEVEL_6(350, 15000), // +5000
    LEVEL_7(410, 21000), // +6000
    LEVEL_8(480, 28000), // +7000
    LEVEL_9(560, 36000), // +8000
    LEVEL_10(650, 45000); // +9000
}

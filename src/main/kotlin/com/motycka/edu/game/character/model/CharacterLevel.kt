package com.motycka.edu.game.character.model

enum class CharacterLevel(val minExperience: Int, val nextLevelThreshold: Int?) {
    LEVEL_1(minExperience = 0, nextLevelThreshold = 1000),
    LEVEL_2(minExperience = 1000, nextLevelThreshold = 1999),
    LEVEL_3(minExperience = 2000, nextLevelThreshold = 2999),
    LEVEL_4(minExperience = 3000, nextLevelThreshold = 3999),
    LEVEL_5(minExperience = 4000, nextLevelThreshold = 4999),
    LEVEL_6(minExperience = 5000, nextLevelThreshold = 5999),
    LEVEL_7(minExperience = 6000, nextLevelThreshold = 6999),
    LEVEL_8(minExperience = 7000, nextLevelThreshold = 7999),
    LEVEL_9(minExperience = 8000, nextLevelThreshold = 8999),
    LEVEL_10(minExperience = 9000, nextLevelThreshold = null);

    companion object {
        fun fromExp(exp: Int): CharacterLevel {
            return when {
                exp >= LEVEL_2.minExperience -> LEVEL_2
                exp >= LEVEL_3.minExperience -> LEVEL_3
                exp >= LEVEL_4.minExperience -> LEVEL_4
                exp >= LEVEL_5.minExperience -> LEVEL_5
                exp >= LEVEL_6.minExperience -> LEVEL_6
                exp >= LEVEL_7.minExperience -> LEVEL_7
                exp >= LEVEL_8.minExperience -> LEVEL_8
                exp >= LEVEL_9.minExperience -> LEVEL_9
                exp >= LEVEL_10.minExperience -> LEVEL_10
                else -> LEVEL_1
            }
        }
    }

    fun canLevelUp(exp: Int): Boolean {
        val lvl = fromExp(exp)
        return exp > (lvl.nextLevelThreshold ?: 0)
    }
}
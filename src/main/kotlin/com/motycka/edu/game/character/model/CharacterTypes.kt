package com.motycka.edu.game.character.model

typealias CharacterId = Long

enum class CharacterClass {
    WARRIOR,
    SORCERER,
}


interface WarriorStats {
    val defensePower: Int
    val stamina: Int
}

interface SorcererStats {
    val healingPower: Int
    val mana: Int
}

interface CharacterStats :
    WarriorStats,
    SorcererStats
{
    val health: Int
    val attackPower: Int
}

interface Levelable {
    val level: CharacterLevel
    val experience: Int
    val shouldLevelUp: Boolean
}

enum class CharacterLevel(val expRequirement: Int?,  val totalPoints: Int, val level: Int) {
    LEVEL_1(0, 200, 1),
    LEVEL_2(1000, 210, 2),
    LEVEL_3(3000, 230, 3),
    LEVEL_4(6000, 260, 4),
    LEVEL_5(10000, 300, 5),
    LEVEL_6(15000, 350, 6),
    LEVEL_7(21000, 410, 7),
    LEVEL_8(28000, 480, 8),
    LEVEL_9(36000, 560, 9),
    LEVEL_10(45000, 650, 10);

    fun nextLevel(): CharacterLevel {
        return entries.firstOrNull { it.level == level + 1 } ?: entries.last()
    }

    companion object {
        fun fromExp(exp: Int): CharacterLevel {
            return entries.filter { it.expRequirement != null }
                .sortedByDescending { it.expRequirement!! }
                .firstOrNull { exp >= it.expRequirement!! } ?: LEVEL_1
        }

        fun fromTotalPoints(totalPoints: Int): CharacterLevel {
            return entries
                .sortedByDescending { it.totalPoints }
                .firstOrNull { totalPoints >= it.totalPoints } ?: LEVEL_1
        }
    }
}
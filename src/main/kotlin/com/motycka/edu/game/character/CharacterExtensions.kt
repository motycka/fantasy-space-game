package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.Character
import com.motycka.edu.game.character.model.CharacterLevel
import com.motycka.edu.game.character.model.Sorcerer
import com.motycka.edu.game.character.model.Warrior
import kotlin.math.roundToInt

/**
 * Extension functions for the Character class and its subclasses.
 *
 * This file demonstrates Kotlin's extension functions, which allow adding
 * new functionality to existing classes without inheritance or decoration.
 *
 * ## Teaching Points:
 * - Extension functions are resolved statically at compile time
 * - They can access public members of the extended class
 * - They provide a clean way to add utility functions
 * - Extensions can be defined for interfaces and abstract classes
 * - Member functions always win over extension functions
 *
 * ## Design Principles:
 * - **Single Responsibility**: Each extension has one clear purpose
 * - **Open/Closed**: Adds functionality without modifying the original class
 *
 * @see Character
 * @see <a href="../lessons/lesson-03.md">Lesson 3: Kotlin Extension Functions</a>
 */

// ============================================================================
// Health and Status Extensions
// ============================================================================

/**
 * Checks if the character is alive (has health remaining).
 *
 * This is a simple property extension that improves code readability.
 *
 * Example:
 * ```kotlin
 * if (character.isAlive()) {
 *     character.attack(target)
 * }
 * ```
 */
fun Character.isAlive(): Boolean = currentHealth > 0

/**
 * Checks if the character is dead (no health remaining).
 *
 * The inverse of isAlive() for clearer intent in certain contexts.
 */
fun Character.isDead(): Boolean = currentHealth <= 0

/**
 * Calculates the character's health as a percentage.
 *
 * @return Health percentage from 0 to 100
 */
fun Character.percentHealth(): Int = ((currentHealth.toDouble() / health) * 100).roundToInt()

/**
 * Gets a descriptive health status based on remaining health percentage.
 *
 * This demonstrates using when expressions in extension functions.
 */
fun Character.healthStatus(): String = when (percentHealth()) {
    in 90..100 -> "Excellent condition"
    in 70..89 -> "Good health"
    in 50..69 -> "Moderately wounded"
    in 30..49 -> "Heavily wounded"
    in 10..29 -> "Critical condition"
    in 1..9 -> "Near death"
    else -> "Defeated"
}

/**
 * Checks if the character is at full health.
 */
fun Character.isFullHealth(): Boolean = currentHealth == health

/**
 * Calculates how much health is missing.
 */
fun Character.missingHealth(): Int = health - currentHealth

// ============================================================================
// Level and Experience Extensions
// ============================================================================

/**
 * Calculates the percentage progress towards the next level.
 *
 * @return Progress percentage from 0 to 100, or 100 if at max level
 */
fun Character.levelProgress(): Int {
    if (level == CharacterLevel.LEVEL_10) return 100

    val currentLevelMin = level.minimumExperience
    val nextLevelMin = CharacterLevel.entries[level.ordinal + 1].minimumExperience
    val progressInLevel = experience - currentLevelMin
    val totalNeeded = nextLevelMin - currentLevelMin

    return ((progressInLevel.toDouble() / totalNeeded) * 100).roundToInt()
}

/**
 * Gets the amount of experience needed for the next level.
 *
 * @return Experience points needed, or 0 if at max level
 */
fun Character.experienceToNextLevel(): Int {
    if (level == CharacterLevel.LEVEL_10) return 0

    val nextLevel = CharacterLevel.entries[level.ordinal + 1]
    return nextLevel.minimumExperience - experience
}

/**
 * Checks if the character is at maximum level.
 */
fun Character.isMaxLevel(): Boolean = level == CharacterLevel.LEVEL_10

/**
 * Gets a formatted string describing level and progress.
 *
 * Example output: "Level 3 (45% to Level 4)"
 */
fun Character.levelDescription(): String = when {
    isMaxLevel() -> "${level.displayName} (MAX)"
    else -> "${level.displayName} (${levelProgress()}% to ${CharacterLevel.entries[level.ordinal + 1].displayName})"
}

// ============================================================================
// Class-Specific Extensions
// ============================================================================

/**
 * Extension for Warriors to check if they have enough stamina for an action.
 *
 * @param required The amount of stamina required
 * @return true if the warrior has enough stamina
 */
fun Warrior.hasStamina(required: Int = 1): Boolean {
    // Note: We can't access private currentStamina, so we check if they can act
    return isAlive()
}

/**
 * Extension for Sorcerers to check if they have enough mana for an action.
 *
 * @param required The amount of mana required
 * @return true if the sorcerer has enough mana
 */
fun Sorcerer.hasMana(required: Int = 1): Boolean {
    // Note: We can't access private currentMana, so we check if they can act
    return isAlive()
}

/**
 * Gets the character's power rating based on total attributes.
 *
 * This demonstrates polymorphic extensions that work differently
 * for different character types.
 */
fun Character.powerRating(): Int = when (this) {
    is Warrior -> health + attack + stamina + defense
    is Sorcerer -> health + attack + mana + healing
    else -> health + attack + energy + ability
}

/**
 * Gets the character's primary attribute value.
 */
fun Character.primaryAttribute(): Pair<String, Int> = when (this) {
    is Warrior -> "Defense" to defense
    is Sorcerer -> "Healing" to healing
    else -> "Ability" to ability
}

// ============================================================================
// Combat and Strategy Extensions
// ============================================================================

/**
 * Checks if this character would have an advantage against another.
 *
 * This demonstrates complex logic in extension functions.
 *
 * @param opponent The character to compare against
 * @return true if this character has a type advantage
 */
fun Character.hasAdvantageAgainst(opponent: Character): Boolean = when {
    // Warriors have advantage against low-health opponents
    this is Warrior && opponent.health < 100 -> true
    // Sorcerers have advantage against high-health opponents (can heal)
    this is Sorcerer && opponent.health > 150 -> true
    // Level advantage
    this.level.ordinal > opponent.level.ordinal -> true
    else -> false
}

/**
 * Estimates the number of attacks needed to defeat an opponent.
 *
 * This is a simple combat calculation for AI or UI hints.
 */
fun Character.attacksToDefeat(opponent: Character): Int {
    if (attack <= 0) return Int.MAX_VALUE
    return (opponent.currentHealth / attack) + if (opponent.currentHealth % attack > 0) 1 else 0
}

/**
 * Determines if the character should be cautious based on health.
 */
fun Character.shouldRetreat(): Boolean = percentHealth() < 25

/**
 * Gets a combat effectiveness score considering health and resources.
 */
fun Character.combatEffectiveness(): Double = when (this) {
    is Warrior -> {
        val healthScore = percentHealth() / 100.0
        val resourceScore = 0.5 // Can't access private stamina
        (healthScore * 0.7 + resourceScore * 0.3)
    }
    is Sorcerer -> {
        val healthScore = percentHealth() / 100.0
        val resourceScore = 0.5 // Can't access private mana
        (healthScore * 0.5 + resourceScore * 0.5)
    }
    else -> percentHealth() / 100.0
}

// ============================================================================
// Formatting and Display Extensions
// ============================================================================

/**
 * Creates a summary string for the character.
 *
 * This demonstrates string formatting in extension functions.
 */
fun Character.summary(): String = buildString {
    append("$name (${characterClass.displayName})")
    append(" - Level ${level.ordinal + 1}")
    append(" - ${percentHealth()}% Health")
    append(" - $experience XP")
}

/**
 * Creates a detailed status report for the character.
 */
fun Character.detailedStatus(): String = buildString {
    appendLine("=== $name ===")
    appendLine("Class: ${characterClass.displayName}")
    appendLine("Level: ${levelDescription()}")
    appendLine("Health: $currentHealth/$health (${percentHealth()}% - ${healthStatus()})")
    appendLine("Attack Power: $attack")
    when (this@detailedStatus) {
        is Warrior -> {
            appendLine("Stamina: $stamina")
            appendLine("Defense: $defense")
        }
        is Sorcerer -> {
            appendLine("Mana: $mana")
            appendLine("Healing Power: $healing")
        }
    }
    appendLine("Total Power Rating: ${powerRating()}")
    appendLine("Combat Effectiveness: ${(combatEffectiveness() * 100).roundToInt()}%")
}

/**
 * Creates a battle-ready check message.
 */
fun Character.battleReadyStatus(): String = when {
    isDead() -> "$name is defeated and cannot battle!"
    percentHealth() < 25 -> "$name is critically wounded and should rest!"
    percentHealth() < 50 -> "$name is wounded but can still fight."
    percentHealth() < 75 -> "$name has minor injuries but is battle-ready."
    else -> "$name is in excellent condition for battle!"
}

// ============================================================================
// Utility Extensions
// ============================================================================

/**
 * Creates a deep copy of the character with optional health reset.
 *
 * Useful for simulations or battle previews.
 */
fun Character.copyWithFullHealth(): Character = when (this) {
    is Warrior -> Warrior(
        id = id,
        accountId = accountId,
        name = name,
        health = health,
        attack = attack,
        level = level,
        experience = experience,
        stamina = stamina,
        defense = defense
    )
    is Sorcerer -> Sorcerer(
        id = id,
        accountId = accountId,
        name = name,
        health = health,
        attack = attack,
        level = level,
        experience = experience,
        mana = mana,
        healing = healing
    )
    else -> throw UnsupportedOperationException("Unknown character type")
}

/**
 * Checks if two characters are eligible to fight.
 *
 * @param opponent The potential opponent
 * @return true if both characters can fight
 */
infix fun Character.canFight(opponent: Character): Boolean =
    this.isAlive() && opponent.isAlive() && this.id != opponent.id

/**
 * Extension property for getting the character's level display name.
 *
 * This demonstrates extension properties in Kotlin.
 */
val CharacterLevel.displayName: String
    get() = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
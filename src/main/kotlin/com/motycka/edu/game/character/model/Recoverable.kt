package com.motycka.edu.game.character.model

/**
 * Interface for characters that can recover resources during battle.
 *
 * This interface demonstrates polymorphism by allowing different character types
 * to implement their own recovery logic without the match system needing to know
 * the specific implementation details.
 *
 * ## Implementation Examples:
 * - **Warrior**: Regenerates stamina before each round
 * - **Sorcerer**: Regenerates mana before each round
 *
 * The match system can call `beforeRound()` on any character, and each type
 * will recover the appropriate resource for its class.
 *
 * @see Character The base class that implements this interface
 */
interface Recoverable {
    /**
     * Called before each round to allow characters to recover resources.
     * Each character type implements its own recovery logic.
     */
    fun beforeRound()

    /**
     * Called after each round to apply any end-of-round effects.
     * Currently unused but provides extension point for future mechanics.
     */
    fun afterRound()
}


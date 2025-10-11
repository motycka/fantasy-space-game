package com.motycka.edu.game.character.model

/**
 * Interface for characters with defensive abilities.
 *
 * This interface is specific to Warrior characters, who can reduce incoming
 * damage by raising their shield. The defense ability requires stamina and
 * reduces damage based on the character's defense power.
 *
 * ## Key Characteristics:
 * - Requires stamina to use defensive abilities
 * - Defense power determines how much damage is blocked
 * - Cannot defend when out of stamina
 * - Defense is applied automatically when receiving attacks
 *
 * @see Warrior The class that implements this interface
 */
interface Defender {
//    /**
//     * Character's name for logging defense actions.
//     */
//    val name: String
//
//    /**
//     * Current stamina available for attacking and defending.
//     */
//    val stamina: Int
//
//    /**
//     * Amount of damage blocked when defending with stamina.
//     */
//    val defense: Int

    /**
     * Attempts to reduce incoming damage by blocking with shield.
     * Called automatically when the Warrior receives an attack.
     *
     * @param attack The incoming damage amount
     * @return The reduced damage after applying defense
     */
    fun defend(attack: Int): Int
}

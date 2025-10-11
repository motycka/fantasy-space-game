package com.motycka.edu.game.character.model

/**
 * Interface for characters with healing abilities.
 *
 * This interface is specific to Sorcerer characters, who can use their mana
 * to restore their own health during combat. The healing ability is called
 * automatically before each attack.
 *
 * ## Key Characteristics:
 * - Requires mana to cast healing
 * - Healing power determines how much health is restored
 * - Cannot heal beyond maximum health
 * - Cannot heal if dead or out of mana
 *
 * @see Sorcerer The class that implements this interface
 */
interface Healer {
//    /**
//     * Current mana available for healing and spellcasting.
//     */
//    val mana: Int
//
//    /**
//     * Amount of health restored per heal action.
//     */
//    val healing: Int

    /**
     * Attempts to restore health using mana.
     * Called automatically before the Sorcerer attacks.
     */
    fun heal()
}

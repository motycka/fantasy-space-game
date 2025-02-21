export const CHARACTER_CLASSES = {
    WARRIOR: {
        name: 'Warrior',
        properties: {
            health: { type: 'number', min: 1, max: 200, default: 110 },
            attackPower: { type: 'number', min: 1, max: 100, default: 40 },
            stamina: { type: 'number', min: 1, max: 100, default: 20 },
            defensePower: { type: 'number', min: 1, max: 100, default: 30 }
        }
    },
    SORCERER: {
        name: 'Sorcerer',
        properties: {
            health: { type: 'number', min: 1, max: 200, default: 100 },
            attackPower: { type: 'number', min: 1, max: 100, default: 40 },
            mana: { type: 'number', min: 0, max: 100, default: 30 },
            healingPower: { type: 'number', min: 0, max: 100, default: 30 }
        }
    }
};

export const COMMON_DISPLAY_PROPERTIES = ['health', 'attackPower'];
export const CLASS_SPECIFIC_PROPERTIES = {
    WARRIOR: ['stamina', 'defensePower'],
    SORCERER: ['mana', 'healingPower']
}; 
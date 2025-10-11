export const CHARACTER_CLASSES = {
    WARRIOR: {
        name: 'Warrior',
        properties: {
            health: { type: 'number', min: 1, max: 200, default: 110 },
            attack: { type: 'number', min: 1, max: 100, default: 40 },
            stamina: { type: 'number', min: 1, max: 100, default: 20 },
            defense: { type: 'number', min: 1, max: 100, default: 30 }
        }
    },
    SORCERER: {
        name: 'Sorcerer',
        properties: {
            health: { type: 'number', min: 1, max: 200, default: 100 },
            attack: { type: 'number', min: 1, max: 100, default: 40 },
            mana: { type: 'number', min: 0, max: 100, default: 30 },
            healing: { type: 'number', min: 0, max: 100, default: 30 }
        }
    }
};

export const CHARACTER_LEVELS = {
    LEVEL_1: { ordinal: 0, points: 200, experience: 0 },
    LEVEL_2: { ordinal: 1, points: 210, experience: 1000 },
    LEVEL_3: { ordinal: 2, points: 230, experience: 3000 },
    LEVEL_4: { ordinal: 3, points: 260, experience: 6000 },
    LEVEL_5: { ordinal: 4, points: 300, experience: 10000 },
    LEVEL_6: { ordinal: 5, points: 350, experience: 15000 },
    LEVEL_7: { ordinal: 6, points: 410, experience: 21000 },
    LEVEL_8: { ordinal: 7, points: 480, experience: 28000 },
    LEVEL_9: { ordinal: 8, points: 560, experience: 36000 },
    LEVEL_10: { ordinal: 9, points: 650, experience: 45000 }
};

export const COMMON_DISPLAY_PROPERTIES = ['health', 'attack'];
export const CLASS_SPECIFIC_PROPERTIES = {
    WARRIOR: ['stamina', 'defense'],
    SORCERER: ['mana', 'healing']
}; 
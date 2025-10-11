import ApiClient from './apiClient.js';

export default class CharacterService extends ApiClient {
    static async getCharacters() {
        return this.getAuthenticated('/api/characters');
    }

    static async getChallengers() {
        return this.getAuthenticated('/api/characters/challengers');
    }

    static async getOpponents() {
        return this.getAuthenticated('/api/characters/opponents');
    }

    static async createCharacter(characterData) {
        return this.postAuthenticated('/api/characters', characterData);
    }

    static async levelUp(characterId, characterData) {
        return this.putAuthenticated(`/api/characters/${characterId}`, characterData);
    }
} 
import ApiClient from './apiClient.js';

export default class MatchService extends ApiClient {
    static async getMatches() {
        return this.getAuthenticated('/api/matches');
    }

    static async createMatch(challengerId, opponentId, rounds) {
        return this.postAuthenticated('/api/matches', {
            challengerId,
            opponentId,
            rounds
        });
    }
} 
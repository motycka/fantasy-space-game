import ApiClient from './apiClient.js';

export default class LeaderboardService extends ApiClient {
    static async getLeaderboard() {
        return this.getAuthenticated('/api/leaderboards');
    }
} 
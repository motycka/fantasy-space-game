import ApiClient from './apiClient.js';

export default class AccountService extends ApiClient {
    
    static async getUserAccount() {
        return this.getAuthenticated('/api/accounts');
    }

    static async register(name, username, password) {
        return this.post('/api/accounts', {
            name,
            username,
            password
        });
    }
} 
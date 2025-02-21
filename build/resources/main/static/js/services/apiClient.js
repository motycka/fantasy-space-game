export default class ApiClient {
    static async getAuthenticated(endpoint) {
        const response = await fetch(endpoint, {
            headers: {
                'Authorization': `Basic ${window.sessionStorage.getItem('auth')}`
            }
        });
        
        if (!response.ok) {
            throw new Error(await response.text() || `Failed to fetch from ${endpoint}`);
        }
        
        return response.json();
    }

    static async postAuthenticated(endpoint, data) {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${window.sessionStorage.getItem('auth')}`
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(await response.text() || `Failed to post to ${endpoint}`);
        }

        return response.json();
    }

    static async post(endpoint, data) {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(await response.text() || `Failed to post to ${endpoint}`);
        }

        return response.json();
    }

    static async putAuthenticated(endpoint, data) {
        const response = await fetch(endpoint, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${window.sessionStorage.getItem('auth')}`
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(await response.text() || `Failed to update ${endpoint}`);
        }

        return response.json();
    }

    static async deleteAuthenticated(endpoint) {
        const response = await fetch(endpoint, {
            method: 'DELETE',
            headers: {
                'Authorization': `Basic ${window.sessionStorage.getItem('auth')}`
            }
        });

        if (!response.ok) {
            throw new Error(await response.text() || `Failed to delete from ${endpoint}`);
        }

        return response.json();
    }
}
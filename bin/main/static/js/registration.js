import { AccountService } from './services/accountService.js';
import Toast from './components/Toast.js';

document.getElementById('registrationForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        // Register the user
        await AccountService.register(name, username, password);

        // Create Basic Auth token
        const basicAuth = btoa(`${username}:${password}`);
        
        // Store the credentials
        localStorage.setItem('auth', `Basic ${basicAuth}`);
        
        // Redirect to main page
        window.location.href = '/';
        
    } catch (error) {
        console.error('Registration error:', error);
        Toast.show(error.message, true);
    }
}); 
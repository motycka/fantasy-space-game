import AccountService from './services/accountService.js';
import './components/CharactersTab.js';
import './components/MatchesTab.js';
import './components/LeaderboardTab.js';
import Toast from './components/Toast.js';

// Load user account data
async function loadUserAccount() {
    try {
        const account = await AccountService.getUserAccount();
        console.log('User account loaded:', account);
        
        // Update UI with account info if needed
        const userElement = document.getElementById('currentUser');
        if (userElement && account.username) {
            userElement.textContent = account.username;
        }
    } catch (error) {
        console.error('Error loading user account:', error);
        // Redirect to login if unauthorized
        if (error.status === 401) {
            window.location.href = '/login.html?authError=true';
        }
    }
}

// Initialize tabs when document is ready
document.addEventListener('DOMContentLoaded', () => {
    // Load user account data
    loadUserAccount();

    // Initialize characters tab content
    const charactersTabContent = document.getElementById('characters-tab-content');
    if (charactersTabContent) {
        const charactersTab = document.createElement('characters-tab');
        charactersTabContent.appendChild(charactersTab);
    }
});

// Handle logout
window.handleLogout = () => {
    // Clear session storage
    window.sessionStorage.removeItem('auth');
    
    // Clear all storage and caches
    resetUserContext();
    
    // Redirect to login
    window.location.href = '/login.html';
};

// Reset user context
export function resetUserContext() {
    window.sessionStorage.clear();
    window.localStorage.clear();
    if (window.caches) {
        caches.keys().then(names => {
            names.forEach(name => {
                caches.delete(name);
            });
        });
    }
} 
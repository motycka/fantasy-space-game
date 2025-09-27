import AccountService from './services/accountService.js';
import './components/Characters.js';
import './components/Matches.js';
import './components/Leaderboard.js';

// Check authentication before initializing
function checkAuthentication() {
    const auth = window.sessionStorage.getItem('auth');
    if (!auth) {
        console.log('No authentication found, redirecting to login');
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// Initialize tabs when document is ready
document.addEventListener('DOMContentLoaded', () => {
    if (!checkAuthentication()) {
        return;
    }

    const tabSelector = '[data-bs-toggle="tab"]';

    // Load user account data
    loadUserAccount().then(
        () => console.log('User account loaded successfully'),
        (error) => console.error('Failed to load user account:', error)
    )

    // Load content for a specific tab
    const loadTabContent = async (id) => {
        console.log('Loading content for tab:', id);
        const tabPane = document.querySelector(id);
        if (!tabPane) {
            console.error('Tab pane not found:', id);
            return;
        }

        // Find the component inside the tab pane
        const componentName = id.substring(1) + '-tab';  // e.g., '#matches' -> 'matches-tab'
        const component = tabPane.querySelector(componentName);
        console.log('Found component:', componentName, component);

        if (component && typeof component.load === 'function') {
            await component.load();
        } else {
            console.error('Tab component or load method not found:', componentName);
        }
    };

    // Add event listeners for tab changes
    document.querySelectorAll(tabSelector).forEach(tabEl => {
        console.log('Registering event listener for ', tabEl);
        tabEl.addEventListener('shown.bs.tab', async (event) => {
            console.log('event', event.target);
            const newTabId = event.target.getAttribute('href');
            console.log('newTabId', newTabId);
            await loadTabContent(newTabId);
        });
    });

    // loadTabContent(TABS.CHARACTERS.tabId);

    // Load initial active tab content
    // loadTabContent('#characters');
    const activeTabLink = document.querySelector(tabSelector);
    if (activeTabLink) {
        const initialTabId = activeTabLink.getAttribute('href');
        loadTabContent(initialTabId).then(
            () => console.log('Initial tab content loaded successfully'),
            (error) => console.error('Failed to load initial tab content:', error)
        )
    }
});

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

// Global error handler for unhandled promise rejections (e.g., API 401 errors)
window.addEventListener('unhandledrejection', event => {
    console.error('Unhandled promise rejection:', event.reason);
    if (event.reason?.status === 401) {
        console.log('401 error detected, redirecting to login');
        // Clear auth token and redirect to login with error flag
        window.sessionStorage.removeItem('auth');
        window.location.href = '/login.html?authError=true';
    }
});

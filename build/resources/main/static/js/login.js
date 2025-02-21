import Toast from './components/Toast.js';

// Reset user context
function resetUserContext() {
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

// Show login error
function showLoginError() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('authError')) {
        Toast.show('Your session has expired. Please log in again.', true);
    }
}

// Handle form submission
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const submitButton = event.target.querySelector('button[type="submit"]');
    
    try {
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Entering...';

        // Store auth token directly - no API call needed for Basic Auth
        const auth = btoa(`${username}:${password}`);
        window.sessionStorage.setItem('auth', auth);

        // Redirect to main page
        window.location.href = '/';
    } catch (error) {
        console.error('Login failed:', error);
        Toast.show('Login failed', true);
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="fas fa-portal-enter"></i> Dare to Enter';
    }
}

// Initialize login page
document.addEventListener('DOMContentLoaded', () => {
    // Reset any existing user context
    resetUserContext();
    
    // Show error if redirected due to auth failure
    showLoginError();
    
    // Add form submit handler
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

window.addEventListener('unhandledrejection', event => {
    if (event.reason?.status === 401) {
        // Clear auth token and redirect to login with error flag
        window.sessionStorage.removeItem('auth');
        window.location.href = '/login?authError=true';
    }
});

function showError(message) {
    const form = document.getElementById('loginForm');

    // Remove any existing error messages
    const existingError = form.querySelector('.login-error');
    if (existingError) {
        existingError.remove();
    }

    // Create and add new error message
    const errorDiv = document.createElement('div');
    errorDiv.className = 'login-error mt-3 text-center';
    errorDiv.innerHTML = `
        <i class="fas fa-exclamation-circle"></i>
        <span>${message}</span>
    `;
    form.appendChild(errorDiv);
}

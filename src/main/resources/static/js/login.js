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

    if (!username || !password) {
        showError('Please enter both username and password');
        return;
    }

    const submitButton = event.target.querySelector('button[type="submit"]');

    try {
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Entering...';

        // Validate credentials by making an API call
        const auth = btoa(`${username}:${password}`);

        // Test the credentials by trying to fetch user account
        const response = await fetch('/api/accounts', {
            headers: {
                'Authorization': `Basic ${auth}`
            }
        });

        if (!response.ok) {
            throw new Error('Invalid username or password');
        }

        // Store auth token only if validation succeeds
        window.sessionStorage.setItem('auth', auth);

        // Redirect to main page
        window.location.href = '/';
    } catch (error) {
        const message = error.message || 'Login failed';
        Toast.show(message, true);
        showError(message);

        // Re-enable button only on error
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="fas fa-portal-enter"></i> Dare to Enter';
    }
}

// Initialize login page
document.addEventListener('DOMContentLoaded', () => {
    resetUserContext();
    showLoginError();

    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', handleLogin);

    // // Also add a click handler to the submit button as backup
    // loginForm.querySelector('button[type="submit"]')
    //     .addEventListener('click', (event) => {
    //         console.log('Submit button clicked');
    //         // Let the form submission handle the rest
    //     });
});

window.addEventListener('unhandledrejection', event => {
    if (event.reason?.status === 401) {
        window.sessionStorage.removeItem('auth');
        window.location.href = '/login?authError=true';
    }
});

function showError(message) {
    const form = document.getElementById('loginForm');

    const existingError = form.querySelector('.login-error');
    if (existingError) {
        existingError.remove();
    }

    const errorDiv = document.createElement('div');
    errorDiv.className = 'login-error mt-3 text-center';
    errorDiv.innerHTML = `
        <i class="fas fa-exclamation-circle"></i>
        <span>${message}</span>
    `;
    form.appendChild(errorDiv);
}

export default class ErrorState {
    static render(message, type = 'error') {
        return `
            <div class="error-state">
                <div class="error-icon">
                    <i class="fas fa-exclamation-circle fa-3x"></i>
                </div>
                <div class="error-message">
                    <h5>Failed to load ${type}</h5>
                    <p>${message}</p>
                    <button class="btn btn-cosmic-outline btn-sm mt-3" onclick="window.location.reload()">
                        <i class="fas fa-sync-alt"></i> Try Again
                    </button>
                </div>
            </div>
        `;
    }
} 
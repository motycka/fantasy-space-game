import ErrorState from './ErrorState.js';
import Toast from './Toast.js';
import LeaderboardService from '../services/leaderboardService.js';
import './LeaderboardTable.js';
import './EmptyState.js';

export default class Leaderboard extends HTMLElement {
    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
    }

    initialize() {
        console.log('Initializing leaderboard tab...');
        this.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="mb-0">Leaderboard</h4>
            </div>
            <div id="leaderboardContent">
                <empty-state
                    icon="fa-trophy"
                    message="No rankings yet"
                    description="Be the first to join the leaderboard!"
                ></empty-state>
            </div>
        `;
    }

    async load() {
        const contentContainer = this.querySelector('#leaderboardContent');
        try {
            contentContainer.innerHTML = '<div class="loading">Loading rankings...</div>';
            
            const rankings = await LeaderboardService.getLeaderboard();
            
            if (!rankings || rankings.length === 0) {
                contentContainer.innerHTML = `
                    <empty-state
                        icon="fa-trophy"
                        message="No rankings yet"
                        description="Be the first to join the leaderboard!"
                    ></empty-state>
                `;
                return;
            }

            contentContainer.innerHTML = '<leaderboard-table></leaderboard-table>';
            const leaderboardTable = this.querySelector('leaderboard-table');
            leaderboardTable.displayRankings(rankings);

        } catch (error) {
            console.error('Error loading leaderboard:', error);
            contentContainer.innerHTML = ErrorState.render(
                error.message || 'Unable to load leaderboard. Please try again later.',
                'leaderboard'
            );
            Toast.show('Failed to load leaderboard: ' + error.message, true);
        }
    }
}

// Register the custom element
customElements.define('leaderboard-tab', Leaderboard); 
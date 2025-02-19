import ErrorState from './ErrorState.js';
import Toast from './Toast.js';
import LeaderboardService from '../services/leaderboardService.js';

export default class LeaderboardTab extends HTMLElement {
    constructor() {
        super();
        this.rankings = [];
        this.currentFilter = 'ALL';
    }

    connectedCallback() {
        this.initialize();
    }

    initialize() {
        console.log('Initializing leaderboard tab...');

        // Create initial structure with cosmic theme
        this.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="mb-0">Leaderboard</h4>
            </div>
            <div class="leaderboard-container">
                <div class="table-responsive">
                    <table class="table cosmic-table">
                        <thead>
                            <tr>
                                <th class="text-center" style="width: 80px;">Rank</th>
                                <th>Player</th>
                                <th class="text-end" style="width: 120px;">Score</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="3" class="text-center">
                                    <div class="loading">Loading leaderboard...</div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        `;

        // Add event listener for tab shown
        const leaderboardTab = document.getElementById('leaderboard-tab');
        if (leaderboardTab) {
            leaderboardTab.addEventListener('shown.bs.tab', () => {
                console.log('Leaderboard tab shown, loading rankings...');
                this.loadLeaderboard();
            });
        }

        // Load leaderboard initially if this tab is active
        if (this.closest('.tab-pane.active')) {
            this.loadLeaderboard();
        }
    }

    async loadLeaderboard() {
        try {
            const tableBody = this.querySelector('tbody');
            if (!tableBody) {
                throw new Error('Leaderboard table not found');
            }

            tableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="text-center">
                        <div class="loading">Loading leaderboard...</div>
                    </td>
                </tr>
            `;

            const data = await LeaderboardService.getLeaderboard();
            this.displayLeaderboard(data);
        } catch (error) {
            console.error('Error loading leaderboard:', error);
            const tableBody = this.querySelector('tbody');
            if (tableBody) {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="3" class="text-center">
                            ${ErrorState.render(
                                error.message || 'Unable to load leaderboard. Please try again later.',
                                'leaderboard'
                            )}
                        </td>
                    </tr>
                `;
            }
            Toast.show('Failed to load leaderboard: ' + error.message, true);
        }
    }

    displayLeaderboard(rankings) {
        const tableBody = this.querySelector('tbody');
        if (!rankings || rankings.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="text-center">
                        <div class="empty-state">
                            <i class="fas fa-trophy fa-3x mb-3"></i>
                            <p>No rankings yet</p>
                            <small>Be the first to join the leaderboard!</small>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = rankings.map((entry, index) => `
            <tr>
                <td class="text-center">
                    ${this.getRankDisplay(index + 1)}
                </td>
                <td>
                    <i class="fas fa-user-astronaut me-2"></i>
                    ${entry.playerName}
                </td>
                <td class="text-end">
                    <span class="badge bg-cosmic">
                        ${entry.score} pts
                    </span>
                </td>
            </tr>
        `).join('');
    }

    getRankDisplay(rank) {
        const medal = rank <= 3 ? this.getMedalIcon(rank) : rank;
        return `<span class="rank-display">${medal}</span>`;
    }

    getMedalIcon(rank) {
        const medals = {
            1: '🥇',
            2: '🥈',
            3: '🥉'
        };
        return medals[rank] || rank;
    }

    displayError(message) {
        const tableBody = this.querySelector('tbody');
        if (tableBody) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="text-center">
                        ${ErrorState.render(message, 'leaderboard')}
                    </td>
                </tr>
            `;
        }
    }
}

// Register the custom element
customElements.define('leaderboard-tab', LeaderboardTab); 
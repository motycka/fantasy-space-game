import { LevelBadge } from './LevelBadge.js';

export default class LeaderboardTable extends HTMLElement {
    constructor() {
        super();
    }

    connectedCallback() {
        this.render();
    }

    render() {
        this.innerHTML = `
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th class="text-center" style="width: 80px">Rank</th>
                            <th>Player</th>
                            <th class="text-end" style="width: 120px">Score</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="3" class="text-center">
                                <div class="loading">Loading rankings...</div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        `;
    }

    displayRankings(rankings) {
        const tableBody = this.querySelector('tbody');
        tableBody.innerHTML = rankings.map((entry, index) => `
            <tr>
                <td class="text-center">
                    ${this.getRankDisplay(index + 1)}
                </td>
                <td>
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-astronaut me-2"></i>
                        <div>
                            <div>${entry.playerName}</div>
                            ${LevelBadge.render(entry.level)}
                        </div>
                    </div>
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
}

customElements.define('leaderboard-table', LeaderboardTable); 
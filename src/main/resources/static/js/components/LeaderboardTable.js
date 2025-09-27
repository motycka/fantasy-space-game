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
                <table class="table cosmic-table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Player</th>
                            <th>Level</th>
                            <th>Experience</th>
                            <th>Victories</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="5" class="text-center">
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
        tableBody.innerHTML = rankings.map((rank, index) => `
            <tr>
                <td>${this.getRankDisplay(index + 1)}</td>
                <td>
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-astronaut me-2"></i>
                        <div>
                            <div>${rank.name}</div>
                            ${LevelBadge.render(rank.level)}
                        </div>
                    </div>
                </td>
                <td>${rank.level}</td>
                <td>${rank.experience} XP</td>
                <td>${rank.victories}</td>
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
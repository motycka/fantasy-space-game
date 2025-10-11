import ErrorState from './ErrorState.js';
import MatchService from '../services/matchService.js';
import Toast from './Toast.js';
import MatchDialog from './MatchDialog.js';
import './EmptyState.js';
import CharacterService from '../services/characterService.js';

export default class Matches extends HTMLElement {
    constructor() {
        super();
        this.matchDialog = null;
        this.matchResultModal = null;
        this.characters = [];
        this.matches = [];
    }

    connectedCallback() {
        this.initialize();
    }

    initialize() {
        console.log('Initializing matches tab...');

        // Create initial structure
        this.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="mb-0">Match History</h4>
                <button id="newMatchBtn" class="btn btn-cosmic">
                    <i class="fas fa-swords"></i> New Match
                </button>
            </div>
            <div id="matchesList" class="matches-list">
                <empty-state
                    icon="fa-swords"
                    message="No matches yet"
                    description="Challenge someone to start your battle history!"
                ></empty-state>
            </div>
            <match-dialog></match-dialog>
        `;

        // Initialize match dialog
        this.matchDialog = this.querySelector('match-dialog');
        this.querySelector('#newMatchBtn').addEventListener('click', () => {
            this.matchDialog.show(async (match) => {
                await this.loadMatches();
                Toast.show('Match completed successfully!');
            });
        });
    }

    async load() {
        try {
            // Load both characters and matches
            const [characters, matches] = await Promise.all([
                CharacterService.getCharacters(),
                MatchService.getMatches()
            ]);
            
            this.characters = characters;
            this.matches = matches;
            
            this.updateCharacterSelects();
            this.displayMatches(matches);
        } catch (error) {
            console.error('Error loading matches data:', error);
            Toast.show('Failed to load matches data: ' + error.message, true);
        }
    }

    updateCharacterSelects() {
        if (!this.characters || !Array.isArray(this.characters)) {
            console.warn('No characters available for select options');
            return;
        }

        const challengerSelect = document.getElementById('challengerSelect');
        const opponentSelect = document.getElementById('opponentSelect');

        if (challengerSelect && opponentSelect) {
            // Filter owned characters for challenger
            const ownedCharacters = this.characters.filter(char => char.isOwner);
            const challengerOptions = ownedCharacters.map(char =>
                `<option value="${char.id}">${char.name} (Level ${char.level})</option>`
            ).join('');

            // Filter non-owned characters for opponent
            const opponentCharacters = this.characters.filter(char => !char.isOwner);
            const opponentOptions = opponentCharacters.map(char =>
                `<option value="${char.id}">${char.name} (Level ${char.level})</option>`
            ).join('');

            challengerSelect.innerHTML = '<option value="">Select challenger...</option>' +
                (challengerOptions || '<option disabled>No owned characters available</option>');
            opponentSelect.innerHTML = '<option value="">Select opponent...</option>' +
                (opponentOptions || '<option disabled>No opponent characters available</option>');
        }
    }

    showNewMatchModal() {
        if (this.matchDialog) {
            this.matchDialog.show(async (match) => {
                await this.loadMatches();
                Toast.show('Match completed successfully!');
            });
        }
    }

    updateFightButton() {
        const challengerId = document.getElementById('challengerSelect')?.value;
        const opponentId = document.getElementById('opponentSelect')?.value;
        const fightButton = document.getElementById('fightButton');

        if (fightButton) {
            fightButton.disabled = !challengerId || !opponentId || challengerId === opponentId;
        }
    }

    async handleRandomMatch() {
        if (this.characters.length < 2) {
            Toast.show('Not enough characters for a random match', true);
            return;
        }

        const randomIndices = this.getRandomPair(this.characters.length);
        const challengerSelect = document.getElementById('challengerSelect');
        const opponentSelect = document.getElementById('opponentSelect');

        if (challengerSelect && opponentSelect) {
            challengerSelect.selectedIndex = randomIndices[0] + 1;
            opponentSelect.selectedIndex = randomIndices[1] + 1;
            this.updateFightButton();
        }
    }

    getRandomPair(max) {
        const first = Math.floor(Math.random() * max);
        let second;
        do {
            second = Math.floor(Math.random() * max);
        } while (second === first);
        return [first, second];
    }

    async handleFight() {
        try {
            const challengerId = document.getElementById('challengerSelect')?.value;
            const opponentId = document.getElementById('opponentSelect')?.value;
            const rounds = document.getElementById('roundsSelect')?.value;

            if (!challengerId || !opponentId || !rounds) {
                throw new Error('Please select both characters and number of rounds');
            }

            const match = await MatchService.createMatch(challengerId, opponentId, rounds);

            // Show match result
            this.displayMatchResult(match);
            this.matchDialog.hide();
            
            // Reload matches list
            await this.loadMatches();
            Toast.show('Match completed successfully!');
        } catch (error) {
            console.error('Error during fight:', error);
            Toast.show('Fight failed: ' + error.message, true);
        }
    }

    async loadMatches() {
        const listContainer = this.querySelector('#matchesList');
        try {
            listContainer.innerHTML = '<div class="loading">Loading matches...</div>';

            const matches = await MatchService.getMatches();
            this.displayMatches(matches);
        } catch (error) {
            console.error('Error loading matches:', error);
            listContainer.innerHTML = ErrorState.render(
                error.message || 'Unable to load matches. Please try again later.',
                'matches'
            );
            Toast.show('Failed to load matches: ' + error.message, true);
        }
    }

    displayMatches(matches) {
        const listContainer = this.querySelector('#matchesList');
        
        if (!matches || matches.length === 0) {
            listContainer.innerHTML = `
                <empty-state
                    icon="fa-swords"
                    message="No matches yet"
                    description="Challenge someone to start your battle history!"
                ></empty-state>
            `;
            return;
        }

        listContainer.innerHTML = `
            <div class="table-responsive">
                <table class="table cosmic-table">
                    <thead>
                        <tr>
                            <th>Challenger</th>
                            <th>VS</th>
                            <th>Opponent</th>
                            <th>Result</th>
                            <th>Experience</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${matches.map(match => this.createMatchRow(match)).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }

    createMatchRow(match) {
        return `
            <tr class="match-row">
                <td>${match.challenger.name}</td>
                <td class="text-center">⚔️</td>
                <td>${match.opponent.name}</td>
                <td>${this.getResultBadge(match)}</td>
                <td>${this.getExperienceGained(match)}</td>
            </tr>
        `;
    }

    getResultBadge(match) {
        // Check matchOutcome field instead of winner name
        switch (match.matchOutcome) {
            case 'CHALLENGER_WON':
                return '<span class="match-result-icon match-victory" title="Victory"><i class="fas fa-trophy"></i></span>';
            case 'OPPONENT_WON':
                return '<span class="match-result-icon match-defeat" title="Defeat"><i class="fas fa-skull"></i></span>';
            case 'DRAW':
                return '<span class="match-result-icon match-draw" title="Draw"><i class="fas fa-handshake"></i></span>';
            default:
                // Fallback to old logic if matchOutcome is not available
                if (match.winner === match.challenger.name) {
                    return '<span class="match-result-icon match-victory" title="Victory"><i class="fas fa-trophy"></i></span>';
                }
                return '<span class="match-result-icon match-defeat" title="Defeat"><i class="fas fa-skull"></i></span>';
        }
    }

    getExperienceGained(match) {
        // Experience is stored per character, show challenger's experience
        const exp = match.challenger?.experienceGained || 0;
        return `<span class="experience-gained">+${exp} XP</span>`;
    }

    displayError(message) {
        const listContainer = this.querySelector('#matchesList');
        if (listContainer) {
            listContainer.innerHTML = ErrorState.render(message, 'matches');
        }
    }

    displayMatchResult(match) {
        // Update challenger info
        const challengerName = document.querySelector('.challenger-name');
        const challengerResult = document.querySelector('.challenger-result');
        const challengerStats = document.querySelector('.challenger-stats');
        
        if (challengerName) challengerName.textContent = match.challenger.name;
        if (challengerResult) {
            // Handle all match outcomes including DRAW
            switch (match.matchOutcome) {
                case 'CHALLENGER_WON':
                    challengerResult.className = 'match-result-icon match-victory';
                    challengerResult.title = 'Victory';
                    challengerResult.innerHTML = '<i class="fas fa-trophy"></i>';
                    break;
                case 'OPPONENT_WON':
                    challengerResult.className = 'match-result-icon match-defeat';
                    challengerResult.title = 'Defeat';
                    challengerResult.innerHTML = '<i class="fas fa-skull"></i>';
                    break;
                case 'DRAW':
                    challengerResult.className = 'match-result-icon match-draw';
                    challengerResult.title = 'Draw';
                    challengerResult.innerHTML = '<i class="fas fa-handshake"></i>';
                    break;
                default:
                    // Fallback to old logic
                    challengerResult.className = 'match-result-icon ' + (match.winner === match.challenger.name ? 'match-victory' : 'match-defeat');
                    challengerResult.title = match.winner === match.challenger.name ? 'Victory' : 'Defeat';
                    challengerResult.innerHTML = match.winner === match.challenger.name ? '<i class="fas fa-trophy"></i>' : '<i class="fas fa-skull"></i>';
            }
        }
        if (challengerStats) {
            challengerStats.innerHTML = this.createStatsDisplay(match.challenger);
        }

        // Update opponent info
        const opponentName = document.querySelector('.opponent-name');
        const opponentResult = document.querySelector('.opponent-result');
        const opponentStats = document.querySelector('.opponent-stats');

        if (opponentName) opponentName.textContent = match.opponent.name;
        if (opponentResult) {
            // Handle all match outcomes including DRAW
            switch (match.matchOutcome) {
                case 'CHALLENGER_WON':
                    opponentResult.className = 'match-result-icon match-defeat';
                    opponentResult.title = 'Defeat';
                    opponentResult.innerHTML = '<i class="fas fa-skull"></i>';
                    break;
                case 'OPPONENT_WON':
                    opponentResult.className = 'match-result-icon match-victory';
                    opponentResult.title = 'Victory';
                    opponentResult.innerHTML = '<i class="fas fa-trophy"></i>';
                    break;
                case 'DRAW':
                    opponentResult.className = 'match-result-icon match-draw';
                    opponentResult.title = 'Draw';
                    opponentResult.innerHTML = '<i class="fas fa-handshake"></i>';
                    break;
                default:
                    // Fallback to old logic
                    opponentResult.className = 'match-result-icon ' + (match.winner === match.opponent.name ? 'match-victory' : 'match-defeat');
                    opponentResult.title = match.winner === match.opponent.name ? 'Victory' : 'Defeat';
                    opponentResult.innerHTML = match.winner === match.opponent.name ? '<i class="fas fa-trophy"></i>' : '<i class="fas fa-skull"></i>';
            }
        }
        if (opponentStats) {
            opponentStats.innerHTML = this.createStatsDisplay(match.opponent);
        }

        // Display rounds timeline
        const roundsList = document.querySelector('.rounds-list');
        if (roundsList) {
            roundsList.innerHTML = this.createRoundsTimeline(match.rounds);
        }

        // Show the result modal
        this.matchResultModal.show();
    }

    createStatsDisplay(character) {
        return `
            <div class="character-properties">
                <div class="property-item" data-property="health">
                    <div class="property-label">Health</div>
                    <div class="property-value">${character.health}</div>
                </div>
                <div class="property-item" data-property="attackPower">
                    <div class="property-label">Attack</div>
                    <div class="property-value">${character.attackPower}</div>
                </div>
                <div class="property-item" data-property="defensePower">
                    <div class="property-label">Defense</div>
                    <div class="property-value">${character.defensePower}</div>
                </div>
                <div class="property-item" data-property="experience">
                    <div class="property-label">Experience</div>
                    <div class="property-value">+${character.experienceGained}</div>
                </div>
            </div>
        `;
    }

    createRoundsTimeline(rounds) {
        if (!rounds || !Array.isArray(rounds)) {
            console.warn('No rounds data available:', rounds);
            return `
                <div class="empty-state">
                    <p>No rounds data available</p>
                </div>
            `;
        }

        return rounds.map((round, index) => {
            // Check if round has the expected structure
            if (!round || !round.actions) {
                console.warn('Invalid round data:', round);
                return '';
            }

            return `
                <div class="round-item mb-3">
                    <div class="round-header mb-2">
                        <strong>Round ${index + 1}</strong>
                    </div>
                    <div class="round-actions">
                        ${Array.isArray(round.actions) ? round.actions.map(action => `
                            <div class="action-item">
                                <span class="action-character">${action.character || 'Unknown'}:</span>
                                <span class="action-description">${action.description || ''}</span>
                            </div>
                        `).join('') : ''}
                    </div>
                </div>
            `;
        }).join('');
    }
}

// Register the custom element
// customElements.define('matches-tab', MatchesTab); 
customElements.define('matches-tab', Matches); 
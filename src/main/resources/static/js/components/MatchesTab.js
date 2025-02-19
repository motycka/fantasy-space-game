import { COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import { formatLevel } from '../characters.js';
import ErrorState from './ErrorState.js';
import CharacterService from '../services/characterService.js';
import MatchService from '../services/matchService.js';
import Toast from './Toast.js';

export default class MatchesTab extends HTMLElement {
    constructor() {
        super();
        this.matchModal = null;
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
                <button id="newMatchBtn" class="btn btn-cosmic" data-bs-toggle="modal" data-bs-target="#newMatchModal">
                    <i class="fas fa-swords"></i> New Match
                </button>
            </div>
            <div id="matchesList" class="matches-list">
                <div class="loading">Loading matches...</div>
            </div>
        `;

        // Initialize modals after ensuring they exist
        this.initializeModals();

        // Add event listener for tab shown
        document.getElementById('matches-tab').addEventListener('shown.bs.tab', () => {
            console.log('Matches tab shown, loading matches...');
            this.loadMatches();
        });

        // Load matches initially
        this.loadMatches();
        
        // Load characters for the match modal
        this.loadCharacters();
    }

    initializeModals() {
        // Initialize modals
        this.matchModal = new bootstrap.Modal(document.getElementById('newMatchModal'));
        this.matchResultModal = new bootstrap.Modal(document.getElementById('matchResultModal'));

        // Add event listeners
        const newMatchBtn = document.getElementById('newMatchBtn');
        if (newMatchBtn) {
            newMatchBtn.addEventListener('click', () => this.showNewMatchModal());
        }

        const randomMatchBtn = document.getElementById('randomMatchButton');
        if (randomMatchBtn) {
            randomMatchBtn.addEventListener('click', () => this.handleRandomMatch());
        }

        const fightBtn = document.getElementById('fightButton');
        if (fightBtn) {
            fightBtn.addEventListener('click', () => this.handleFight());
        }

        // Add change listeners for character selects
        const challengerSelect = document.getElementById('challengerSelect');
        const opponentSelect = document.getElementById('opponentSelect');
        
        if (challengerSelect) {
            challengerSelect.addEventListener('change', () => this.updateFightButton());
        }
        if (opponentSelect) {
            opponentSelect.addEventListener('change', () => this.updateFightButton());
        }
    }

    async loadCharacters() {
        try {
            const response = await CharacterService.getCharacters();
            if (!response.ok) {
                throw new Error(await response.text() || 'Failed to load characters');
            }
            this.characters = await response.json();
            this.updateCharacterSelects();
        } catch (error) {
            console.error('Error loading characters:', error);
            Toast.show('Failed to load characters: ' + error.message, true);
        }
    }

    updateCharacterSelects() {
        const challengerSelect = document.getElementById('challengerSelect');
        const opponentSelect = document.getElementById('opponentSelect');

        if (challengerSelect && opponentSelect) {
            const options = this.characters.map(char => 
                `<option value="${char.id}">${char.name} (Level ${char.level})</option>`
            ).join('');

            challengerSelect.innerHTML = '<option value="">Select challenger...</option>' + options;
            opponentSelect.innerHTML = '<option value="">Select opponent...</option>' + options;
        }
    }

    showNewMatchModal() {
        if (this.matchModal) {
            this.matchModal.show();
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

            const response = await MatchService.createMatch(challengerId, opponentId, rounds);
            if (!response.ok) {
                throw new Error(await response.text() || 'Failed to create match');
            }
            const match = await response.json();

            // Show match result
            this.displayMatchResult(match);
            this.matchModal.hide();
            
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
                <div class="empty-state">
                    <i class="fas fa-swords fa-3x mb-3"></i>
                    <p>No matches found</p>
                    <small>Start a new match to begin your journey</small>
                </div>
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
        if (match.winner === match.challenger.name) {
            return '<span class="badge bg-success">Victory</span>';
        }
        return '<span class="badge bg-danger">Defeat</span>';
    }

    getExperienceGained(match) {
        return `<span class="experience-gained">+${match.experienceGained} XP</span>`;
    }

    displayError(message) {
        const listContainer = this.querySelector('#matchesList');
        if (listContainer) {
            listContainer.innerHTML = ErrorState.render(message, 'matches');
        }
    }
}

// Register the custom element
customElements.define('matches-tab', MatchesTab); 
customElements.define('matches-tab', MatchesTab); 
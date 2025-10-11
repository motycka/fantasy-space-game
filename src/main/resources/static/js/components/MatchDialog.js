import CharacterService from '../services/characterService.js';
import MatchService from '../services/matchService.js';
import Toast from './Toast.js';
import BaseDialog from './BaseDialog.js';
import { getClassIcon } from '../utils/formatters.js';

export default class MatchDialog extends BaseDialog {
    constructor() {
        super();
        this.challengers = [];
        this.opponents = [];
        this.onMatchCreated = null;
    }

    getModalId() {
        return 'newMatchModal';
    }

    getModalTitle() {
        return '<i class="fas fa-swords"></i> New Match';
    }

    getModalSize() {
        return 'modal-lg';
    }

    /**
     * Helper to get the modal element (now in body, not in this custom element)
     */
    getModalElement() {
        return document.getElementById(this.getModalId());
    }

    renderBody() {
        return `
            <div class="row g-4">
                <!-- Challenger Selection -->
                <div class="col-md-6">
                    <div class="card h-100">
                        <div class="card-header">
                            <h6><i class="fas fa-user-shield"></i> Challenger</h6>
                        </div>
                        <div class="card-body">
                            <select id="challengerSelect" class="form-select mb-3">
                                <option value="">Choose your challenger...</option>
                            </select>
                            <div id="challengerStats" class="character-properties"></div>
                        </div>
                    </div>
                </div>

                <!-- Opponent Selection -->
                <div class="col-md-6">
                    <div class="card h-100">
                        <div class="card-header">
                            <h6><i class="fas fa-skull-crossbones"></i> Opponent</h6>
                        </div>
                        <div class="card-body">
                            <select id="opponentSelect" class="form-select mb-3">
                                <option value="">Choose your opponent...</option>
                            </select>
                            <div id="opponentStats" class="character-properties"></div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    renderFooter() {
        return `
            <div class="d-flex align-items-center">
                <label for="roundsSelect" class="me-2"><i class="fas fa-redo"></i> Rounds:</label>
                <select id="roundsSelect" class="form-select form-select-sm" style="width: auto;">
                    <option value="10">10</option>
                    <option value="20" selected>20</option>
                    <option value="30">30</option>
                    <option value="50">50</option>
                </select>
            </div>
            <div class="ms-auto">
                <button id="randomMatchButton" class="btn btn-cosmic-outline">
                    <i class="fas fa-random"></i> Random Match
                </button>
                <button id="fightButton" class="btn btn-cosmic" disabled>
                    <i class="fas fa-swords"></i> Fight!
                </button>
            </div>
        `;
    }

    initialize() {
        const modal = this.getModalElement();
        if (!modal) {
            console.error('Modal element not found for initialization');
            return;
        }

        // Add event listeners
        modal.querySelector('#randomMatchButton')?.addEventListener('click', () => this.handleRandomMatch());
        modal.querySelector('#fightButton')?.addEventListener('click', () => this.handleFight());
        modal.querySelector('#challengerSelect')?.addEventListener('change', () => this.updateFightButton());
        modal.querySelector('#opponentSelect')?.addEventListener('change', () => this.updateFightButton());
    }

    show(onMatchCreated) {
        this.onMatchCreated = onMatchCreated;
        this.loadCharacters();
        super.show();
    }

    async loadCharacters() {
        try {
            // Load challengers and opponents using separate endpoints
            const [challengers, opponents] = await Promise.all([
                CharacterService.getChallengers(),
                CharacterService.getOpponents()
            ]);
            
            this.challengers = challengers;
            this.opponents = opponents;
            
            this.updateCharacterSelects();
        } catch (error) {
            console.error('Error loading characters:', error);
            Toast.show('Failed to load characters: ' + error.message, true);
        }
    }

    updateCharacterSelects() {
        const modal = this.getModalElement();
        if (!modal) return;

        const challengerSelect = modal.querySelector('#challengerSelect');
        const opponentSelect = modal.querySelector('#opponentSelect');

        if (challengerSelect) {
            challengerSelect.innerHTML = `
                <option value="">Choose your challenger...</option>
                ${this.challengers.map(char => `
                    <option value="${char.id}" data-class="${char.characterClass}">
                        ${getClassIcon(char.characterClass)}
                        ${char.name} (Level ${char.level})
                    </option>
                `).join('')}
            `;
        }

        if (opponentSelect) {
            opponentSelect.innerHTML = `
                <option value="">Choose your opponent...</option>
                ${this.opponents.map(char => `
                    <option value="${char.id}" data-class="${char.characterClass}">
                        ${getClassIcon(char.characterClass)}
                        ${char.name} (Level ${char.level})
                    </option>
                `).join('')}
            `;
        }

        this.updateFightButton();
    }

    handleRandomMatch() {
        if (!this.challengers.length || !this.opponents.length) {
            Toast.show('Not enough characters for a random match', true);
            return;
        }

        const modal = this.getModalElement();
        if (!modal) return;

        const challenger = this.challengers[Math.floor(Math.random() * this.challengers.length)];
        const opponent = this.opponents[Math.floor(Math.random() * this.opponents.length)];

        const challengerSelect = modal.querySelector('#challengerSelect');
        const opponentSelect = modal.querySelector('#opponentSelect');

        if (challengerSelect && opponentSelect) {
            challengerSelect.value = challenger.id;
            opponentSelect.value = opponent.id;
            this.updateFightButton();
        }
    }

    handleFight() {
        const modal = this.getModalElement();
        if (!modal) return;

        const challengerId = modal.querySelector('#challengerSelect')?.value;
        const opponentId = modal.querySelector('#opponentSelect')?.value;
        const rounds = modal.querySelector('#roundsSelect')?.value || 20;

        if (!challengerId || !opponentId) {
            Toast.show('Please select both characters', true);
            return;
        }

        if (challengerId === opponentId) {
            Toast.show('A character cannot fight themselves', true);
            return;
        }

        MatchService.createMatch(challengerId, opponentId, rounds)
            .then(match => {
                if (this.onMatchCreated) {
                    this.onMatchCreated(match);
                }
                this.hide();
            })
            .catch(error => {
                console.error('Error during fight:', error);
                Toast.show(error.message || 'Fight failed', true);
            });
    }

    updateFightButton() {
        const modal = this.getModalElement();
        if (!modal) return;

        const challengerSelect = modal.querySelector('#challengerSelect');
        const opponentSelect = modal.querySelector('#opponentSelect');
        const fightButton = modal.querySelector('#fightButton');

        if (fightButton) {
            const challengerId = challengerSelect?.value;
            const opponentId = opponentSelect?.value;

            // Enable button only if both characters are selected and they're different
            const isValid = challengerId && opponentId && challengerId !== opponentId;
            fightButton.disabled = !isValid;
        }
    }
}

customElements.define('match-dialog', MatchDialog); 
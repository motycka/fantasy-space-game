// import CharacterDialog from './CharacterDialog.js';
import CharacterService from '../services/characterService.js';
import Toast from './Toast.js';
import ErrorState from './ErrorState.js';
import './CharacterCard.js';
import './CharacterDialog.js';
import './LevelUpDialog.js';
import './EmptyState.js';

export default class Characters extends HTMLElement {
    constructor() {
        super();
        this.showMyCharacters = true; // Default to showing user's own characters
        this.characterDialog = null;
        this.levelUpDialog = null;
    }

    connectedCallback() {
        this.initialize();
    }

    async initialize() {
        console.log('Initializing characters tab...');

        // Create tab content structure
        this.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h4 class="mb-2" id="charactersTitle">Your Characters</h4>
                    <div class="btn-group" role="group">
                        <button type="button" class="btn btn-cosmic-outline btn-sm" id="myCharactersBtn">
                            <i class="fas fa-user"></i> My Characters
                        </button>
                        <button type="button" class="btn btn-cosmic-outline btn-sm" id="opponentsBtn">
                            <i class="fas fa-skull-crossbones"></i> Opponents
                        </button>
                    </div>
                </div>
                <button id="createCharacterBtn" class="btn btn-cosmic">
                    <i class="fas fa-plus-circle"></i> Create Character
                </button>
            </div>
            <div id="charactersList" class="characters-grid">
                <div class="loading">Loading characters...</div>
            </div>
            <character-dialog></character-dialog>
            <level-up-dialog></level-up-dialog>
        `;

        // Get dialog references and store them
        this.characterDialog = this.querySelector('character-dialog');
        this.levelUpDialog = this.querySelector('level-up-dialog');

        // Add event listener for create character button
        const createCharacterBtn = this.querySelector('#createCharacterBtn');
        if (createCharacterBtn) {
            createCharacterBtn.addEventListener('click', () => {
                // Small delay to ensure modal is initialized
                setTimeout(() => {
                    this.characterDialog.show(async (characterData) => {
                        try {
                            await this.createCharacter(characterData);
                            await this.load();
                            Toast.show('Character created successfully!');
                        } catch (error) {
                            console.error('Error creating character:', error);
                            const message = error.message || 'Failed to create character';
                            Toast.show(message, true);
                            throw error;
                        }
                    });
                }, 100);
            });
        }

        // Add event listeners for view toggle buttons
        const myCharactersBtn = this.querySelector('#myCharactersBtn');
        const opponentsBtn = this.querySelector('#opponentsBtn');

        if (myCharactersBtn) {
            myCharactersBtn.addEventListener('click', () => {
                this.showMyCharacters = true;
                this.updateViewButtons();
                this.load();
            });
        }

        if (opponentsBtn) {
            opponentsBtn.addEventListener('click', () => {
                this.showMyCharacters = false;
                this.updateViewButtons();
                this.load();
            });
        }

        // Set initial button state
        this.updateViewButtons();

        // Load characters initially
        // await this.load();
    }

    updateViewButtons() {
        const myCharactersBtn = this.querySelector('#myCharactersBtn');
        const opponentsBtn = this.querySelector('#opponentsBtn');
        const title = this.querySelector('#charactersTitle');
        const createBtn = this.querySelector('#createCharacterBtn');

        if (myCharactersBtn && opponentsBtn) {
            if (this.showMyCharacters) {
                myCharactersBtn.classList.remove('btn-cosmic-outline');
                myCharactersBtn.classList.add('btn-cosmic');
                opponentsBtn.classList.remove('btn-cosmic');
                opponentsBtn.classList.add('btn-cosmic-outline');
                if (title) title.textContent = 'Your Characters';
                if (createBtn) createBtn.style.display = 'block';
            } else {
                myCharactersBtn.classList.remove('btn-cosmic');
                myCharactersBtn.classList.add('btn-cosmic-outline');
                opponentsBtn.classList.remove('btn-cosmic-outline');
                opponentsBtn.classList.add('btn-cosmic');
                if (title) title.textContent = 'Opponents';
                if (createBtn) createBtn.style.display = 'none';
            }
        }
    }

    async load() {
        console.log('Loading characters...');
        const listContainer = this.querySelector('#charactersList');
        try {
            listContainer.innerHTML = '<div class="loading">Loading characters...</div>';

            // Use dedicated API endpoints for filtering
            const characters = this.showMyCharacters
                ? await CharacterService.getChallengers()
                : await CharacterService.getOpponents();

            console.log('Characters loaded:', characters);
            this.displayCharacters(characters);
        } catch (error) {
            console.error('Error loading characters:', error);
            listContainer.innerHTML = ErrorState.render(
                error.message || 'Unable to load characters. Please try again later.',
                'characters'
            );
            Toast.show('Failed to load characters: ' + error.message, true);
        }
    }

    displayCharacters(characters) {
        console.log('Displaying characters:', characters);
        const listContainer = this.querySelector('#charactersList');
        listContainer.innerHTML = '';

        if (!characters || characters.length === 0) {
            const emptyIcon = this.showMyCharacters ? 'fa-user-astronaut' : 'fa-skull-crossbones';
            const emptyMessage = this.showMyCharacters
                ? 'Your cosmic journey begins here...'
                : 'No opponents available';
            const emptyDescription = this.showMyCharacters
                ? 'Create your first character to start your adventure'
                : 'No other players have created characters yet';

            listContainer.innerHTML = `
                <empty-state
                    icon="${emptyIcon}"
                    message="${emptyMessage}"
                    description="${emptyDescription}"
                ></empty-state>
            `;
            return;
        }

        characters.forEach(character => {
            const characterCard = document.createElement('character-card');
            characterCard.initialize(character, (char) => this.showLevelUpModal(char));
            listContainer.appendChild(characterCard);
        });
    }

    async createCharacter(characterData) {
        return CharacterService.createCharacter(characterData);
    }

    async showLevelUpModal(character) {
        console.log('Level up character:', character);

        // Show the dedicated level up dialog
        this.levelUpDialog.show(character, async (characterData) => {
            try {
                // Call the level up API endpoint
                await CharacterService.levelUp(character.id, characterData);

                Toast.show(`${character.name} leveled up successfully!`);

                // Reload character list
                await this.load();
            } catch (error) {
                console.error('Error leveling up character:', error);
                Toast.show(`Failed to level up: ${error.message}`, true);
                throw error;
            }
        });
    }
}

// Register the custom element
customElements.define('characters-tab', Characters); 
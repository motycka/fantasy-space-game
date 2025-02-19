import CharacterDialog from './CharacterDialog.js';
import CharacterService from '../services/characterService.js';
import Toast from './Toast.js';
import ErrorState from './ErrorState.js';

export default class CharactersTab extends HTMLElement {
    constructor() {
        super();
        this.characterDialog = new CharacterDialog();
    }

    connectedCallback() {
        this.initialize();
    }

    async initialize() {
        console.log('Initializing characters tab...');
        
        // Create tab content structure
        this.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="mb-0">Your Characters</h4>
                <button id="createCharacterBtn" class="btn btn-cosmic">
                    <i class="fas fa-plus-circle"></i> Create Character
                </button>
            </div>
            <div id="charactersList" class="characters-grid">
                <div class="loading">Loading characters...</div>
            </div>
        `;

        // Initialize the character dialog
        this.characterDialog.initialize();
        
        // Add tab change listener
        const charactersTab = document.getElementById('characters-tab');
        if (charactersTab) {
            charactersTab.addEventListener('shown.bs.tab', () => {
                console.log('Characters tab shown, reloading characters...');
                this.loadCharacters();
            });
        }

        // Add event listener for create character button
        const createCharacterBtn = this.querySelector('#createCharacterBtn');
        if (createCharacterBtn) {
            createCharacterBtn.addEventListener('click', () => {
                this.characterDialog.show({
                    onSubmit: async (characterData) => {
                        try {
                            await this.createCharacter(characterData);
                            await this.loadCharacters();
                            Toast.show('Character created successfully!');
                        } catch (error) {
                            console.error('Error creating character:', error);
                            const message = error.message || 'Failed to create character';
                            Toast.show(message, true);
                            throw error;
                        }
                    }
                });
            });
        }

        // Load characters initially
        await this.loadCharacters();
    }

    async loadCharacters() {
        console.log('Loading characters...');
        const listContainer = this.querySelector('#charactersList');
        try {
            listContainer.innerHTML = '<div class="loading">Loading characters...</div>';
    
            const characters = await CharacterService.getCharacters();
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
            listContainer.innerHTML = `
                <div class="empty-state mb-4">
                    <i class="fas fa-user-astronaut fa-3x mb-3"></i>
                    <p>Your cosmic journey begins here...</p>
                    <small>Create your first character to start your adventure</small>
                </div>
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
        // Implement level up logic
        console.log('Level up character:', character);
    }
}

// Register the custom element
customElements.define('characters-tab', CharactersTab); 
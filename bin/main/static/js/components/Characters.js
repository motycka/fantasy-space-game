// import CharacterDialog from './CharacterDialog.js';
import CharacterService from '../services/characterService.js';
import Toast from './Toast.js';
import ErrorState from './ErrorState.js';
import './CharacterCard.js';
import './CharacterDialog.js';
import './EmptyState.js';

export default class Characters extends HTMLElement {
    constructor() {
        super();
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
            <character-dialog></character-dialog>
        `;

        // Get dialog reference and wait for it to be ready
        const dialog = this.querySelector('character-dialog');

        // Add event listener for create character button
        const createCharacterBtn = this.querySelector('#createCharacterBtn');
        if (createCharacterBtn) {
            createCharacterBtn.addEventListener('click', () => {
                // Small delay to ensure modal is initialized
                setTimeout(() => {
                    dialog.show(async (characterData) => {
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
                    });
                }, 100);
            });
        }

        // Load characters initially
        // await this.load();
    }

    async load() {
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
                <empty-state
                    icon="fa-user-astronaut"
                    message="Your cosmic journey begins here..."
                    description="Create your first character to start your adventure"
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
        // Implement level up logic
        console.log('Level up character:', character);
    }
}

// Register the custom element
customElements.define('characters-tab', Characters); 
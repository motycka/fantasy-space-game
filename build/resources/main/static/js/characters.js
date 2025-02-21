import CharacterDialog from './components/CharacterDialog.js';
import CharacterCard from './components/CharacterCard.js';
import CharacterService from './services/characterService.js';
import ErrorState from './components/ErrorState.js';
import Toast from './components/Toast.js';

// Add the function here and export it
export function formatLevel(level) {
    // If level is a string like "LEVEL_1", extract the number
    if (typeof level === 'string' && level.startsWith('LEVEL_')) {
        return `Level ${level.split('_')[1]}`;
    }
    // If it's already a number or other format
    return `Level ${level}`;
}

class CharactersTab {
    constructor() {
        this.characterDialog = new CharacterDialog();
            this.initialize();
    }

    async initialize() {
        console.log('Initializing characters tab...');
        
        // Create tab content structure
        const tabContent = document.getElementById('characters-tab-content');
        if (tabContent) {
            tabContent.innerHTML = `
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
        }
        
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
        const createCharacterBtn = document.getElementById('createCharacterBtn');
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
        const listContainer = document.getElementById('charactersList');
        try {
            listContainer.innerHTML = '<div class="loading">Loading characters...</div>';
    
            const characters = await CharacterService.getCharacters();
            
            console.log('Characters loaded:', characters);
            this.displayCharacters(characters);
        } catch (error) {
            console.error('Error loading characters:', error);
            listContainer.innerHTML = ErrorState.render(error.message, 'characters');
            Toast.show(error.message, true);
        }
    }

    displayCharacters(characters) {
        console.log('Displaying characters:', characters);
        const listContainer = document.getElementById('charactersList');
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
            // Create the character card element
            const characterCard = document.createElement('character-card');
            // Initialize it with the character data
            characterCard.initialize(character, (char) => this.showLevelUpModal(char));
            // Append it to the container
            listContainer.appendChild(characterCard);
        });
    }

    showLevelUpModal(character) {
        this.characterDialog.show({
            isLevelUp: true,
            character: character,
            onSubmit: async (characterData) => {
                try {
                    await this.levelUpCharacter(characterData);
                    await this.loadCharacters();
                    Toast.show('Character leveled up successfully!');
                } catch (error) {
                    console.error('Error leveling up character:', error);
                    const message = error.message || 'Failed to level up character';
                    Toast.show(message, true);
                    throw error; // Re-throw to let dialog handle error state
                }
            }
        });
    }

    async createCharacter(characterData) {
        return CharacterService.createCharacter(characterData);
    }

    async levelUpCharacter(characterData) {
        return CharacterService.levelUpCharacter(characterData);
    }
}

export default CharactersTab; 
import './LevelBadge.js';  // Import for side effects to register the component

export default class CharacterHeader extends HTMLElement {
    constructor() {
        super();
        this.character = null;
    }

    connectedCallback() {
        if (this.character) {
            this.render();
        }
    }

    initialize(character) {
        this.character = character;
        if (this.isConnected) {
            this.render();
        }
    }

    render() {
        if (!this.character) return;

        this.className = 'character-header';
        this.innerHTML = `
            <div class="d-flex align-items-center gap-2">
                <span class="class-icon" title="${this.character.characterClass === 'WARRIOR' ? 'Warrior' : 'Sorcerer'}">
                    ${this.character.characterClass === 'WARRIOR' ? '⚔️' : '🔮'}
                </span>
                <h5 class="character-name mb-0">${this.character.name}</h5>
                <level-badge></level-badge>
            </div>
        `;

        // Initialize level badge
        const levelBadge = this.querySelector('level-badge');
        if (levelBadge) {
            levelBadge.initialize(this.character.level);
        }

        // Initialize tooltip for class icon
        const iconElement = this.querySelector('.class-icon');
        if (iconElement) {
            new bootstrap.Tooltip(iconElement);
        }
    }
}

// Register the custom element
customElements.define('character-header', CharacterHeader); 
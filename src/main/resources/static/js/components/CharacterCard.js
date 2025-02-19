import { COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import './CharacterHeader.js';  // Import for side effects to register the component

export default class CharacterCard extends HTMLElement {
    constructor() {
        super();
        this.character = null;
        this.onLevelUp = null;
        
        // Create template
        this.template = `
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <character-header></character-header>
                </div>
                <div class="character-properties"></div>
            </div>
        `;
    }

    connectedCallback() {
        if (this.character) {
            this.render();
        }
    }

    initialize(character, onLevelUp = null) {
        if (!character) {
            console.error('Cannot initialize CharacterCard with null character');
            return;
        }

        this.character = character;
        this.onLevelUp = onLevelUp;
        if (this.isConnected) {
            this.render();
        }
    }

    createPropertyElement(prop, value) {
        return `
            <div class="property-item" data-property="${prop}">
                <div class="property-label">${this.formatPropertyName(prop)}</div>
                <div class="property-value">${value}</div>
            </div>
        `;
    }

    formatPropertyName(prop) {
        return prop
            .replace(/Power/g, '')
            .replace(/([A-Z])/g, ' $1')
            .replace(/^./, str => str.toUpperCase())
            .trim();
    }

    render() {
        if (!this.character) {
            console.error('Cannot render CharacterCard without character data');
            return;
        }

        this.className = 'character-item card mb-3';
        this.dataset.class = this.character.characterClass;
        
        // Set initial HTML from template
        this.innerHTML = this.template;
        
        // Initialize character header
        const header = this.querySelector('character-header');
        if (header) {
            header.initialize(this.character);
        }
        
        // Build properties HTML
        const propertiesContainer = this.querySelector('.character-properties');
        let propertiesHtml = '';
        
        // Add common properties first
        COMMON_DISPLAY_PROPERTIES.forEach(prop => {
            if (this.character[prop] !== undefined) {
                propertiesHtml += this.createPropertyElement(prop, this.character[prop]);
            }
        });

        // Add class-specific properties
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];
        classSpecificProps.forEach(prop => {
            if (this.character[prop] !== undefined) {
                propertiesHtml += this.createPropertyElement(prop, this.character[prop]);
            }
        });

        // Add experience property with level up functionality
        const expPropertyHtml = this.createPropertyElement('experience', this.character.experience);
        const isCharacterOwner = this.character.isOwner || this.character.owner;
        
        propertiesContainer.innerHTML = propertiesHtml + expPropertyHtml;
        
        // Add level up functionality if needed
        if (this.character.shouldLevelUp && isCharacterOwner && this.onLevelUp) {
            const expProperty = propertiesContainer.querySelector('[data-property="experience"]');
            expProperty.classList.add('can-level-up');
            const label = expProperty.querySelector('.property-label');
            label.textContent = `${this.formatPropertyName('experience')} ⇧`;
            expProperty.addEventListener('click', () => this.onLevelUp(this.character));
        }
    }
}

// Register the custom element
customElements.define('character-card', CharacterCard);
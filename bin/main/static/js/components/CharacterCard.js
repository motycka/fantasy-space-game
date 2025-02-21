import { COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import './CharacterHeader.js';  // Import for side effects to register the component
import './PropertyItem.js';  // Add this import

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
        const propertyItem = document.createElement('property-item');
        const isExp = prop === 'experience';
        const canLevelUp = isExp && this.character.shouldLevelUp && 
                           (this.character.isOwner || this.character.owner) && 
                           this.onLevelUp;
        
        propertyItem.initialize(prop, value, {
            canLevelUp,
            onLevelUp: canLevelUp ? () => this.onLevelUp(this.character) : null
        });
        
        return propertyItem;
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
        
        // Build properties
        const propertiesContainer = this.querySelector('.character-properties');
        
        // Add common properties first
        COMMON_DISPLAY_PROPERTIES.forEach(prop => {
            if (this.character[prop] !== undefined) {
                propertiesContainer.appendChild(this.createPropertyElement(prop, this.character[prop]));
            }
        });

        // Add class-specific properties
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];
        classSpecificProps.forEach(prop => {
            if (this.character[prop] !== undefined) {
                propertiesContainer.appendChild(this.createPropertyElement(prop, this.character[prop]));
            }
        });

        // Add experience property
        const expProperty = this.createPropertyElement('experience', this.character.experience);
        propertiesContainer.appendChild(expProperty);
    }
}

// Register the custom element
customElements.define('character-card', CharacterCard);
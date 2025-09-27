import { CHARACTER_CLASSES, COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import Toast from './Toast.js';
import BaseDialog from './BaseDialog.js';

export default class CharacterDialog extends BaseDialog {
    constructor() {
        super();
        this.currentCharacter = null;
        this.onSubmit = null;
    }

    getModalId() {
        return 'characterModal';
    }

    getModalTitle() {
        return '<i class="fas fa-user-plus"></i> Create New Character';
    }

    getModalSize() {
        return 'modal-dialog-centered';
    }

    renderBody() {
        return `
            <form id="createCharacterForm">
                <div class="mb-3">
                    <label for="characterName" class="form-label">Name</label>
                    <input type="text" class="form-control" id="characterName" name="name" required>
                </div>
                <div class="mb-3">
                    <label for="characterClass" class="form-label">Class</label>
                    <select class="form-select" id="characterClass" name="characterClass" required>
                        <option value="">Choose a class...</option>
                    </select>
                </div>
                <div id="dynamicProperties"></div>
                <div id="pointsContainer" class="mt-3">
                    <div class="points-row">
                        <span>Available Points:</span>
                        <span id="availablePoints">200</span>
                    </div>
                </div>
            </form>
        `;
    }

    renderFooter() {
        return `
            <div class="d-flex gap-2 ms-auto">
                <button type="button" class="btn btn-cosmic-outline" id="autoAssignBtn">
                    <i class="fas fa-magic"></i> Auto Assign
                </button>
                <button type="submit" form="createCharacterForm" class="btn btn-cosmic" disabled>
                    <i class="fas fa-meteor"></i> Launch into the Space
                </button>
            </div>
        `;
    }

    initialize() {
        // Add form submit handler
        const form = this.querySelector('#createCharacterForm');
        if (form) {
            form.addEventListener('submit', (e) => this.handleSubmit(e));
        }

        // Add class change handler
        const classSelect = this.querySelector('#characterClass');
        if (classSelect) {
            classSelect.addEventListener('change', () => this.updatePropertyInputs());

            // Populate class options
            Object.entries(CHARACTER_CLASSES).forEach(([value, data]) => {
                const option = document.createElement('option');
                option.value = value;
                option.textContent = data.name;
                classSelect.appendChild(option);
            });
        }

        // Add auto assign handler
        const autoAssignBtn = this.querySelector('#autoAssignBtn');
        if (autoAssignBtn) {
            autoAssignBtn.addEventListener('click', () => this.autoLevelUp());
        }

        // Create property inputs
        this.renderPropertyInputs();

        // Initially hide all class-specific properties
        this.updatePropertyInputs();
    }

    renderPropertyInputs() {
        const dynamicPropertiesContainer = this.querySelector('#dynamicProperties');
        if (!dynamicPropertiesContainer) return;

        // Create inputs for all properties (common + all class-specific)
        const allProperties = [
            ...COMMON_DISPLAY_PROPERTIES,
            ...Object.values(CLASS_SPECIFIC_PROPERTIES).flat()
        ];

        // Remove duplicates
        const uniqueProperties = [...new Set(allProperties)];

        dynamicPropertiesContainer.innerHTML = uniqueProperties.map(property => {
            const label = property.charAt(0).toUpperCase() + property.slice(1).replace(/([A-Z])/g, ' $1');
            return `
                <div class="mb-3" id="${property}Container">
                    <label for="${property}" class="form-label">${label}</label>
                    <input
                        type="number"
                        class="form-control"
                        id="${property}"
                        name="${property}"
                        value="0"
                        min="0"
                    >
                </div>
            `;
        }).join('');

        // Add event listeners to update remaining points
        uniqueProperties.forEach(property => {
            const input = this.querySelector(`#${property}`);
            if (input) {
                input.addEventListener('input', () => this.updateRemainingPoints());
            }
        });

        // Update the submit button state when points change
        const submitButton = this.querySelector('button[type="submit"]');
        if (submitButton) {
            uniqueProperties.forEach(property => {
                const input = this.querySelector(`#${property}`);
                if (input) {
                    input.addEventListener('input', () => {
                        const pointsDisplay = this.querySelector('#availablePoints');
                        const remainingPoints = parseInt(pointsDisplay?.textContent || '0');
                        submitButton.disabled = remainingPoints !== 0;
                    });
                }
            });
        }
    }

    show(onSubmit) {
        this.onSubmit = onSubmit;
        super.show();
    }

    prepareFormForLevelUp(character) {
        const form = this.querySelector('#createCharacterForm');
        const nameInput = form.querySelector('#characterName');
        const classSelect = form.querySelector('#characterClass');
        
        // Disable name and class fields for level up
        nameInput.value = character.name;
        nameInput.disabled = true;
        
        classSelect.value = character.characterClass;
        classSelect.disabled = true;

        // Set current values
        for (const property of COMMON_DISPLAY_PROPERTIES) {
            const input = form.querySelector(`#${property}`);
            if (input) {
                input.value = character[property];
            }
        }

        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[character.characterClass] || [];
        for (const property of classSpecificProps) {
            const input = form.querySelector(`#${property}`);
            if (input) {
                input.value = character[property];
            }
        }

        // Update available points for new level
        const newLevel = character.level + 1;
        const availablePoints = this.getPointsForLevel(newLevel);
        const pointsDisplay = this.querySelector('#availablePoints');
        if (pointsDisplay) {
            pointsDisplay.textContent = availablePoints;
        }

        this.updatePropertyInputs();
    }

    resetForm() {
        // Wait for next tick to ensure modal is mounted
        setTimeout(() => {
            const form = this.querySelector('#createCharacterForm');
            if (!form) {
                console.error('Character form not found');
                return;
            }

            try {
                form.reset();

                // Enable name and class fields (they might have been disabled in level up mode)
                const nameInput = form.querySelector('#characterName');
                const classSelect = form.querySelector('#characterClass');
                
                if (nameInput) nameInput.disabled = false;
                if (classSelect) classSelect.disabled = false;

                // Reset all numeric inputs to 0
                const numericInputs = form.querySelectorAll('input[type="number"]');
                numericInputs.forEach(input => {
                    input.value = '0';
                });

                // Set initial available points
                const pointsDisplay = this.querySelector('#availablePoints');
                if (pointsDisplay) {
                    pointsDisplay.textContent = this.getPointsForLevel(1);
                }

                this.updatePropertyInputs();
            } catch (error) {
                console.error('Error resetting form:', error);
            }
        }, 0);
    }

    updatePropertyInputs() {
        const form = this.querySelector('#createCharacterForm');
        const selectedClass = form.querySelector('#characterClass').value;
        
        // Hide all class-specific property inputs first
        Object.values(CLASS_SPECIFIC_PROPERTIES).flat().forEach(property => {
            const container = form.querySelector(`#${property}Container`);
            if (container) {
                container.style.display = 'none';
            }
        });

        // Show relevant class-specific property inputs
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[selectedClass] || [];
        classSpecificProps.forEach(property => {
            const container = form.querySelector(`#${property}Container`);
            if (container) {
                container.style.display = 'block';
            }
        });

        this.updateRemainingPoints();
    }

    updateRemainingPoints() {
        const form = this.querySelector('#createCharacterForm');
        const pointsDisplay = this.querySelector('#availablePoints');
        
        if (!pointsDisplay) return;

        const level = this.currentCharacter ? this.currentCharacter.level + 1 : 1;
        const totalPoints = this.getPointsForLevel(level);
        
        let usedPoints = 0;
        
        // Calculate points used in common properties
        COMMON_DISPLAY_PROPERTIES.forEach(property => {
            const input = form.querySelector(`#${property}`);
            if (input) {
                usedPoints += parseInt(input.value) || 0;
            }
        });

        // Calculate points used in class-specific properties
        const selectedClass = form.querySelector('#characterClass').value;
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[selectedClass] || [];
        classSpecificProps.forEach(property => {
            const input = form.querySelector(`#${property}`);
            if (input) {
                usedPoints += parseInt(input.value) || 0;
            }
        });

        const remainingPoints = totalPoints - usedPoints;
        pointsDisplay.textContent = remainingPoints;
        
        // Visual feedback
        pointsDisplay.classList.toggle('text-danger', remainingPoints < 0);
    }

    validateCharacterPoints() {
        const pointsDisplay = this.querySelector('#availablePoints');
        if (!pointsDisplay) return false;

        const remainingPoints = parseInt(pointsDisplay.textContent);
        return remainingPoints >= 0;
    }

    getPointsForLevel(level) {
        return level === 1 ? 200 : 50;
    }

    async handleSubmit(event) {
        event.preventDefault();
        
        if (!this.validateCharacterPoints()) {
            Toast.show('Please ensure points are properly allocated', true);
            return;
        }

        const form = this.querySelector('#createCharacterForm');
        const formData = new FormData(form);
        const characterData = Object.fromEntries(formData.entries());

        // Convert numeric fields to numbers
        const numericFields = [
            ...COMMON_DISPLAY_PROPERTIES,
            ...(CLASS_SPECIFIC_PROPERTIES[characterData.characterClass] || [])
        ];

        numericFields.forEach(field => {
            characterData[field] = parseInt(characterData[field]) || 0;
        });

        try {
            if (this.onSubmit) {
                await this.onSubmit(characterData);
                this.modal.hide();
            }
        } catch (error) {
            console.error('Error submitting character:', error);
            // Error handling is done in the onSubmit callback
        }
    }

    autoLevelUp() {
        const form = this.querySelector('#createCharacterForm');
        const remainingPoints = parseInt(this.querySelector('#availablePoints').textContent);
        
        if (remainingPoints <= 0) return;

        const selectedClass = form.querySelector('#characterClass').value;
        const allProperties = [
            ...COMMON_DISPLAY_PROPERTIES,
            ...(CLASS_SPECIFIC_PROPERTIES[selectedClass] || [])
        ];

        // Distribute points randomly
        for (let i = 0; i < remainingPoints; i++) {
            const randomProperty = allProperties[Math.floor(Math.random() * allProperties.length)];
            const input = form.querySelector(`#${randomProperty}`);
            if (input) {
                input.value = (parseInt(input.value) || 0) + 1;
            }
        }

        this.updateRemainingPoints();
    }
}

// Register the custom element
customElements.define('character-dialog', CharacterDialog);
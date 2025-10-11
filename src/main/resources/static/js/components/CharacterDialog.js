import { CHARACTER_CLASSES, CHARACTER_LEVELS, COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import Toast from './Toast.js';
import BaseDialog from './BaseDialog.js';

export default class CharacterDialog extends BaseDialog {
    constructor() {
        super();
        this.currentCharacter = null;
        this.onSubmit = null;
        this.minimumValues = {}; // Store minimum values for level up mode
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

    /**
     * Helper to get the modal element (now in body, not in this custom element)
     */
    getModalElement() {
        return document.getElementById(this.getModalId());
    }

    initialize() {
        const modal = this.getModalElement();
        if (!modal) {
            console.error('Modal element not found for initialization');
            return;
        }

        // Add form submit handler
        const form = modal.querySelector('#createCharacterForm');
        if (form) {
            form.addEventListener('submit', (e) => this.handleSubmit(e));
        }

        // Add class change handler
        const classSelect = modal.querySelector('#characterClass');
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
        const autoAssignBtn = modal.querySelector('#autoAssignBtn');
        if (autoAssignBtn) {
            autoAssignBtn.addEventListener('click', () => this.autoLevelUp());
        }

        // Create property inputs
        this.renderPropertyInputs();

        // Initially hide all class-specific properties
        this.updatePropertyInputs();
    }

    renderPropertyInputs() {
        const modal = this.getModalElement();
        if (!modal) return;

        const dynamicPropertiesContainer = modal.querySelector('#dynamicProperties');
        if (!dynamicPropertiesContainer) return;

        // Get all unique class-specific properties
        const allClassSpecific = [...new Set(Object.values(CLASS_SPECIFIC_PROPERTIES).flat())];

        // Create HTML for common properties (all on one line)
        const commonPropertiesHtml = COMMON_DISPLAY_PROPERTIES.map(property => {
            const label = property.charAt(0).toUpperCase() + property.slice(1).replace(/([A-Z])/g, ' $1');
            return `
                <div class="attribute-item">
                    <label class="attribute-label-compact">${label}</label>
                    <div class="attribute-input-group">
                        <input
                            type="number"
                            class="attribute-input"
                            id="${property}"
                            name="${property}"
                            value="0"
                            readonly
                        >
                        <span class="attribute-difference" id="${property}-diff" style="display: none;">+0</span>
                        <div class="spinner-buttons-vertical">
                            <button type="button" class="btn-spinner btn-spinner-up" data-property="${property}" data-action="increment">
                                <i class="fas fa-chevron-up"></i>
                            </button>
                            <button type="button" class="btn-spinner btn-spinner-down" data-property="${property}" data-action="decrement">
                                <i class="fas fa-chevron-down"></i>
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        // Create HTML for class-specific properties (all on one line, hidden by default)
        const classSpecificHtml = allClassSpecific.map(property => {
            const label = property.charAt(0).toUpperCase() + property.slice(1).replace(/([A-Z])/g, ' $1');
            return `
                <div class="attribute-item" id="${property}Container">
                    <label class="attribute-label-compact">${label}</label>
                    <div class="attribute-input-group">
                        <input
                            type="number"
                            class="attribute-input"
                            id="${property}"
                            name="${property}"
                            value="0"
                            readonly
                        >
                        <span class="attribute-difference" id="${property}-diff" style="display: none;">+0</span>
                        <div class="spinner-buttons-vertical">
                            <button type="button" class="btn-spinner btn-spinner-up" data-property="${property}" data-action="increment">
                                <i class="fas fa-chevron-up"></i>
                            </button>
                            <button type="button" class="btn-spinner btn-spinner-down" data-property="${property}" data-action="decrement">
                                <i class="fas fa-chevron-down"></i>
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        // Render in two rows
        dynamicPropertiesContainer.innerHTML = `
            <div class="attribute-row mb-3">
                <label class="form-label mb-2"><strong>Base Attributes</strong></label>
                <div class="attributes-inline">
                    ${commonPropertiesHtml}
                </div>
            </div>
            <div class="attribute-row mb-3">
                <label class="form-label mb-2"><strong>Class Attributes</strong></label>
                <div class="attributes-inline">
                    ${classSpecificHtml}
                </div>
            </div>
        `;

        // Add event listeners for all spinner buttons
        const allButtons = modal.querySelectorAll('.btn-spinner');
        allButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const property = button.getAttribute('data-property');
                const action = button.getAttribute('data-action');
                const input = modal.querySelector(`#${property}`);

                if (!input) return;

                let currentValue = parseInt(input.value) || 0;

                if (action === 'increment') {
                    // Intentionally no maximum validation - backend will validate points
                    currentValue++;
                } else if (action === 'decrement') {
                    // In level up mode, don't go below the minimum value (existing attribute value)
                    // In create mode, don't go below 0
                    const minimum = this.minimumValues[property] || 0;
                    currentValue = Math.max(minimum, currentValue - 1);
                }

                input.value = currentValue;

                // Update difference indicator
                const originalValue = this.minimumValues[property] || 0;
                const difference = currentValue - originalValue;
                const diffElement = modal.querySelector(`#${property}-diff`);
                if (diffElement) {
                    if (difference > 0) {
                        diffElement.textContent = `+${difference}`;
                        diffElement.style.display = 'block';
                    } else {
                        diffElement.style.display = 'none';
                    }
                }

                this.updateRemainingPoints();
                this.updateSubmitButton();
            });
        });
    }

    show(onSubmit) {
        this.onSubmit = onSubmit;
        super.show();
    }

    prepareFormForLevelUp(character) {
        const modal = this.getModalElement();
        if (!modal) return;

        const form = modal.querySelector('#createCharacterForm');
        if (!form) return;

        // Store character for level up
        this.currentCharacter = character;

        const nameInput = form.querySelector('#characterName');
        const classSelect = form.querySelector('#characterClass');

        // Disable name and class fields for level up
        if (nameInput) {
            nameInput.value = character.name;
            nameInput.disabled = true;
        }

        if (classSelect) {
            classSelect.value = character.characterClass;
            classSelect.disabled = true;
        }

        // Store minimum values (current character attributes)
        this.minimumValues = {};
        for (const property of COMMON_DISPLAY_PROPERTIES) {
            this.minimumValues[property] = character[property];
        }
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[character.characterClass] || [];
        for (const property of classSpecificProps) {
            this.minimumValues[property] = character[property];
        }

        // Set current values as starting point
        for (const property of COMMON_DISPLAY_PROPERTIES) {
            const input = form.querySelector(`#${property}`);
            if (input) {
                input.value = character[property];
            }
        }

        for (const property of classSpecificProps) {
            const input = form.querySelector(`#${property}`);
            if (input) {
                input.value = character[property];
            }
        }

        // Calculate next level and available points
        const currentLevelData = CHARACTER_LEVELS[character.level];
        const nextLevelKey = `LEVEL_${currentLevelData.ordinal + 2}`; // +2 because ordinal is 0-based and we want next level
        const nextLevelData = CHARACTER_LEVELS[nextLevelKey];

        if (nextLevelData) {
            const pointsDisplay = modal.querySelector('#availablePoints');
            if (pointsDisplay) {
                pointsDisplay.textContent = nextLevelData.points;
            }
        }

        this.updatePropertyInputs();
    }

    resetForm() {
        // Wait for next tick to ensure modal is mounted
        setTimeout(() => {
            const modal = this.getModalElement();
            if (!modal) {
                console.error('Modal element not found');
                return;
            }

            const form = modal.querySelector('#createCharacterForm');
            if (!form) {
                console.error('Character form not found');
                return;
            }

            try {
                form.reset();

                // Clear level up state
                this.currentCharacter = null;
                this.minimumValues = {};

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
                const pointsDisplay = modal.querySelector('#availablePoints');
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
        const modal = this.getModalElement();
        if (!modal) return;

        const form = modal.querySelector('#createCharacterForm');
        if (!form) return;

        const classSelect = form.querySelector('#characterClass');
        if (!classSelect) return;

        const selectedClass = classSelect.value;

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
        const modal = this.getModalElement();
        if (!modal) return;

        const form = modal.querySelector('#createCharacterForm');
        const pointsDisplay = modal.querySelector('#availablePoints');

        if (!form || !pointsDisplay) return;

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
        const classSelect = form.querySelector('#characterClass');
        if (classSelect) {
            const selectedClass = classSelect.value;
            const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[selectedClass] || [];
            classSpecificProps.forEach(property => {
                const input = form.querySelector(`#${property}`);
                if (input) {
                    usedPoints += parseInt(input.value) || 0;
                }
            });
        }

        const remainingPoints = totalPoints - usedPoints;
        pointsDisplay.textContent = remainingPoints;

        // Visual feedback
        pointsDisplay.classList.toggle('text-danger', remainingPoints < 0);
        pointsDisplay.classList.toggle('text-success', remainingPoints === 0);
    }

    updateSubmitButton() {
        const modal = this.getModalElement();
        if (!modal) return;

        const submitButton = modal.querySelector('button[type="submit"]');
        const pointsDisplay = modal.querySelector('#availablePoints');

        if (submitButton && pointsDisplay) {
            const remainingPoints = parseInt(pointsDisplay.textContent || '0');
            submitButton.disabled = remainingPoints !== 0;
        }
    }

    validateCharacterPoints() {
        const modal = this.getModalElement();
        if (!modal) return false;

        const pointsDisplay = modal.querySelector('#availablePoints');
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

        const modal = this.getModalElement();
        if (!modal) return;

        const form = modal.querySelector('#createCharacterForm');
        if (!form) return;

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
        const modal = this.getModalElement();
        if (!modal) return;

        const form = modal.querySelector('#createCharacterForm');
        const pointsDisplay = modal.querySelector('#availablePoints');

        if (!form || !pointsDisplay) return;

        const remainingPoints = parseInt(pointsDisplay.textContent);

        if (remainingPoints <= 0) return;

        const classSelect = form.querySelector('#characterClass');
        if (!classSelect) return;

        const selectedClass = classSelect.value;
        const allProperties = [
            ...COMMON_DISPLAY_PROPERTIES,
            ...(CLASS_SPECIFIC_PROPERTIES[selectedClass] || [])
        ];

        // Distribute points randomly
        for (let i = 0; i < remainingPoints; i++) {
            const randomProperty = allProperties[Math.floor(Math.random() * allProperties.length)];
            const input = form.querySelector(`#${randomProperty}`);
            if (input) {
                const newValue = (parseInt(input.value) || 0) + 1;
                input.value = newValue;

                // Update difference indicator
                const originalValue = this.minimumValues[randomProperty] || 0;
                const difference = newValue - originalValue;
                const diffElement = modal.querySelector(`#${randomProperty}-diff`);
                if (diffElement) {
                    if (difference > 0) {
                        diffElement.textContent = `+${difference}`;
                        diffElement.style.display = 'block';
                    } else {
                        diffElement.style.display = 'none';
                    }
                }
            }
        }

        this.updateRemainingPoints();
        this.updateSubmitButton(); // Fix: Enable submit button after auto-assign
    }
}

// Register the custom element
customElements.define('character-dialog', CharacterDialog);
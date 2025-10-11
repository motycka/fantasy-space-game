import { CHARACTER_LEVELS, COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import { formatPropertyName, getClassIcon, getClassName } from '../utils/formatters.js';
import Toast from './Toast.js';
import BaseDialog from './BaseDialog.js';

export default class LevelUpDialog extends BaseDialog {
    constructor() {
        super();
        this.character = null;
        this.onLevelUp = null;
        this.originalValues = {}; // Store original character values
    }

    getModalId() {
        return 'levelUpModal';
    }

    getModalTitle() {
        return '<i class="fas fa-arrow-up"></i> Level Up Character';
    }

    getModalSize() {
        return 'modal-dialog-centered';
    }

    calculateCurrentPoints() {
        if (!this.character) return 0;

        let total = 0;
        // Add common properties
        COMMON_DISPLAY_PROPERTIES.forEach(property => {
            total += this.character[property] || 0;
        });

        // Add class-specific properties
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];
        classSpecificProps.forEach(property => {
            total += this.character[property] || 0;
        });

        return total;
    }

    renderBody() {
        if (!this.character) {
            return '<div class="text-center p-4">Loading character data...</div>';
        }

        const currentLevelData = CHARACTER_LEVELS[this.character.level];
        const nextLevelKey = `LEVEL_${currentLevelData.ordinal + 2}`; // +2 because ordinal is 0-based and we want next level
        const nextLevelData = CHARACTER_LEVELS[nextLevelKey];

        // Calculate the difference between next level total points and current character points
        const currentPoints = this.calculateCurrentPoints();
        const availablePoints = nextLevelData ? (nextLevelData.points - currentPoints) : 50;

        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];

        return `
            <div class="level-up-container">
                <!-- Character Info -->
                <div class="mb-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h5 class="mb-1">
                                <span class="class-icon">${getClassIcon(this.character.characterClass)}</span>
                                ${this.character.name}
                            </h5>
                            <small class="text-muted">${getClassName(this.character.characterClass)}</small>
                        </div>
                        <div class="text-end">
                            <div class="level-transition">
                                <span class="current-level">Level ${currentLevelData.ordinal + 1}</span>
                                <i class="fas fa-arrow-right mx-2"></i>
                                <span class="next-level text-success">Level ${currentLevelData.ordinal + 2}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Points Display -->
                <div id="pointsContainer" class="mt-3 mb-3">
                    <div class="points-row">
                        <span>Available Points:</span>
                        <span id="remainingPoints">${availablePoints}</span>
                    </div>
                </div>

                <!-- Base Attributes -->
                <div class="attribute-row mb-3">
                    <label class="form-label mb-2"><strong>Base Attributes</strong></label>
                    <div class="attributes-inline">
                        ${COMMON_DISPLAY_PROPERTIES.map(property => this.renderAttributeRow(property)).join('')}
                    </div>
                </div>

                <!-- Class Attributes -->
                ${classSpecificProps.length > 0 ? `
                <div class="attribute-row mb-3">
                    <label class="form-label mb-2"><strong>Class Attributes</strong></label>
                    <div class="attributes-inline">
                        ${classSpecificProps.map(property => this.renderAttributeRow(property)).join('')}
                    </div>
                </div>
                ` : ''}
            </div>
        `;
    }

    renderAttributeRow(property) {
        const currentValue = this.character[property];
        this.originalValues[property] = currentValue;
        const label = formatPropertyName(property);

        return `
            <div class="attribute-item" data-property="${property}">
                <label class="attribute-label-compact">${label}</label>
                <div class="attribute-input-group">
                    <input
                        type="number"
                        class="attribute-input"
                        id="${property}"
                        name="${property}"
                        value="${currentValue}"
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
    }

    renderFooter() {
        return `
            <div class="d-flex gap-2 ms-auto">
                <button type="button" class="btn btn-cosmic-outline" id="autoDistributeBtn">
                    <i class="fas fa-magic"></i> Auto Assign
                </button>
                <button type="button" class="btn btn-cosmic" id="levelUpBtn" disabled>
                    <i class="fas fa-arrow-up"></i> Confirm Level Up
                </button>
            </div>
        `;
    }

    getModalElement() {
        return document.getElementById(this.getModalId());
    }

    initialize() {
        const modal = this.getModalElement();
        if (!modal) {
            console.error('Modal element not found for initialization');
            return;
        }

        // Add event listeners for increment/decrement buttons
        const buttons = modal.querySelectorAll('[data-action]');
        buttons.forEach(button => {
            button.addEventListener('click', () => {
                const property = button.getAttribute('data-property');
                const action = button.getAttribute('data-action');
                this.handleAttributeChange(property, action);
            });
        });

        // Add auto distribute handler
        const autoDistributeBtn = modal.querySelector('#autoDistributeBtn');
        if (autoDistributeBtn) {
            autoDistributeBtn.addEventListener('click', () => this.autoDistribute());
        } else {
            console.warn('Auto distribute button not found');
        }

        // Add level up button handler
        const levelUpBtn = modal.querySelector('#levelUpBtn');
        console.log('Level up button found:', levelUpBtn, 'disabled:', levelUpBtn?.disabled);
        if (levelUpBtn) {
            levelUpBtn.addEventListener('click', () => {
                console.log('Level up button clicked');
                this.handleLevelUp();
            });
        } else {
            console.error('Level up button not found');
        }
    }

    show(character, onLevelUp) {
        this.character = character;
        this.onLevelUp = onLevelUp;
        this.originalValues = {};

        // Update the modal body content with character data
        this.updateBodyContent();

        // Re-initialize event listeners after content update
        this.initialize();

        super.show();
    }

    /**
     * Update the modal body content with current character data
     */
    updateBodyContent() {
        const modal = this.getModalElement();
        if (!modal) {
            console.error('Modal element not found when updating body content');
            return;
        }

        const bodyElement = modal.querySelector('.modal-body');
        if (bodyElement) {
            bodyElement.innerHTML = this.renderBody();
        }

        const footerElement = modal.querySelector('.modal-footer');
        if (footerElement) {
            footerElement.innerHTML = this.renderFooter();
        }
    }

    handleAttributeChange(property, action) {
        const modal = this.getModalElement();
        if (!modal) return;

        const input = modal.querySelector(`#${property}`);
        if (!input) return;

        let currentValue = parseInt(input.value) || this.originalValues[property];
        const minValue = this.originalValues[property];

        if (action === 'increment') {
            currentValue++;
        } else if (action === 'decrement') {
            // Don't go below original value
            currentValue = Math.max(minValue, currentValue - 1);
        }

        input.value = currentValue;

        // Update difference indicator
        const difference = currentValue - minValue;
        const diffElement = modal.querySelector(`#${property}-diff`);
        if (diffElement) {
            if (difference > 0) {
                diffElement.textContent = `+${difference}`;
                diffElement.style.display = 'block';
            } else {
                diffElement.style.display = 'none';
            }
        }

        // Highlight if changed
        const row = modal.querySelector(`[data-property="${property}"]`);
        if (row) {
            if (currentValue > minValue) {
                row.classList.add('attribute-increased');
            } else {
                row.classList.remove('attribute-increased');
            }
        }

        this.updateRemainingPoints();
        this.updateSubmitButton();
    }

    updateRemainingPoints() {
        const modal = this.getModalElement();
        if (!modal) return;

        const currentLevelData = CHARACTER_LEVELS[this.character.level];
        const nextLevelKey = `LEVEL_${currentLevelData.ordinal + 2}`;
        const nextLevelData = CHARACTER_LEVELS[nextLevelKey];

        // Calculate available points as the difference between next level and current points
        const currentPoints = this.calculateCurrentPoints();
        const totalAvailablePoints = nextLevelData ? (nextLevelData.points - currentPoints) : 50;

        let usedPoints = 0;

        // Calculate points used (difference from original values)
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];
        const allProperties = [...COMMON_DISPLAY_PROPERTIES, ...classSpecificProps];

        allProperties.forEach(property => {
            const input = modal.querySelector(`#${property}`);
            if (input) {
                const currentValue = parseInt(input.value) || 0;
                const originalValue = this.originalValues[property] || 0;
                usedPoints += (currentValue - originalValue);
            }
        });

        const remainingPoints = totalAvailablePoints - usedPoints;
        const pointsDisplay = modal.querySelector('#remainingPoints');
        if (pointsDisplay) {
            pointsDisplay.textContent = remainingPoints;
            pointsDisplay.classList.toggle('text-danger', remainingPoints < 0);
            pointsDisplay.classList.toggle('text-success', remainingPoints === 0);
            pointsDisplay.classList.toggle('text-warning', remainingPoints > 0);
        }
    }

    updateSubmitButton() {
        const modal = this.getModalElement();
        if (!modal) return;

        const levelUpBtn = modal.querySelector('#levelUpBtn');
        const pointsDisplay = modal.querySelector('#remainingPoints');

        if (levelUpBtn && pointsDisplay) {
            const remainingPoints = parseInt(pointsDisplay.textContent || '0');
            levelUpBtn.disabled = remainingPoints !== 0;
        }
    }

    autoDistribute() {
        const modal = this.getModalElement();
        if (!modal) return;

        const pointsDisplay = modal.querySelector('#remainingPoints');
        if (!pointsDisplay) return;

        const remainingPoints = parseInt(pointsDisplay.textContent);
        if (remainingPoints <= 0) return;

        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];
        const allProperties = [...COMMON_DISPLAY_PROPERTIES, ...classSpecificProps];

        // Distribute points randomly
        for (let i = 0; i < remainingPoints; i++) {
            const randomProperty = allProperties[Math.floor(Math.random() * allProperties.length)];
            const input = modal.querySelector(`#${randomProperty}`);

            if (input) {
                const newValue = (parseInt(input.value) || 0) + 1;
                input.value = newValue;

                // Update difference indicator
                const originalValue = this.originalValues[randomProperty] || 0;
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

                // Highlight the row
                const row = modal.querySelector(`[data-property="${randomProperty}"]`);
                if (row) {
                    row.classList.add('attribute-increased');
                }
            }
        }

        this.updateRemainingPoints();
        this.updateSubmitButton();
    }

    async handleLevelUp() {
        console.log('handleLevelUp called');
        const modal = this.getModalElement();
        if (!modal) {
            console.error('Modal not found');
            return;
        }

        // Validate points
        const pointsDisplay = modal.querySelector('#remainingPoints');
        if (!pointsDisplay) {
            console.error('Points display not found');
            return;
        }

        const remainingPoints = parseInt(pointsDisplay.textContent);
        console.log('Remaining points:', remainingPoints);
        if (remainingPoints !== 0) {
            Toast.show('Please distribute all points before leveling up', true);
            return;
        }

        // Collect updated character data with all required fields for CharacterUpdateRequest
        const characterData = {
            health: 0,
            attack: 0,
            stamina: null,
            defense: null,
            mana: null,
            healing: null
        };

        // Set common properties
        COMMON_DISPLAY_PROPERTIES.forEach(property => {
            const input = modal.querySelector(`#${property}`);
            if (input) {
                characterData[property] = parseInt(input.value) || 0;
            }
        });

        // Set class-specific properties (and keep others as null)
        const classSpecificProps = CLASS_SPECIFIC_PROPERTIES[this.character.characterClass] || [];
        classSpecificProps.forEach(property => {
            const input = modal.querySelector(`#${property}`);
            if (input) {
                characterData[property] = parseInt(input.value) || 0;
            }
        });

        // Calculate total points being sent
        let totalPoints = 0;
        [...COMMON_DISPLAY_PROPERTIES, ...classSpecificProps].forEach(prop => {
            totalPoints += characterData[prop] || 0;
        });
        console.log('Sending character data with total points:', totalPoints, characterData);

        try {
            if (this.onLevelUp) {
                await this.onLevelUp(characterData);
                this.hide();
            }
        } catch (error) {
            console.error('Error during level up:', error);
            // Error handling is done in the onLevelUp callback
        }
    }
}

// Register the custom element
customElements.define('level-up-dialog', LevelUpDialog);

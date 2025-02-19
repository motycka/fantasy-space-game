import { CHARACTER_CLASSES, COMMON_DISPLAY_PROPERTIES, CLASS_SPECIFIC_PROPERTIES } from '../config.js';
import Toast from './Toast.js';

export default class CharacterDialog {
    constructor() {
        this.modal = null;
        this.currentCharacter = null;
        this.onSubmit = null;
    }

    initialize() {
        // Create modal HTML structure if it doesn't exist
        if (!document.getElementById('characterModal')) {
            document.body.insertAdjacentHTML('beforeend', `
                <div class="modal fade" id="characterModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content cosmic-modal">
                            <div class="modal-header">
                                <h5 class="modal-title" id="characterModalTitle">Create New Character</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <form id="characterForm">
                                <div class="modal-body">
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
                                </div>
                                <div class="modal-footer">
                                    <div class="d-flex gap-2 ms-auto">
                                        <button type="button" class="btn btn-cosmic-outline" id="autoAssignBtn">
                                            <i class="fas fa-magic"></i> Auto Assign
                                        </button>
                                        <button type="submit" class="btn btn-cosmic" disabled>
                                            <i class="fas fa-meteor"></i> Launch into the Space
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            `);
        }

        // Initialize Bootstrap modal
        const modalElement = document.getElementById('characterModal');
        this.modal = new bootstrap.Modal(modalElement);

        // Add form submit handler
        const form = modalElement.querySelector('#characterForm');
        form.addEventListener('submit', (e) => this.handleSubmit(e));

        // Add class change handler
        const classSelect = modalElement.querySelector('#characterClass');
        classSelect.addEventListener('change', () => this.updatePropertyInputs());

        // Populate class options
        Object.entries(CHARACTER_CLASSES).forEach(([value, data]) => {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = data.name;
            classSelect.appendChild(option);
        });
    }

    show({ isLevelUp = false, character = null, onSubmit }) {
        this.currentCharacter = character;
        this.onSubmit = onSubmit;

        // Update modal title and form based on mode
        const modalTitle = document.getElementById('characterModalTitle');
        modalTitle.textContent = isLevelUp ? 'Level Up Character' : 'Create New Character';

        // Reset and prepare form
        this.resetForm();
        
        if (isLevelUp) {
            this.prepareFormForLevelUp(character);
        }

        this.modal.show();
    }

    prepareFormForLevelUp(character) {
        const form = document.getElementById('characterForm');
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
        const pointsDisplay = document.getElementById('availablePoints');
        if (pointsDisplay) {
            pointsDisplay.textContent = availablePoints;
        }

        this.updatePropertyInputs();
    }

    resetForm() {
        const form = document.getElementById('characterForm');
        form.reset();

        // Enable name and class fields (they might have been disabled in level up mode)
        const nameInput = form.querySelector('#characterName');
        const classSelect = form.querySelector('#characterClass');
        nameInput.disabled = false;
        classSelect.disabled = false;

        // Reset all numeric inputs to 0
        const numericInputs = form.querySelectorAll('input[type="number"]');
        numericInputs.forEach(input => {
            input.value = '0';
        });

        // Set initial available points
        const pointsDisplay = document.getElementById('availablePoints');
        if (pointsDisplay) {
            pointsDisplay.textContent = this.getPointsForLevel(1);
        }

        this.updatePropertyInputs();
    }

    updatePropertyInputs() {
        const form = document.getElementById('characterForm');
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
        const form = document.getElementById('characterForm');
        const pointsDisplay = document.getElementById('availablePoints');
        
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
        const form = document.getElementById('characterForm');
        const pointsDisplay = document.getElementById('availablePoints');
        
        if (!pointsDisplay) return false;

        const remainingPoints = parseInt(pointsDisplay.textContent);
        return remainingPoints >= 0;
    }

    getPointsForLevel(level) {
        return level * 5;
    }

    async handleSubmit(e) {
        e.preventDefault();
        
        if (!this.validateCharacterPoints()) {
            Toast.show('Please allocate points correctly before submitting.', true);
            return;
        }

        const form = e.target;
        const formData = new FormData(form);
        const characterData = Object.fromEntries(formData.entries());

        // Convert numeric fields to numbers
        for (const key in characterData) {
            if (key !== 'name' && key !== 'characterClass') {
                characterData[key] = parseInt(characterData[key]);
            }
        }

        // Add ID if this is a level up
        if (this.currentCharacter) {
            characterData.id = this.currentCharacter.id;
        }

        try {
            await this.onSubmit(characterData);
            this.modal.hide();
        } catch (error) {
            console.error('Form submission failed:', error);
            // Modal stays open on error
        }
    }

    autoLevelUp() {
        const form = document.getElementById('characterForm');
        const remainingPoints = parseInt(document.getElementById('availablePoints').textContent);
        
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
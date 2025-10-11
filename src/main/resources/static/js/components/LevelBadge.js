export class LevelBadge extends HTMLElement {
    constructor() {
        super();
        this.level = 1;
    }

    connectedCallback() {
        if (this.level) {
            this.render();
        }
    }

    initialize(levelString) {
        // Parse level number from "LEVEL_n" format
        this.level = parseInt(levelString.split('_')[1]) || 1;
        if (this.isConnected) {
            this.render();
        }
        return this; // Return this for chaining
    }

    render() {
        this.className = 'character-level';
        this.innerHTML = `
            <i class="fas fa-star"></i>
            <span class="level-value">Level ${this.level}</span>
        `;
        return this.outerHTML; // Return the HTML string
    }

    /**
     * Static helper method to render level badge HTML without creating a DOM element
     * @param {string} levelString - Level in "LEVEL_n" format
     * @returns {string} HTML string for the level badge
     */
    static renderHTML(levelString) {
        const level = parseInt(levelString.split('_')[1]) || 1;
        return `
            <span class="character-level">
                <i class="fas fa-star"></i>
                <span class="level-value">Level ${level}</span>
            </span>
        `;
    }
}

customElements.define('level-badge', LevelBadge); 
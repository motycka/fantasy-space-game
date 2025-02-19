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

    initialize(level) {
        this.level = level;
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
}

customElements.define('level-badge', LevelBadge); 
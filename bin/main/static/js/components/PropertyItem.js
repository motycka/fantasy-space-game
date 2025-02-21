export default class PropertyItem extends HTMLElement {
    constructor() {
        super();
        this.property = '';
        this.value = '';
        this.canLevelUp = false;
        this.onLevelUp = null;
    }

    initialize(prop, value, options = {}) {
        this.property = prop;
        this.value = value;
        this.canLevelUp = options.canLevelUp || false;
        this.onLevelUp = options.onLevelUp;
        this.render();
    }

    render() {
        this.innerHTML = `
            <div class="property-item ${this.canLevelUp ? 'can-level-up' : ''}" data-property="${this.property}">
                <div class="property-label">
                    ${this.formatPropertyName(this.property)}${this.canLevelUp ? ' ⇧' : ''}
                </div>
                <div class="property-value">${this.value}</div>
            </div>
        `;

        if (this.canLevelUp && this.onLevelUp) {
            this.querySelector('.property-item').addEventListener('click', () => this.onLevelUp());
        }
    }

    formatPropertyName(prop) {
        return prop
            .replace(/Power/g, '')
            .replace(/([A-Z])/g, ' $1')
            .replace(/^./, str => str.toUpperCase())
            .trim();
    }
}

customElements.define('property-item', PropertyItem); 
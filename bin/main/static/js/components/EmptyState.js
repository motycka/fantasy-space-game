export default class EmptyState extends HTMLElement {
    static get observedAttributes() {
        return ['icon', 'message', 'description'];
    }
    
    constructor() {
        super();
    }

    connectedCallback() {
        this.render();
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (oldValue !== newValue) {
            this.render();
        }
    }

    render() {
        const icon = this.getAttribute('icon') || 'fa-exclamation-circle';
        const message = this.getAttribute('message') || 'No data available';
        const description = this.getAttribute('description') || '';

        this.innerHTML = `
            <div class="empty-state">
                <i class="fas ${icon} fa-3x mb-3"></i>
                <p>${message}</p>
                ${description ? `<small>${description}</small>` : ''}
            </div>
        `;
    }
}

customElements.define('empty-state', EmptyState); 
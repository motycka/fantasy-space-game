/**
 * BaseDialog - Abstract base class for modal dialogs
 *
 * Provides common functionality for Bootstrap modals with cosmic styling.
 * Subclasses should override:
 * - getModalId(): Return unique modal ID
 * - getModalTitle(): Return modal title
 * - getModalSize(): Return modal size (default: 'modal-lg')
 * - renderBody(): Return HTML for modal body
 * - renderFooter(): Return HTML for modal footer
 * - initialize(): Set up event listeners and additional initialization
 */
export default class BaseDialog extends HTMLElement {
    constructor() {
        super();
        this.modal = null;
    }

    connectedCallback() {
        this.render();
        this.initializeModal();
        this.initialize();
    }

    /**
     * Render the complete modal structure
     */
    render() {
        const modalId = this.getModalId();
        const modalSize = this.getModalSize();
        const title = this.getModalTitle();

        this.innerHTML = `
            <div class="modal fade" id="${modalId}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog ${modalSize}">
                    <div class="modal-content cosmic-modal">
                        <div class="modal-header">
                            <h5 class="modal-title">${title}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            ${this.renderBody()}
                        </div>
                        <div class="modal-footer">
                            ${this.renderFooter()}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Initialize the Bootstrap modal
     */
    initializeModal() {
        const modalElement = this.querySelector(`#${this.getModalId()}`);
        if (!modalElement) {
            console.error(`Modal element #${this.getModalId()} not found`);
            return;
        }

        this.modal = new bootstrap.Modal(modalElement, {
            backdrop: false  // Prevent duplicate backdrops
        });
    }

    /**
     * Show the modal
     */
    show() {
        // Ensure modal is initialized (lazy-init safeguard)
        if (!this.modal) {
            try {
                // Render structure in case it's not yet rendered
                this.render();
                // Initialize Bootstrap modal
                this.initializeModal();
                // Allow subclass to bind events
                this.initialize();
            } catch (e) {
                console.error('Failed to initialize modal before showing:', e);
            }
        }
        if (!this.modal) {
            console.error('Modal not initialized');
            return;
        }
        this.modal.show();
    }

    /**
     * Hide the modal
     */
    hide() {
        if (!this.modal) {
            console.error('Modal not initialized');
            return;
        }
        this.modal.hide();
    }

    // ========== Abstract methods to be overridden by subclasses ==========

    /**
     * Get the unique modal ID
     * @returns {string} Modal ID
     */
    getModalId() {
        throw new Error('getModalId() must be implemented by subclass');
    }

    /**
     * Get the modal title
     * @returns {string} Modal title (can include HTML/icons)
     */
    getModalTitle() {
        return 'Modal';
    }

    /**
     * Get the modal size class
     * @returns {string} Bootstrap modal size class
     */
    getModalSize() {
        return 'modal-lg';
    }

    /**
     * Render the modal body content
     * @returns {string} HTML for modal body
     */
    renderBody() {
        return '<p>Override renderBody() to provide content</p>';
    }

    /**
     * Render the modal footer content
     * @returns {string} HTML for modal footer
     */
    renderFooter() {
        return `
            <button type="button" class="btn btn-cosmic-outline" data-bs-dismiss="modal">
                Close
            </button>
        `;
    }

    /**
     * Initialize event listeners and additional setup
     * Called after modal is created and initialized
     */
    initialize() {
        // Override in subclass to add event listeners
    }
}

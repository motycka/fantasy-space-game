# Frontend Architecture

This document describes the architecture and organization of the Fantasy.Space frontend application.

## Overview

The frontend is built using **Web Components** for modularity and reusability, with a clean separation of concerns between UI components, API services, and utility functions.

## Directory Structure

```
js/
├── components/        # Web Components (UI elements)
│   ├── *Dialog.js    # Modal dialogs (extend BaseDialog)
│   ├── *Tab.js       # Main tab components (Characters, Matches, Leaderboard)
│   └── *.js          # Display and utility components
├── services/         # API client modules
├── utils/            # Utility functions and helpers
├── config.js         # Application configuration and constants
└── index.js          # Application entry point
```

## Architecture Patterns

### 1. Web Components

All UI components are implemented as **Custom Elements** following the Web Components standard:

```javascript
export default class MyComponent extends HTMLElement {
    connectedCallback() {
        this.render();
    }

    render() {
        this.innerHTML = `<!-- component HTML -->`;
    }
}

customElements.define('my-component', MyComponent);
```

### 2. Component Types

#### Tab Components
Main page sections that load content dynamically:
- `Characters.js` (`<characters-tab>`)
- `Matches.js` (`<matches-tab>`)
- `Leaderboard.js` (`<leaderboard-tab>`)

**Pattern:**
- Implement `load()` method for fetching data
- Render content into their container
- Handle user interactions

#### Dialog Components
Modal windows that extend `BaseDialog`:
- `CharacterDialog.js` - Character creation and level-up
- `MatchDialog.js` - New match creation
- `BaseDialog.js` - Base class for all dialogs

**Pattern:**
- Extend `BaseDialog`
- Implement `getModalId()`, `getModalTitle()`, `getModalSize()`
- Implement `renderBody()` and `renderFooter()`
- Call `super.show()` to display

#### Display Components
Reusable UI elements:
- `CharacterCard.js` - Character display card
- `LeaderboardTable.js` - Ranking table
- `LevelBadge.js` - Level indicator
- `Toast.js` - Notification system
- `EmptyState.js` - Empty state placeholder
- `ErrorState.js` - Error display

### 3. Service Layer

API client modules in `services/` handle all backend communication:

```javascript
// services/characterService.js
export default class CharacterService extends ApiClient {
    static async getCharacters() {
        return this.getAuthenticated('/api/characters');
    }
}
```

**Available Services:**
- `ApiClient.js` - Base HTTP client with auth
- `accountService.js` - User account operations
- `characterService.js` - Character CRUD operations
- `matchService.js` - Match creation and history
- `leaderboardService.js` - Rankings and statistics

### 4. Utilities

Shared helper functions in `utils/`:

- **`formatters.js`** - Display formatting functions
  - `formatLevel(level)` - Format level string
  - `getLevelNumber(level)` - Extract level number
  - `getClassIcon(characterClass)` - Get class emoji
  - `getClassName(characterClass)` - Get class display name
  - `formatExperience(experience)` - Format XP display
  - `formatPropertyName(propertyName)` - Format camelCase to Title Case

## Component Lifecycle

### Initialization Flow

1. **Page Load** (`index.html`)
   ```html
   <characters-tab></characters-tab>
   <matches-tab></matches-tab>
   <leaderboard-tab></leaderboard-tab>
   ```

2. **Component Registration** (`index.js`)
   ```javascript
   import './components/Characters.js';  // Registers custom element
   import './components/Matches.js';
   import './components/Leaderboard.js';
   ```

3. **Tab Activation** (`index.js`)
   - User clicks tab
   - `shown.bs.tab` event fires
   - Component's `load()` method is called
   - Data is fetched and rendered

### Dialog Lifecycle

1. **Trigger** - User clicks button (e.g., "Create Character")
2. **Show** - Call `dialog.show(callback)`
3. **User Input** - User fills form
4. **Submit** - Form submission calls callback with data
5. **Hide** - Dialog closes on success or cancel

## Data Flow

```
User Action
    ↓
Component Event Handler
    ↓
Service API Call
    ↓
Backend REST API
    ↓
Service Receives Response
    ↓
Component Updates UI
```

## Configuration

**`config.js`** contains application-wide constants:

```javascript
export const CHARACTER_CLASSES = {
    WARRIOR: { name: 'Warrior', properties: {...} },
    SORCERER: { name: 'Sorcerer', properties: {...} }
};

export const CHARACTER_LEVELS = {
    LEVEL_1: { ordinal: 0, points: 200, experience: 0 },
    // ...
};

export const COMMON_DISPLAY_PROPERTIES = ['health', 'attackPower'];
export const CLASS_SPECIFIC_PROPERTIES = {
    WARRIOR: ['stamina', 'defensePower'],
    SORCERER: ['mana', 'healingPower']
};
```

## Authentication

Authentication is handled via Basic Auth:
- Credentials stored in `sessionStorage.auth`
- `ApiClient` automatically includes auth header
- 401 errors trigger redirect to login

## Adding New Components

### 1. Create Component File

```javascript
// components/MyComponent.js
export default class MyComponent extends HTMLElement {
    connectedCallback() {
        this.render();
    }

    render() {
        this.innerHTML = `
            <div class="my-component">
                <!-- Your HTML -->
            </div>
        `;
    }
}

customElements.define('my-component', MyComponent);
```

### 2. Import in Parent

```javascript
import './components/MyComponent.js';
```

### 3. Use in HTML

```html
<my-component></my-component>
```

## Styling

Global styles are in `styles.css`. Components should use semantic class names:
- `.cosmic-*` - Theme-specific styles
- `.btn-cosmic` - Custom button styles
- `.character-*` - Character-related elements
- `.match-*` - Match-related elements

## Best Practices

1. **Component Isolation** - Each component should be self-contained
2. **Single Responsibility** - Components should do one thing well
3. **Use Services** - All API calls go through service layer
4. **Use Utilities** - Don't duplicate formatting logic
5. **Handle Errors** - Always catch and display errors to users
6. **Loading States** - Show loading indicators during async operations
7. **Empty States** - Handle empty data gracefully
8. **Responsive** - Use Bootstrap grid and responsive utilities

## Debugging

- Open browser DevTools Console for logs
- Components log their lifecycle events
- API errors are logged and displayed via Toast
- Use browser Network tab to inspect API calls

## Testing

To test the UI:
1. Start backend: `./gradlew bootRun`
2. Navigate to `http://localhost:8090/`
3. Login with test credentials
4. Test each tab and feature

## Future Improvements

- Unit tests for components
- Integration tests for workflows
- TypeScript for type safety
- Build system for bundling
- CSS modules for component styles

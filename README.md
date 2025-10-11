# Project Assignment: Fantasy Game Simulation with Spring Boot and Kotlin

## Overview
Your task is to create a Spring Boot application in Kotlin that simulates a fantasy game where players can create characters and engage in battles. 
The application should expose a **REST API** for managing characters, matches, and leaderboards.

### Characters API
The characters API allows users to create, update, and retrieve characters. 
Characters can be warriors or sorcerers and have different attributes based on their class.

#### Endpoints

1. `GET /api/characters`
  - Retrieves all characters. 
  - Accepts query parameters to filter by class (WARRIOR, SORCERER), name or no filter:
    - `class=(WARRIOR|SORCERER|null)`
    - `name=(string|null)`
  - Responds with list of:
    ```json
    {
      "id": "1",
      "name": "Aragorn",
      "health": 100,
      "attackPower": 50,
      "stamina": 30,
      "defensePower": 20,
      "mana": null,
      "healingPower": null,
      "characterClass": "WARRIOR",
      "level": "5",
      "experience": 2000,
      "shouldLevelUp": true,
      "isOwner": true
    }
    ```
    The characterClass object:
    ```json
    {
      "CharacterClass": ["WARRIOR", "SORCERER"]
    }
    ```

2. `GET /api/characters/{id}`
   - Retrieves a character by ID.
   - Responds with the same character object as above.

3. `POST /api/characters`
   - Creates a new character.
   - Accepts body:
     ```json
     {
     "name": "Aragorn",
     "health": 100,
     "attackPower": 50,
     "stamina": 30,
     "defensePower": 20,
     "mana": null,
     "healingPower": null,
     "characterClass": "WARRIOR"
     }
     ```
     ```json
     {
     "name": "Aragorn",
     "health": 100,
     "attackPower": 50,
     "stamina": null,
     "defensePower": null,
     "mana": 30,
     "healingPower": 20,
     "characterClass": "SORCERER"
     }
     ```
     The characterClass can be one of: **WARRIOR**, **SORCERER**.
     
4. `GET /api/characters/challengers`
   - Retrieves all challengers (characters owned by the current user).
   
5. `GET /api/characters/opponents`
   - Retrieves all opponents (characters not owned by the current user).
   
6. `PUT /api/characters/{id}`
   - Updates a character by ID (level up).
     - Accepts body:
       ```json
       {
           "name": "Aragorn",
           "health": 100,
           "attackPower": 50,
           "stamina": 30,
           "defensePower": 20,
           "mana": null,
           "healingPower": null
       }
       ```
       ```json
       {
           "name": "Aragorn",
           "health": 100,
           "attackPower": 50,
           "stamina": null,
           "defensePower": null,
           "mana": 30,
           "healingPower": 20
       }
       ```

#### Functional Requirements
- Characters should have health, attack power, level, and experience.
- Warriors should have stamina and defense power.
- Sorcerers should have mana and healing power.
- The service should allow creating a new character and validate point distribution.
- The service should allow updating character attributes (level up) and validate point distribution.
- The service should allow retrieving all characters, a character by ID, all challengers, and all opponents. 
- Challengers are characters owned by the current user.
- Opponents are characters not owned by the current user.

---
### Matches API
The matches API allows users to create and retrieve matches between characters.

#### Endpoints

1. `GET /api/matches`
    - Retrieves all matches.
    - Response object:
      ```json
      {
        "id": "1",
        "challenger": {
          "id": "1",
          "name": "Aragorn",
          "characterClass": "WARRIOR",
          "level": "5",
          "experienceTotal": 2000,
          "experienceGained": 100
        },
        "opponent": {
          "id": "2",
          "name": "Gandalf",
          "characterClass": "SORCERER",
          "level": "5",
          "experienceTotal": 2000,
          "experienceGained": 100
        },
        "rounds": [
          {
            "round": 1,
            "characterId": "1",
            "healthDelta": -10,
            "staminaDelta": -5,
            "manaDelta": 0
          },
          {
            "round": 1,
            "characterId": "2",
            "healthDelta": -5,
            "staminaDelta": 0,
            "manaDelta": -10
          }
        ],
        "matchOutcome": "CHALLENGER_WON"
      }
      ```   
      Match outcome can be one of: **CHALLENGER_WON**, **OPPONENT_WON**, **DRAW**.
       
2. `POST /api/matches`
    - Creates a new match.
    - Accepts body:
      ```json
      {
        "rounds": 10,
        "challengerId": 1,
        "opponentId": 2
      }
      ```

#### Functional Requirements
- The service should allow creating a new match (POST).
- It should validate that characters are valid and that the user owns the challenger character.
- The service should allow retrieving all matches.
- The match should return a list of rounds with changes in health, stamina, and mana for each character.
- The match should update character statistics (experience, wins, losses, draws) based on the match outcome.

#### Match Experience System

The game implements a **level-based risk/reward experience system** that encourages strategic opponent selection.

##### Design Philosophy
- **Challengers** (active participants who initiate matches) receive variable XP based on:
  - Match outcome (win/loss/draw)
  - Level difference between challenger and opponent
- **Opponents** (passive participants who are selected for matches) receive a flat minimal XP regardless of outcome
- Fighting higher-level opponents yields greater rewards and encourages challenging gameplay
- Fighting lower-level opponents yields diminished rewards to discourage "farming"

##### Experience Formula

**Opponent Experience:**
```
Always 20 XP (flat fee for being selected as opponent)
```

**Challenger Experience:**
```
Base XP = 100 × (1 + (OpponentLevel - ChallengerLevel) × 0.2)
Minimum Base XP = 60 (when fighting much lower levels)

Final Challenger XP:
  - Win:  Base × 1.5
  - Loss: Fixed 15 XP (participation reward)
  - Draw: Base × 0.5
```

##### Examples

| Challenger Level | Opponent Level | Win XP | Loss XP | Draw XP | Opponent XP |
|-----------------|----------------|--------|---------|---------|-------------|
| Level 1         | Level 1        | 150    | 15      | 50      | 20          |
| Level 1         | Level 3        | 210    | 15      | 70      | 20          |
| Level 3         | Level 1        | 90     | 15      | 30      | 20          |
| Level 5         | Level 8        | 270    | 15      | 90      | 20          |

##### Strategic Implications
- **High Risk, High Reward**: Challenge higher-level opponents for maximum XP gains
- **Safe Play**: Challenge equal-level opponents for moderate rewards
- **Farming Prevention**: Challenging lower-level opponents gives minimal XP
- **Loss Forgiveness**: Even losses provide 15 XP to keep players engaged
- **Opponent Fairness**: Passive participants receive consistent 20 XP without gameplay burden

##### Implementation
The experience system uses the **Strategy Pattern** for flexibility and testability:
- `ExperienceCalculationStrategy` - Interface defining the calculation contract
- `LevelBasedExperienceStrategy` - Concrete implementation (can be swapped for different algorithms)

---
### Leaderboard API
The leaderboard API allows users to retrieve the leaderboard sorted by position and filtered by class.

#### Endpoints
  GET /api/leaderboards?class=(WARRIOR|SORCERER|null)
#### Model
  ```json
  {
    "position": 1,
    "character": {
      "id": "1",
      "name": "Aragorn",
      "health": 100,
      "attackPower": 50,
      "stamina": 30,
      "defensePower": 20,
      "mana": null,
      "healingPower": null,
      "characterClass": "WARRIOR",
      "level": "5",
      "experience": 2000,
      "shouldLevelUp": true,
      "isOwner": true
    },
    "wins": 10,
    "losses": 2,
    "draws": 1
  }
  ```

#### Functional Requirements
    - The service should allow retrieving the leaderboard sorted by position.
    - The leaderboard should allow filtering by class (WARRIOR, SORCERER) or no filter.

### User Management API
The user management API allows users to register.

#### Endpoints

1. `POST /api/accounts`
    - Registers a new user.
    - Accepts body:
      ```json
      {
        "name": "John Doe",
        "username": "johndoe",
        "password": "password123"
      }
    ```

## Authentication
- Authentication is done using Basic Auth.
- The user is authenticated using the username and password.

## Database and Data mode
- H2 database is provided by default.
- The database schema is provided in the `schema.sql` file.
- The database is initialized with data in the `data.sql` file.
- You can update the provided basic model or use another database if preferred.

## User Interface
I have provided a simple user interface that consumes the API, you can use it to verify the API functionality.

Access the UI at `http://localhost:8090/` (by default).

## General Requirements
- Authentication is required to use the API.
- Register your user using the user management API or add it to the database init script.
- Use a layered architecture (Controller, Service, Repository).
- Use different classes to map objects between layers (DTOs), for example have dedicated classes for REST serialized objects and database objects.
- Use a service layer to handle business logic.
- Use a repository layer to handle database operations.
- Use a controller layer to handle REST API requests.
- Don't forget about error handling and validations.
- Use jUnit and Mockk for testing.
- If you adhere to the schema and requirements, I will have a cool UI for you.

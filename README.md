# Cage Kings

Cage Kings is a virtual MMA betting simulator that enables users to place bets and view data without real financial loss.

## Contributors
* **Robert Lloyd** - Lead Developer, Documentation & Architecture
* **Clemente Fernandez** - Developer, Testing & Version Control
* **Alden Walker** - Developer
* **Eddie Garcia** - Developer

## Application Features
* **Live Fight Data:** Fetches real-world fight data using the api-sports.io.
* **User Authentication:** Registration and login system with persistent (local) user profiles.
* **Bankroll Management:** Add virtual funds and track your balance across sessions.
* **Betting Simulator:** Place bets on upcoming or recent fights and see results immediately based on actual fight outcomes.
* **Betting History:** Track previous wagers and outcomes via the dedicated Bet History view.
* **Fighter Stats:** View fighter records (Wins/Losses/Draws) directly within the app.

## Project Structure (MVC Pattern)
The project strictly adheres to the Model-View-Controller architecture:
* **Models:** `FightModel.java`, `User.java`, `BalanceModel.java`, `Session.java`.
* **Views (FXML):** `login-view.fxml`, `main-screen.fxml` (Main), `balance.fxml`, `previous-bets.fxml`.
* **Controllers:** `LoginController.java`, `MainController.java`, `BalanceController.java`, `BetHistoryController.java`.
* **Services:** `FightService.java` (API communication and JSON parsing).

## Requirements
* **Java JDK 17 or higher**
* **Maven** (for dependency management)
* **Internet Access:** Required to fetch live fight data from the API.
* **JSON Library:** The project uses `org.json` for parsing API responses.

## Installation & Running Instructions

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/UTSA-CS-3443/Spring2026-CS3443-Section003-XOR_Squad.git
    cd path/to/repo
    ```

2.  **Ensure Data Directory Exists:**
    The application stores user data in a `data/` folder at the project root. Create this folder if it is not present:
    ```bash
    mkdir data
    ```

3.  **Build the Project:**
    Using Maven:
    ```bash
    mvn clean install
    ```

4.  **Run the Application:**
    Execute the `Launcher` class to start the JavaFX environment:
    ```bash
    mvn javafx:run
    ```
    *Alternatively, run the `Launcher.java` file directly from your IDE.*

## Known Issues & Limitations
* **API Rate Limits:** The free tier of the MMA API may limit the number of fighter record lookups.
* **Single Device Persistence:** User data is stored locally in CSV files within the `data/` directory and is not synced to a remote/server database.
* **User data conflicts:** The application has trouble separating user data from each other. Balance and betting history may override one another.

## UML Diagram
The UML diagram illustrating the MVC relationships is located in the root of the repository as `UML_Diagram.png`. It details the interactions between the controllers and the service layer.
`insert_uml_here`

# Ticket to Ride: Rails & Sails

**Java game engine and JavaFX desktop application · February–June 2023**

We developed a digital adaptation of the *World* board from *Ticket to Ride: Rails & Sails* (French title: *Les Aventuriers du Rail — Autour du Monde*) during the second semester of the BUT Informatique at **IUT Montpellier–Sète**. Our work covered two successive deliverables: the game mechanics in Java, followed by a desktop interface in JavaFX.

## The two phases

| Phase | Period | Completed work | Source |
| --- | --- | --- | --- |
| **1 — Game engine** | February–March 2023; assessment in April | Object-oriented implementation of the game rules, player actions and scoring, with the supplied browser interface and unit tests. | [phase-1-game-engine](phase-1-game-engine/) |
| **2 — Graphical application** | May–June 2023 | JavaFX application with player setup, an interactive board, card and destination views, player information and final results. | [phase-2-javafx](phase-2-javafx/) |

The graphical deliverable was designated **Phase 3 / IHM** in the original course material. It was the second of the two deliverables collected here.

## Phase 1 — Game mechanics

We implemented the Java model around the board, cities, players, transport cards, routes and destinations. Our work included:

- Drawing and discarding wagon and ship cards, replenishing visible cards and handling jokers.
- Claiming land routes, sea routes and paired routes with the corresponding cards and pieces.
- Building harbours and exchanging train and ship pieces.
- Managing player turns, destination choices and the end of a game.
- Checking connected destinations and calculating scores, bonuses and penalties.

The application used the supplied WebSocket server and browser client to display the game state and receive player choices. The repository also retained the JUnit test suite and the original assessment results.

## Phase 2 — JavaFX interface

We built the desktop interface around the JavaFX-adapted engine supplied for the IHM assignment. Observable properties, listeners and bindings connected the views to the game state through the provided Java interfaces.

The completed interface included:

- Player setup with names and avatar selection.
- An interactive world map with selectable routes and harbours.
- Transport-card piles, visible cards and destination choices.
- Separate views for the current player and the other players.
- Action prompts, game messages and visual feedback.
- A results screen with player rankings, scores and completed destinations.
- Custom graphics, CSS styling, music and sound effects.

FXML layouts and Java view classes organised the application into setup, gameplay and results screens.

## Technical structure

Both deliverables used **Java 17** and **Maven**, with separate build configurations. The graphical application used **JavaFX 18.0.2**, FXML and CSS. The first phase used Jackson, Gson and Tyrus for its browser connection, alongside JUnit, Mockito and Hamcrest for testing.

| Component | Location |
| --- | --- |
| Game engine and browser server | [Phase 1 Java sources](phase-1-game-engine/src/main/java/fr/umontpellier/iut/) |
| Browser client | [Phase 1 web interface](phase-1-game-engine/web/index.html) |
| Unit tests | [Phase 1 tests](phase-1-game-engine/src/test/java/fr/umontpellier/iut/rails/) |
| JavaFX entry point | [RailsIHM.java](phase-2-javafx/src/main/java/fr/umontpellier/iut/rails/RailsIHM.java) |
| Desktop views | [JavaFX view classes](phase-2-javafx/src/main/java/fr/umontpellier/iut/rails/vues/) |
| Layouts, styles and media | [JavaFX resources](phase-2-javafx/src/main/resources/) |

## Authors and academic context

We completed the project as **[Daniil Hirchyts](https://github.com/Daniil-Hirchyts)** and **[Clément Garro](https://github.com/Clement-Garro)**, whose historical commits also used the names **Suyano** and **Suuyano**.

The project formed part of the **SAÉ S2.01 / S2.02** coursework. The teaching team supplied the assignment material, starter code, board data, browser interface, tests and the adapted engine used by the JavaFX phase. Our contributions completed the game mechanics and the desktop views on those foundations.

The original [game-engine brief](phase-1-game-engine/README.md), [IHM brief](phase-2-javafx/README.md), phase-specific instructions and game-rule documents were retained with their respective deliverables.

## License

The authors' original contributions are licensed under the [MIT License](LICENSE). Third-party code, assets and course materials retain their respective rights and license notices.

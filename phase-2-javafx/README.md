# Phase 2 — JavaFX desktop application

The desktop interface for *Ticket to Ride: Rails & Sails*, developed in May–June 2023.
The course designated this deliverable as Phase 3 / IHM.

## Interface

- Player names and avatar selection before the game.
- A world map with clickable routes and harbours.
- Transport-card piles, visible cards and destination choices.
- Current-player information and separate views of the other players.
- Action prompts, game messages, music and sound effects.
- Final rankings, scores and completed destinations.

The views used JavaFX properties, listeners and bindings to observe the supplied game engine through its Java
interfaces. FXML, CSS and Java view classes defined the setup, game and results screens.

## Launch

Use JDK 17 and Maven. Import this directory as a Maven project and run
[`fr.umontpellier.iut.rails.RailsIHM`](src/main/java/fr/umontpellier/iut/rails/RailsIHM.java)
with the JavaFX 18.0.2 dependencies declared in `pom.xml`.

The Maven resource configuration enables filtering for all resources, including binary media. This can cause
resource-processing errors during a Maven build.

## Source

- [Application entry point](src/main/java/fr/umontpellier/iut/rails/RailsIHM.java)
- [View classes](src/main/java/fr/umontpellier/iut/rails/vues/)
- [Layouts, styles and media](src/main/resources/)

## Credits

Daniil Hirchyts and Clément Garro, BUT Informatique, IUT Montpellier–Sète.
The JavaFX phase used the adapted game engine and interfaces supplied by the teaching team.
The [course brief](ASSIGNMENT.md) describes the assignment, starter architecture and assessment requirements.

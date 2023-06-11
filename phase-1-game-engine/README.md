# Phase 1 — Java game engine

The game mechanics for the World board of *Ticket to Ride: Rails & Sails*, developed in February–March 2023.

## Implementation

The model handled players, cities, train and ship routes, transport cards, destinations and scoring.
Player actions included drawing cards, claiming routes, building harbours and exchanging train and ship pieces.
The game managed turn order, destination selection and the final rounds.

The supplied WebSocket server connected the model to a browser interface. Game state and prompts were also
printed to the console.

## Run a game

Use JDK 17 and Maven. Import this directory as a Maven project, then run
[`fr.umontpellier.iut.gui.GameServer`](src/main/java/fr/umontpellier/iut/gui/GameServer.java).
Open [`web/index.html`](web/index.html) in a browser while the server is running on `localhost:3232`.

The default player names are set in `GameServer.main`. Input can be supplied through the browser or console.

## Tests and source

From this directory, run the JUnit suite with:

```bash
mvn test
```

- [Game model](src/main/java/fr/umontpellier/iut/rails/)
- [Board data](src/main/java/fr/umontpellier/iut/rails/data/)
- [Browser server](src/main/java/fr/umontpellier/iut/gui/)
- [Tests](src/test/java/fr/umontpellier/iut/rails/)

## Credits

Daniil Hirchyts and Clément Garro, BUT Informatique, IUT Montpellier–Sète.
The teaching team supplied the starter code, board data, browser interface and assessment tests.
The [course brief](ASSIGNMENT.md) documents the assignment and its requirements.

package fr.umontpellier.iut.rails;

import com.google.gson.Gson;
import fr.umontpellier.iut.gui.GameServer;
import fr.umontpellier.iut.rails.data.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Jeu implements Runnable {
    /**
     * Liste des joueurs
     */
    private final List<Joueur> joueurs;
    /**
     * Liste des villes disponibles sur le plateau de jeu
     */
    private final List<Ville> portsLibres;
    /**
     * Liste des routes disponibles sur le plateau de jeu
     */
    private final List<Route> routesLibres;
    /**
     * Pile de pioche et défausse des cartes wagon
     */
    private final PilesCartesTransport pilesDeCartesWagon;
    /**
     * Pile de pioche et défausse des cartes bateau
     */
    private final PilesCartesTransport pilesDeCartesBateau;
    /**
     * Cartes de la pioche face visible (normalement il y a 6 cartes face visible)
     */
    private final List<CarteTransport> cartesTransportVisibles;
    /**
     * Pile des cartes "Destination"
     */
    private final List<Destination> pileDestinations;
    /**
     * File d'attente des instructions recues par le serveur
     */
    private final BlockingQueue<String> inputQueue;
    /**
     * Messages d'information du jeu
     */
    private final List<String> log;
    /**
     * Le joueur dont c'est le tour
     */
    private Joueur joueurCourant;
    private String instruction;
    private Collection<Bouton> boutons;

    public Jeu(String[] nomJoueurs) {
        // initialisation des entrées/sorties
        inputQueue = new LinkedBlockingQueue<>();
        log = new ArrayList<>();

        // création des villes et des routes
        Plateau plateau = Plateau.makePlateauMonde();
        portsLibres = plateau.getPorts();
        routesLibres = plateau.getRoutes();

        // création des piles de pioche et défausses des cartes Transport (wagon et
        // bateau)
        ArrayList<CarteTransport> cartesWagon = new ArrayList<>();
        ArrayList<CarteTransport> cartesBateau = new ArrayList<>();
        for (Couleur c : Couleur.values()) {
            if (c == Couleur.GRIS) {
                continue;
            }
            for (int i = 0; i < 4; i++) {
                // Cartes wagon simples avec une ancre
                cartesWagon.add(new CarteTransport(TypeCarteTransport.WAGON, c, false, true));
            }
            for (int i = 0; i < 7; i++) {
                // Cartes wagon simples sans ancre
                cartesWagon.add(new CarteTransport(TypeCarteTransport.WAGON, c, false, false));
            }
            for (int i = 0; i < 4; i++) {
                // Cartes bateau simples (toutes avec une ancre)
                cartesBateau.add(new CarteTransport(TypeCarteTransport.BATEAU, c, false, true));
            }
            for (int i = 0; i < 6; i++) {
                // Cartes bateau doubles (toutes sans ancre)
                cartesBateau.add(new CarteTransport(TypeCarteTransport.BATEAU, c, true, false));
            }
        }
        for (int i = 0; i < 14; i++) {
            // Cartes wagon joker
            cartesWagon.add(new CarteTransport(TypeCarteTransport.JOKER, Couleur.GRIS, false, true));
        }
        pilesDeCartesWagon = new PilesCartesTransport(cartesWagon);
        pilesDeCartesBateau = new PilesCartesTransport(cartesBateau);

        // création de la liste pile de cartes transport visibles
        // (les cartes seront retournées plus tard, au début de la partie dans run())
        cartesTransportVisibles = new ArrayList<>();

        // création des destinations
        pileDestinations = Destination.makeDestinationsMonde();
        Collections.shuffle(pileDestinations);

        // création des joueurs
        ArrayList<Joueur.CouleurJouer> couleurs = new ArrayList<>(Arrays.asList(Joueur.CouleurJouer.values()));
        Collections.shuffle(couleurs);
        joueurs = new ArrayList<>();
        for (String nomJoueur : nomJoueurs) {
            joueurs.add(new Joueur(nomJoueur, this, couleurs.remove(0)));
        }
        this.joueurCourant = joueurs.get(0);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public List<Ville> getPortsLibres() {
        return new ArrayList<>(portsLibres);
    }

    public List<Route> getRoutesLibres() {
        return routesLibres;
    }

    public PilesCartesTransport getPilesDeCartesWagon() {
        return pilesDeCartesWagon;
    }

    public PilesCartesTransport getPilesDeCartesBateau() {
        return pilesDeCartesBateau;
    }

    public List<Destination> getPileDestinations() {
        return pileDestinations;
    }

    public List<CarteTransport> getCartesTransportVisibles() {
        return cartesTransportVisibles;
    }

    /**
     * Exécute la partie
     * <p>
     * C'est cette méthode qui est appelée pour démarrer la partie. Elle doit intialiser le jeu
     * (retourner les cartes transport visibles, puis demander à chaque joueur de choisir ses destinations initiales
     * et le nombre de pions wagon qu'il souhaite prendre) puis exécuter les tours des joueurs en appelant la
     * méthode Joueur.jouerTour() jusqu'à ce que la condition de fin de partie soit réalisée.
     */
    public void run() {

        //Afficher les cartes transport visibles
        for (int i = 0; i < 3; i++) {
            cartesTransportVisibles.add(pilesDeCartesWagon.piocher());
            cartesTransportVisibles.add(pilesDeCartesBateau.piocher());
        }

        //Pioche des cartes transport initiales
        Collections.shuffle(pileDestinations);
        for (Joueur j : joueurs) {
            List<CarteTransport> cartesBateau = new ArrayList<>(pilesDeCartesBateau.getCartes());
            List<CarteTransport> cartesWagon = new ArrayList<>(pilesDeCartesWagon.getCartes());
            Collections.shuffle(cartesWagon);
            Collections.shuffle(cartesBateau);
            for (int i = 0; i < 7; i++) {
                if (i < 3) j.getCartesTransport().add(pilesDeCartesWagon.piocher());
                j.getCartesTransport().add(pilesDeCartesBateau.piocher());
            }
        }

        //Choix des destinations initiales et des pions
        for (Joueur j : joueurs) {
            joueurCourant = j;
            PiocheCartesDestination(2, 5, joueurCourant);
            faireChoixPions(joueurCourant);
        }

        //Début de la partie
        for (Joueur j : joueurs) {
            log("C'est au tour de " + j.getNom() + " de choisir ses destinations initiales.");
            joueurCourant = j;
            j.jouerTour();
        }

        //Fin de la partie
        prompt("Fin de la partie.", new ArrayList<>(), true);
    }

    /**
     * Pioche des cartes destination pour un joueur
     *
     * @param nbCartesMax  nombre maximal de cartes à retirer
     * @param nbTotalCartes nombre total de cartes à piocher
     * @param joueurCourant joueur qui pioche
     */
    public void PiocheCartesDestination(int nbCartesMax, int nbTotalCartes, Joueur joueurCourant) {
        for (int i = 0; i < nbTotalCartes; i++) joueurCourant.getDestinations().add(piocherDestination());
        List<String> choixDestinaton = new ArrayList<>();
        List<String> choixDestinatonSpecial = new ArrayList<>();
        for (Destination destination : joueurCourant.getDestinations()) {
            choixDestinatonSpecial.add(destination.getNom());
            StringJoiner name = new StringJoiner(",");
            for (String ville : destination.getVilles()) name.add(ville);
            choixDestinaton.add(name.toString());
        }
        int compteurRemove = 0;
        while (compteurRemove < nbCartesMax) {
            List<String> grabberDestinaton = Stream.concat(choixDestinaton.stream(), choixDestinatonSpecial.stream()).collect(Collectors.toList());
            String inputTour = joueurCourant.choisir("<h3>Choisissez une destination a retirer</h3>", grabberDestinaton, null, true);
            if (inputTour.isEmpty()) compteurRemove = nbCartesMax;
            else {
                Iterator<Destination> iterator = joueurCourant.getDestinations().iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    Destination destination = iterator.next();
                    if (choixDestinaton.get(index).equals(inputTour) || choixDestinatonSpecial.get(index).equals(inputTour)) {
                        iterator.remove();
                        pileDestinations.add(destination);
                        choixDestinaton.remove(index);
                        choixDestinatonSpecial.remove(index);
                        compteurRemove++;
                        break;
                    }
                    index++;
                }
            }
        }
    }

    /**
     * Demande au joueur de choisir le nombre de pions wagon qu'il souhaite prendre
     *
     * @param j joueur qui choisit
     */
    public void faireChoixPions(Joueur j) {
        joueurCourant = j;
        List<String> choix = IntStream.rangeClosed(10, 25)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        while (true) {
            String inputTour = j.choisir("<h3>Combien de wagon voulez vous garder (10-25)</h3>", choix, null, false);
            if (choix.contains(inputTour)) {
                int nbWagon = Integer.parseInt(inputTour);
                j.setNbPionsWagon(nbWagon);
                j.setNbPionsWagonEnReserve(25 - nbWagon);
                int nbBateau = 60 - nbWagon;
                j.setNbPionsBateau(nbBateau);
                j.setNbPionsBateauEnReserve(50 - nbBateau);
                break;
            }
        }
    }

    /**
     * Pioche une carte de la pile de pioche des cartes wagon.
     *
     * @return la carte qui a été piochée (ou null si aucune carte disponible)
     */
    public CarteTransport piocherCarteWagon() {
        return pilesDeCartesWagon.piocher();
    }

    public boolean piocheWagonEstVide() {
        return pilesDeCartesWagon.estVide();
    }

    /**
     * Pioche une carte de la pile de pioche des cartes bateau.
     *
     * @return la carte qui a été piochée (ou null si aucune carte disponible)
     */
    public CarteTransport piocherCarteBateau() {
        return pilesDeCartesBateau.piocher();
    }

    public boolean piocheBateauEstVide() {
        return pilesDeCartesBateau.estVide();
    }

    public Destination piocherDestination() {
        if (pileDestinations.isEmpty()) {
            return null;
        }
        return pileDestinations.remove(0);
    }

    /**
     * Ajoute un message au log du jeu
     */
    public void log(String message) {
        log.add(message);
    }

    /**
     * Ajoute un message à la file d'entrées
     */
    public void addInput(String message) {
        inputQueue.add(message);
    }

    /**
     * Lit une ligne de l'entrée standard
     * C'est cette méthode qui doit être appelée à chaque fois qu'on veut lire
     * l'entrée clavier de l'utilisateur (par exemple dans {@code Player.choisir})
     *
     * @return une chaîne de caractères correspondant à l'entrée suivante dans la
     * file
     */
    public String lireLigne() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envoie l'état de la partie pour affichage aux joueurs avant de faire un choix
     *
     * @param instruction l'instruction qui est donnée au joueur
     * @param boutons     labels des choix proposés s'il y en a
     * @param peutPasser  indique si le joueur peut passer sans faire de choix
     */
    public void prompt(String instruction, Collection<Bouton> boutons, boolean peutPasser) {
        this.instruction = instruction;
        this.boutons = boutons;

//        System.out.println();
//        System.out.println(this);
        if (boutons.isEmpty()) {
//            System.out.printf(">>> %s: %s <<<\n", joueurCourant.getNom(), instruction);
        } else {
            StringJoiner joiner = new StringJoiner(" / ");
            for (Bouton bouton : boutons) {
                joiner.add(bouton.toPrompt());
            }
//            System.out.printf(">>> %s: %s [%s] <<<\n", joueurCourant.getNom(), instruction, joiner);
        }
        GameServer.setEtatJeu(new Gson().toJson(dataMap()));
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        for (Joueur j : joueurs) {
            joiner.add(j.toString());
        }
        return joiner.toString();
    }

    public Map<String, Object> dataMap() {
        return Map.ofEntries(
                Map.entry("joueurs", joueurs.stream().map(Joueur::dataMap).toList()),
                Map.entry("joueurCourant", joueurs.indexOf(joueurCourant)),
                Map.entry("piocheWagon", pilesDeCartesWagon.dataMap()),
                Map.entry("piocheBateau", pilesDeCartesBateau.dataMap()),
                Map.entry("cartesTransportVisibles", cartesTransportVisibles),
                Map.entry("nbDestinations", pileDestinations.size()),
                Map.entry("instruction", instruction),
                Map.entry("boutons", boutons),
                Map.entry("log", log));
    }
}

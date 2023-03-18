package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.data.CarteTransport;
import fr.umontpellier.iut.rails.data.Destination;
import fr.umontpellier.iut.rails.data.TypeCarteTransport;
import fr.umontpellier.iut.rails.data.Ville;

import java.util.*;

public class Joueur {
    /**
     * Jeu auquel le joueur est rattaché
     */
    private final Jeu jeu;
    /**
     * Nom du joueur
     */
    private final String nom;
    /**
     * CouleurJouer du joueur (pour représentation sur le plateau)
     */
    private final CouleurJouer couleur;
    /**
     * Liste des villes sur lesquelles le joueur a construit un port
     */
    private final List<Ville> ports;
    /**
     * Liste des routes capturées par le joueur
     */
    private final List<Route> routes;
    /**
     * Liste des destinations à réaliser pendant la partie
     */
    private final List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private final List<CarteTransport> cartesTransport;
    /**
     * Liste temporaire de cartes transport que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'un port
     */
    private final List<CarteTransport> cartesTransportPosees;
    /**
     * Nombre de pions wagons que le joueur peut encore poser sur le plateau
     */
    private int nbPionsWagon;
    /**
     * Nombre de pions wagons que le joueur a dans sa réserve (dans la boîte)
     */
    private int nbPionsWagonEnReserve;
    /**
     * Nombre de pions bateaux que le joueur peut encore poser sur le plateau
     */
    private int nbPionsBateau;
    /**
     * Nombre de pions bateaux que le joueur a dans sa réserve (dans la boîte)
     */
    private int nbPionsBateauEnReserve;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées, et points
     * perdus lors des échanges de pions)
     */
    private int score;


    public Joueur(String nom, Jeu jeu, CouleurJouer couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        this.ports = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.nbPionsWagon = 0;
        this.nbPionsWagonEnReserve = 25;
        this.nbPionsBateau = 0;
        this.nbPionsBateauEnReserve = 50;
        this.cartesTransport = new ArrayList<>();
        this.cartesTransportPosees = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.score = 0;
    }

    public void setNbPionsWagonEnReserve(int nbPionsWagonEnReserve) {
        this.nbPionsWagonEnReserve = nbPionsWagonEnReserve;
    }

    public void setNbPionsBateauEnReserve(int nbPionsBateauEnReserve) {
        this.nbPionsBateauEnReserve = nbPionsBateauEnReserve;
    }

    public int getNbPionsWagon() {
        return nbPionsWagon;
    }

    public void setNbPionsWagon(int nbPionsWagon) {
        this.nbPionsWagon = nbPionsWagon;
    }

    public int getNbPionsBateau() {
        return nbPionsBateau;
    }

    public void setNbPionsBateau(int nbPionsBateau) {
        this.nbPionsBateau = nbPionsBateau;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public List<CarteTransport> getCartesTransport() {
        return cartesTransport;
    }

    public String getNom() {
        return nom;
    }

    /**
     * Cette méthode est appelée à tour de rôle pour chacun des joueurs de la partie.
     * Elle doit réaliser un tour de jeu, pendant lequel le joueur a le choix entre 5 actions possibles :
     * - piocher des cartes transport (visibles ou dans la pioche)
     * - échanger des pions wagons ou bateau
     * - prendre de nouvelles destinations
     * - capturer une route
     * - construire un port
     */
    // TODO : jouerTour
    void jouerTour() {
        // TODO : échanger des pions wagons ou bateau
        // TODO : prendre de nouvelles destinations
        // TODO : capturer une route
        // TODO : construire un port
        boolean tour = true;
        int compteur = 0;
        boolean fin = false;
        do {
            //clear the log
            List<Bouton> boutons = Arrays.asList(
                    new Bouton("Echanger pions wagons", "PIONS WAGON"),
                    new Bouton("Echanger pions bateaux", "PIONS BATEAU")
            );
            String inputTour;
            List<String> choixTour = new ArrayList<>(Arrays.asList("WAGON", "BATEAU", "DESTINATION"));
            List<String> choixcarteVisible = new ArrayList<>();
            List<String> choixRoute = new ArrayList<>();
            List<String> choixPort = new ArrayList<>();
            for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                choixcarteVisible.add(carteTransport.getNom());
            }
            for (Route route : jeu.getRoutesLibres()) {
                choixRoute.add(route.getNom());
            }
            for (Ville ville : jeu.getPortsLibres()) {
                choixPort.add(ville.nom());
            }
            choixTour.addAll(choixRoute);
            choixTour.addAll(choixcarteVisible);
            choixTour.addAll(choixPort);
            if (fin) {
                inputTour = choisir("qu'elle choix d'action ferez vous ?", choixTour, null, true);
            } else {
                inputTour = choisir("qu'elle choix d'action ferez vous ?", choixTour, boutons, false);
            }
            if (fin && inputTour.equals("")) tour = false;
            if (inputTour.equals("WAGON")) {
                cartesTransport.add(jeu.piocherCarteWagon());
                compteur++;
                fin = true;
                if (compteur >= 2) {
                    compteur = 0;
                    fin = false;
                    tour = false;
                }
            }
            if (inputTour.equals("BATEAU")) {
                cartesTransport.add(jeu.piocherCarteBateau());
                compteur++;
                fin = true;
                if (compteur >= 2) {
                    compteur = 0;
                    fin = false;
                    tour = false;
                }
            }

            if (choixcarteVisible.contains(inputTour)) {
                List<CarteTransport> carteTransportTemp = new ArrayList<>();
                for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                    if (carteTransport.getNom().equals(inputTour)) {
                        carteTransportTemp.add(carteTransport);
                    }
                }
                if (compteur == 0) {
                    if (carteTransportTemp.get(0).getType().equals(TypeCarteTransport.JOKER)) {
                        choisirPileJoueur(cartesTransport, carteTransportTemp);
                        compteur += 2;
                        fin = false;
                        tour = false;
                    }
                }
                if (compteur < 2) {
                    if (carteTransportTemp.get(0).getType().equals(TypeCarteTransport.BATEAU) || carteTransportTemp.get(0).getType().equals(TypeCarteTransport.WAGON)) {
                        choisirPileJoueur(cartesTransport, carteTransportTemp);
                        compteur++;
                        fin = true;
                    }
                }
                if (compteur >= 2) {
                    compteur = 0;
                    fin = false;
                    tour = false;
                }
            }
            //si inputTour == "PIONS WAGON" "PIONS BATEAU"
            if (!fin) {
                //TODO changer systeme echange
                if (inputTour.equals("PIONS WAGON") || inputTour.equals("PIONS BATEAU")) {
                    if (inputTour.equals("PIONS WAGON")) {
                        if (nbPionsWagonEnReserve > 0 && nbPionsBateau > 0) {
                            List<String> choixPionsWagon = new ArrayList<>();
                            if (nbPionsWagonEnReserve > nbPionsBateau) {
                                for (int i = 0; i <= nbPionsBateau; i++) {
                                    choixPionsWagon.add(String.valueOf(i));
                                }
                            } else {
                                for (int i = 0; i <= nbPionsWagonEnReserve; i++) {
                                    choixPionsWagon.add(String.valueOf(i));
                                }
                                String inputPionsWagon = choisir("combien de pions wagon voulez vous échanger ? Au maximum " + choixPionsWagon.get(choixPionsWagon.size() - 1), choixPionsWagon, null, false);
                                nbPionsWagon += Integer.parseInt(inputPionsWagon);
                                nbPionsWagonEnReserve -= Integer.parseInt(inputPionsWagon);
                                nbPionsBateau -= Integer.parseInt(inputPionsWagon);
                                nbPionsBateauEnReserve += Integer.parseInt(inputPionsWagon);
                                score -= Integer.parseInt(inputPionsWagon);
                                tour = false;
                            }
                        } else {
                            if (nbPionsWagonEnReserve == 0)
                                log("vous ne pouvez pas échanger de pions wagon");
                            if (nbPionsBateau == 0)
                                log("vous ne pouvez pas échanger de pions wagon");
                        }
                    } else if (inputTour.equals("PIONS BATEAU")) {
                        if (nbPionsBateauEnReserve > 0 && nbPionsWagon > 0) {
                            List<String> choixPionsBateau = new ArrayList<>();
                            if (nbPionsBateauEnReserve > nbPionsWagon) {
                                for (int i = 0; i <= nbPionsWagon; i++) {
                                    choixPionsBateau.add(String.valueOf(i));
                                }
                            } else {
                                for (int i = 0; i <= nbPionsBateauEnReserve; i++) {
                                    choixPionsBateau.add(String.valueOf(i));
                                }
                            }
                            String inputPionsBateau = choisir("combien de pions bateau voulez vous échanger ? Au maximum " + choixPionsBateau.get(choixPionsBateau.size() - 1), choixPionsBateau, null, false);
                            nbPionsWagon -= Integer.parseInt(inputPionsBateau);
                            nbPionsWagonEnReserve += Integer.parseInt(inputPionsBateau);
                            nbPionsBateau += Integer.parseInt(inputPionsBateau);
                            nbPionsBateauEnReserve -= Integer.parseInt(inputPionsBateau);
                            score -= Integer.parseInt(inputPionsBateau);
                            tour = false;

                        }
                    } else {
                        System.out.println("vous n'avez pas de pions wagon en reserve");
                    }
                }
            }
            if (!fin) {
                if (inputTour.equals("DESTINATION")) {
                    //TODO : A Opimiser pour pouvoir l'appeler depuis jeu
                    List<Bouton> boutonChoixDestination = new ArrayList<>();
                    List<Destination> destinationTemp = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        System.out.println("destination " + jeu.getPileDestinations().get(0) + " : " + jeu.getPileDestinations().get(0).getNom());
                        destinationTemp.add(jeu.piocherDestination());
                        destinations.add(destinationTemp.get(i));
                    }
                    for (Destination d : destinationTemp) {
                        boutonChoixDestination.add(new Bouton(d.toString(), d.getNom()));
                    }
                    List<String> choixDestinaton = new ArrayList<>();
                    for (Destination d : getDestinations()) {
                        choixDestinaton.add(d.getNom());
                    }
                    int compteurRemove = 0;
                    while (compteurRemove <= 2) {
                        String inputChoix = choisir("choisissez une destination a retirer :", choixDestinaton, boutonChoixDestination, true);
                        System.out.println(inputChoix);
                        if (inputChoix.isEmpty()) {
                            compteurRemove = 3;
                        } else if (choixDestinaton.contains(inputChoix)) {
                            for (Destination d : destinationTemp) {
                                if (d.getNom().equals(inputChoix)) {
                                    choixDestinaton.remove(inputChoix);
                                    System.out.println("destination a retirer : " + d.getNom());
                                    getDestinations().remove(d);
                                    for (Bouton b : boutonChoixDestination) {
                                        if (b.valeur().equals(d.getNom())) {
                                            boutonChoixDestination.remove(b);
                                            break;
                                        }
                                    }
                                    jeu.getPileDestinations().add(d);
                                    destinationTemp.remove(d);
                                    compteurRemove++;
                                    break;
                                }
                            }
                        }
                    }
                    tour = false;
                }
            }
            if (!fin) {
                if (choixRoute.contains(inputTour)) {
                    //TODO : CHANGER CAPTURE ROUTE
                    List<CarteTransport> carteCorrecte = new ArrayList<>();
                    //captureRoute Terrestre
                    int cpt = 0;
                    for (Route r : jeu.getRoutesLibres()) {
                        if (r.getNom().equals(inputTour) && r instanceof RouteTerrestre) {
                            System.out.println(r.getCouleur());
                            for (CarteTransport c : cartesTransport) {
                                System.out.println(c.getCouleur() + " " + c.getType());
                                System.out.println(c.getCouleur().equals(r.getCouleur()));
                                if (c.getCouleur().equals(r.getCouleur()) && (c.getType().equals(TypeCarteTransport.WAGON) || c.getType().equals(TypeCarteTransport.JOKER))) {
                                    System.out.println(c.getNom());
                                    System.out.println(c.getCouleur());
                                    carteCorrecte.add(c);
                                    cpt++;
                                }
                            }
                            if (cpt > r.getLongueur()) {
                                //demander au joueur de choisir les cartes qui vont bien puis compter le nombre de carte tant que le joueur n'a pas selectionner le bon nombre de carte pour capturer la route on lui redemande
                                List<String> choixCarteWagon = new ArrayList<>();
                                for (CarteTransport c : carteCorrecte) {
                                    choixCarteWagon.add(c.getNom());
                                }
                                do {
                                    String inputChoix = choisir("choisissez les cartes qui vont bien pour capturer la route :", choixCarteWagon, null, false);
                                    for (CarteTransport c : carteCorrecte) {
                                        if (c.getNom().equals(inputTour)) {
                                            cartesTransportPosees.add(c);
                                            carteCorrecte.remove(c);
                                        }
                                    }
                                }
                                while (compterCarte(cartesTransportPosees, r.getLongueur()));
                                log("vous avez capturé la route " + r.getNom());
                                cartesTransport.removeAll(cartesTransportPosees);
                                cartesTransportPosees.clear();
                                routes.add(r);
                                jeu.getRoutesLibres().remove(r);
                                cpt = 0;
                                tour = false;
                            } else {
                                log("vous n'avez pas assez de carte de la bonne couleur");
                            }
                        } else if (r.getNom().equals(inputTour) && r instanceof RouteMaritime) {
                            for (CarteTransport c : cartesTransport) {
                                if (c.getCouleur().equals(r.getCouleur()) && (c.getType().equals(TypeCarteTransport.BATEAU) || c.getType().equals(TypeCarteTransport.JOKER))) {
                                    if (c.estDouble()) {
                                        carteCorrecte.add(c);
                                        cpt += 2;
                                    } else {
                                        carteCorrecte.add(c);
                                        cpt++;
                                    }
                                }
                            }
                            if (cpt > r.getLongueur()) {
                                //demander au joueur de choisir les cartes qui vont bien puis compter le nombre de carte tant que le joueur n'a pas selectionner le bon nombre de carte pour capturer la route on lui redemande
                                List<String> choixCarteBateau = new ArrayList<>();
                                for (CarteTransport c : carteCorrecte) {
                                    choixCarteBateau.add(c.getNom());
                                }
                                do {
                                    String inputChoix = choisir("choisissez les cartes qui vont bien pour capturer la route :", choixCarteBateau, null, false);
                                    for (CarteTransport c : carteCorrecte) {
                                        if (c.getNom().equals(inputTour)) {
                                            cartesTransportPosees.add(c);
                                            carteCorrecte.remove(c);
                                        }
                                    }
                                }
                                while (compterCarte(cartesTransportPosees, r.getLongueur()));
                                log("vous avez capturé la route " + r.getNom());
                                cartesTransport.removeAll(cartesTransportPosees);
                                cartesTransportPosees.clear();
                                routes.add(r);
                                jeu.getRoutesLibres().remove(r);
                                cpt = 0;
                                tour = false;
                            } else {
                                log("vous n'avez pas assez de carte de la bonne couleur");
                            }
                        }
                    }
                }
            }
            if (!fin) {
                if (choixPort.contains(inputTour)) {
                    //a completer
                    tour = false;
                }
            }

        } while (tour);
    }

    private boolean compterCarte(List<CarteTransport> carteTransportPosees, int longueurRoute) {
        int cpt = 0;
        for (CarteTransport c : carteTransportPosees) {
            if (c.estDouble()) {
                cpt += 2;
            } else {
                cpt++;
            }
        }
        if (cpt == longueurRoute) {
            return false;
        } else {
            return true;
        }
    }

    public void choisirPileJoueur(List<CarteTransport> cartesTransport, List<CarteTransport> carteTransportTemp) {
        cartesTransport.add(carteTransportTemp.get(0));
        jeu.getCartesTransportVisibles().remove(carteTransportTemp.get(0));
        carteTransportTemp.remove(0);
        String choixPioche = choisir("Quelle carte voulez vous piocher ?", Arrays.asList("WAGON", "BATEAU"), null, false);
        if (choixPioche.equals("WAGON")) {
            jeu.getCartesTransportVisibles().add(jeu.piocherCarteWagon());
        } else {
            jeu.getCartesTransportVisibles().add(jeu.piocherCarteBateau());
        }
    }

    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     * <p>
     * Cette méthode lit les entrées du jeu (`Jeu.lireligne()`) jusqu'à ce
     * qu'un choix valide (un élément de `choix` ou de `boutons` ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     * <p>
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     * <p>
     * ```
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez-vous faire ceci ?", choix, null, false);
     * ```
     * <p>
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     * <p>
     * ```
     * List<Bouton> boutons = Arrays.asList(new Bouton("Un", "1"), new Bouton("Deux", "2"), new Bouton("Trois", "3"));
     * String input = choisir("Choisissez un nombre.", null, boutons, false);
     * ```
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de `Bouton` représentés par deux String (label,
     *                    valeur) correspondant aux choix valides attendus du joueur
     *                    qui doivent être représentés par des boutons sur
     *                    l'interface graphique (le label est affiché sur le bouton,
     *                    la valeur est ce qui est envoyé au jeu quand le bouton est
     *                    cliqué)
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élement de `choix`, ou la valeur
     * d'un élément de `boutons` ou la chaîne vide)
     */
    public String choisir(
            String instruction,
            Collection<String> choix,
            Collection<Bouton> boutons,
            boolean peutPasser) {
        if (choix == null)
            choix = new ArrayList<>();
        if (boutons == null)
            boutons = new ArrayList<>();

        HashSet<String> choixDistincts = new HashSet<>(choix);
        choixDistincts.addAll(boutons.stream().map(Bouton::valeur).toList());
        if (peutPasser || choixDistincts.isEmpty()) {
            choixDistincts.add("");
        }

        String entree;
        // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
        while (true) {
            jeu.prompt(instruction, boutons, peutPasser);
            entree = jeu.lireLigne();
            // si une réponse valide est obtenue, elle est renvoyée
            if (choixDistincts.contains(entree)) {
                return entree;
            }
        }
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Wagons: %d  Bateaux: %d", nbPionsWagon, nbPionsBateau));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    boolean destinationEstComplete(Destination d) {
        //a completer
        return false;
    }

    //TODO calculerScoreFinal()
    public int calculerScoreFinal() {
        return 1;
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un dictionnaire de
     * valeurs sérialisables
     * (qui sera converti en JSON pour l'envoyer à l'interface graphique)
     */
    Map<String, Object> dataMap() {
        return Map.ofEntries(
                Map.entry("nom", nom),
                Map.entry("couleur", couleur),
                Map.entry("score", score),
                Map.entry("pionsWagon", nbPionsWagon),
                Map.entry("pionsWagonReserve", nbPionsWagonEnReserve),
                Map.entry("pionsBateau", nbPionsBateau),
                Map.entry("pionsBateauReserve", nbPionsBateauEnReserve),
                Map.entry("destinationsIncompletes",
                        destinations.stream().filter(d -> !destinationEstComplete(d)).toList()),
                Map.entry("destinationsCompletes", destinations.stream().filter(this::destinationEstComplete).toList()),
                Map.entry("main", cartesTransport.stream().sorted().toList()),
                Map.entry("inPlay", cartesTransportPosees.stream().sorted().toList()),
                Map.entry("ports", ports.stream().map(Ville::nom).toList()),
                Map.entry("routes", routes.stream().map(Route::getNom).toList()));
    }

    public enum CouleurJouer {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }
}


package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.data.*;

import java.lang.reflect.Type;
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

    public void setNbPionsWagon(int nbPionsWagon) {
        this.nbPionsWagon = nbPionsWagon;
    }

    public void setNbPionsBateau(int nbPionsBateau) {
        this.nbPionsBateau = nbPionsBateau;
    }

    public int getNbPionsWagon() {
        return nbPionsWagon;
    }

    public int getNbPionsBateau() {
        return nbPionsBateau;
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
        do {
            //clear the log
            List<Bouton> boutons = Arrays.asList(
                    new Bouton("Echanger pions wagons", "PIONS WAGON"),
                    new Bouton("Echanger pions bateaux", "PIONS BATEAU")
                    );
            List<String> choixTour = Arrays.asList("WAGON","BATEAU", "DESTINATION");
            List<String> carteVisible = new ArrayList<>();
            for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                carteVisible.add(carteTransport.getNom());
            }
            for (Route route : routes) {
                choixTour.add(route.getNom());
            }
            for (Ville ville : ports) {
                if (ville.estPort()){
                    choixTour.add(ville.nom());
                }
            }
            for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                choixTour.add(carteTransport.getNom());
            }
            String inputTour = choisir("qu'elle choix d'action ferez vous ?", choixTour, boutons, false);
            System.out.println(inputTour);
            switch (inputTour) {
                case "WAGON" -> {
                    int size = cartesTransport.size();
                    if (jeu.piocherCarteWagon().getType().equals("JOKER")){
                        cartesTransport.add(jeu.piocherCarteWagon());
                        break;
                    }
                    else {
                        cartesTransport.add(jeu.piocherCarteWagon());
                        tour = size == cartesTransport.size() || size == (cartesTransport.size() + 2);
                    }
                }
                case "BATEAU" -> {
                    int size = cartesTransport.size();
                    cartesTransport.add(jeu.piocherCarteBateau());
                    tour = size == cartesTransport.size() || size == (cartesTransport.size() + 2);
                }
                //si carte visible.contains(inputTour) alors on la prend et on la met dans la main du joueur et on la retire de la liste des cartes visibles
                //TODO: CASE CARTEVISIBLE
//                case carteVisible -> {
//                    int size = cartesTransport.size();
//                    for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
//                        if (carteTransport.getNom().equals(inputTour)) {
//                            cartesTransport.add(carteTransport);
//                            jeu.getCartesTransportVisibles().remove(carteTransport);
//                            break;
//                        }
//                    }
//                    tour = size == cartesTransport.size() || size == (cartesTransport.size() + 2);
//                }
                case "PIONS WAGON", "PIONS BATEAU" -> {
                    if (inputTour.equals("PIONS WAGON")){
                        //a completer
                    }
                }
                case "DESTINATIONS" -> {
                        destinations.add(jeu.piocherDestination());
                        tour = false;
                }
                case "Capturer une route" -> {
                    //TODO : capturer une route a completer
                    List<Bouton> boutonRoute = Arrays.asList(
                            new Bouton("Capturer une route"),
                            new Bouton("Ne rien capturer"));

                    List<String> choixRoute = Arrays.asList("Capturer la première route", "Capturer la deuxième route");
                    String inputRoute = choisir("que voulez-vous capturer ?", choixRoute, boutonRoute, false);

                    if (inputRoute.equals("Capturer une route")) {
                        List<String> choix = new ArrayList<>();
                        for (Route route : jeu.getRoutesLibres()) {
                            choix.add(route.getNom());
                        }
                        do{
                        String inputRoute2 = choisir("quelle route voulez-vous capturer ?", choix, null, false);
                        int nbC = 0;
                        for (Route route : jeu.getRoutesLibres()) {
                            if (route.getNom().equals(inputRoute2)) {
                                for (CarteTransport carteTransport : cartesTransport) {
                                    if (carteTransport.getCouleur().equals(route.getCouleur()) || carteTransport.getType().equals(TypeCarteTransport.JOKER)) {
                                        nbC++;
                                    }
                                }
                                if (nbC >= route.getLongueur()) {
                                    routes.add(route);
                                    //on defausse les cartes de la bonne couleur pour capturer la route
                                    //TODO : a optimiser
                                    //faire en sorte que si la carte estDouble cela compte pour deux
                                    for (int i = 0; i < route.getLongueur(); i++) {
                                        for (CarteTransport carteTransport : cartesTransport) {
                                            if (carteTransport.getCouleur().equals(route.getCouleur()) || carteTransport.getType().equals(TypeCarteTransport.JOKER)) {
                                                cartesTransport.remove(carteTransport);
                                                if (carteTransport.getType().equals(TypeCarteTransport.WAGON)){
                                                    jeu.getPilesDeCartesWagon().defausser(carteTransport);
                                                    jeu.getCartesTransportVisibles().remove(carteTransport);
                                                    nbPionsWagon--;
                                                }
                                                else if (carteTransport.getType().equals(TypeCarteTransport.BATEAU)){
                                                    jeu.getPilesDeCartesBateau().defausser(carteTransport);
                                                    jeu.getCartesTransportVisibles().remove(carteTransport);
                                                    nbPionsBateau--;
                                                }
                                                else if (carteTransport.getType().equals(TypeCarteTransport.JOKER)){
                                                    if (route.getClass().equals(RouteMaritime.class)){
                                                        jeu.getPilesDeCartesBateau().defausser(carteTransport);
                                                        jeu.getCartesTransportVisibles().remove(carteTransport);
                                                        nbPionsBateau--;
                                                    }
                                                    else{
                                                        jeu.getPilesDeCartesWagon().defausser(carteTransport);
                                                        jeu.getCartesTransportVisibles().remove(carteTransport);
                                                        nbPionsWagon--;
                                                    }

                                                }
                                                break;
                                            }
                                        }
                                    }
                                    log("vous avez capturer la route : " + inputRoute2);
                                    System.out.println("routes : " + routes);
                                    tour = false;
                                } else {
                                    log("vous n'avez pas assez de cartes de la bonne couleur");
                                    log("vous avez : " + nbC + " cartes de la bonne couleur");
                                    log("il vous en faut : " + route.getLongueur());
                                    log("veuillez choisir une autre route");
                                    tour = true;
                                }
                            }
                        }
                        }while(tour);
                    } else log("vous n'avez rien capturer");
                }

                case "Construire un port" -> {
                    List<Bouton> boutonPort = Arrays.asList(
                            new Bouton("Construire un port"),
                            new Bouton("Ne rien construire"));
                    List<String> choixPort = Arrays.asList("Construire", "Ne pas construire");
                    String inputPort = choisir("que voulez-vous construire ?", choixPort, boutonPort, false);
                    if (inputPort.equals("Construire un port")) {
                        //a completer
                        tour = false;
                    } else log("vous n'avez rien construire");
                }
            }
            } while (tour) ;
        }
    public void piocherCarte() {
        List<Bouton> boutonPioche = Arrays.asList(
                new Bouton("Piocher une carte visible"),
                new Bouton("Piocher une carte"),
                new Bouton("Retour"));
        List<String> choixPioche = Arrays.asList("Piocher une carte visible", "Piocher une carte wagon", "Retour");

        String inputPioche = choisir("que voulez-vous piochez ?", choixPioche, boutonPioche, false);

        switch (inputPioche) {
            case "Piocher une carte visible" -> {
                List<String> choixPiocheCarte = new ArrayList<>();
                //remplir la liste des choix avec les cartes visibles
                for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                    choixPiocheCarte.add(carteTransport.getNom());
                }
                //permet de choisir la carte a piocher
                String inputPiocheCarte = choisir("qu'elle carte voulez-vous piochez ?", choixPiocheCarte, null, false);
                for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                    if (carteTransport.getNom().equals(inputPiocheCarte)) {
                        cartesTransport.add(carteTransport);
                        jeu.getCartesTransportVisibles().remove(carteTransport);
                        break;
                    }
                }
            }
            case "Piocher une carte" -> {
                //permet de choisir le type de carte a piocher
                List<String> choixPiocheCarte = Arrays.asList("WAGON", "BATEAU");
                String inputPiocheCarte = choisir("qu'elle type de carte voulez-vous piochez ?", choixPiocheCarte, null, false);

                if (inputPiocheCarte.equals("WAGON")) {
                    cartesTransport.add(jeu.piocherCarteWagon());
                } else if (inputPiocheCarte.equals("BATEAU")){
                    cartesTransport.add(jeu.piocherCarteBateau());
                }
            }
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
        List<String> villes = new ArrayList<>();
        for (Route r : routes) {
            if (!villes.contains(r.getVille1().nom())) villes.add(r.getVille1().nom());
            if (!villes.contains(r.getVille2().nom())) villes.add(r.getVille2().nom());
        }
        List<List<Integer>> tabSucc = new ArrayList<>();
        boolean[][] matrice = new boolean[villes.size()][villes.size()];
        for (Route r : routes) {
            matrice[villes.indexOf(r.getVille1().nom())][villes.indexOf(r.getVille2().nom())] = true;
            matrice[villes.indexOf(r.getVille2().nom())][villes.indexOf(r.getVille1().nom())] = true;
        }
        for (int i = 0; i < villes.size(); i++) {
            List<Integer> succ = new ArrayList<>();
            for (int j = 0; j < villes.size(); j++)
                if (matrice[i][j]) succ.add(j);
            tabSucc.add(succ);
        }
        List<Integer> descendant = new ArrayList<>();
        if (tabSucc.size() > 0) descendant.add(villes.indexOf(d.getVilles().get(0)));
        for (int j = 0; j < descendant.size(); j++)
            if (!descendant.contains(j)) descendant.addAll(tabSucc.get(j));
        return descendant.contains(villes.indexOf(d.getVilles().get(1)));
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

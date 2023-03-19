package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.data.*;

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

    private boolean tour = true;
    private boolean fin = false;
    private int compteur = 0;

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
        do {
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
                inputTour = choisir("<h3>Qu'elle choix d'action ferez vous?</h3>", choixTour, null, true);
            } else {
                inputTour = choisir("<h3>Qu'elle choix d'action ferez vous?</h3>", choixTour, boutons, false);
            }


            if (fin && inputTour.equals("")) tour = false;
            if (inputTour.equals("WAGON") || inputTour.equals("BATEAU"))
                joueurPiocheDansLeDeck(cartesTransport, inputTour);
            if (choixcarteVisible.contains(inputTour)) joueurPiocheCarteVisible(cartesTransport, inputTour);

            if (!fin) {
                if (inputTour.equals("PIONS WAGON") || inputTour.equals("PIONS BATEAU")) echangerPions(inputTour);
                if (inputTour.equals("DESTINATION")) prendreDestination();
                if (choixRoute.contains(inputTour)) capturerRoute(inputTour);
                if (choixPort.contains(inputTour)) {
                    //a completer
                    tour = false;
                }
            }
        } while (tour);
    }

    private void capturerRoute(String inputTour) {
        //TODO : CHANGER CAPTURE ROUTE
        List<CarteTransport> carteCorrecte = new ArrayList<>();
        //captureRoute Terrestre
        int cpt = 0;
        List<Route> routeTemp = new ArrayList<>(jeu.getRoutesLibres());
        for (Route r : routeTemp) {
            if (r.getNom().equals(inputTour) && r instanceof RouteTerrestre) {
                System.out.println(r.getCouleur());
                for (CarteTransport c : cartesTransport) {
                    if (!r.getCouleur().equals(Couleur.GRIS)) {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            System.out.println(c.getNom());
                            System.out.println(c.getCouleur());
                            carteCorrecte.add(c);
                            cpt++;
                        } else if (c.getCouleur().equals(r.getCouleur()) && c.getType().equals(TypeCarteTransport.WAGON)) {
                            System.out.println(c.getNom());
                            System.out.println(c.getCouleur());
                            carteCorrecte.add(c);
                            cpt++;
                        }
                    } else {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            System.out.println(c.getNom());
                            System.out.println(c.getCouleur());
                            carteCorrecte.add(c);
                            cpt++;
                        } else if (c.getType().equals(TypeCarteTransport.WAGON)) {
                            System.out.println(c.getNom());
                            System.out.println(c.getCouleur());
                            carteCorrecte.add(c);
                            cpt++;
                        }
                    }
                }
                if (cpt >= r.getLongueur()) {
                    //demander au joueur de choisir les cartes qui vont bien puis compter le nombre de carte tant que le joueur n'a pas selectionner le bon nombre de carte pour capturer la route on lui redemande
                    List<String> choixCarteWagon = new ArrayList<>();
                    for (CarteTransport c : carteCorrecte) {
                        choixCarteWagon.add(c.getNom());
                    }
                    do {
                        String inputChoix = choisir("choisissez les cartes pour capturer la route :", choixCarteWagon, null, false);
                        for (CarteTransport c : carteCorrecte) {
                            if (c.getNom().equals(inputChoix)) {
                                cartesTransportPosees.add(c);
                                cartesTransport.remove(c);
                                choixCarteWagon.remove(inputChoix);
                            }
                        }
                    }
                    while (compterCarte(cartesTransportPosees) < r.getLongueur());
                    conditionGagnante(r, cpt);
                } else {
                    log("vous n'avez pas assez de carte de la bonne couleur");
                }
            } else if (r.getNom().equals(inputTour) && r instanceof RouteMaritime) {
                for (CarteTransport c : cartesTransport) {
                    if (!r.getCouleur().equals(Couleur.GRIS)) {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            System.out.println(c.getNom());
                            System.out.println(c.getCouleur());
                            carteCorrecte.add(c);
                            cpt++;
                        } else if (c.getCouleur().equals(r.getCouleur()) && (c.getType().equals(TypeCarteTransport.BATEAU))) {
                            if (c.estDouble()) {
                                carteCorrecte.add(c);
                                cpt += 2;
                            } else {
                                carteCorrecte.add(c);
                                cpt++;
                            }
                        }
                    } else {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            System.out.println(c.getNom());
                            System.out.println(c.getCouleur());
                            carteCorrecte.add(c);
                            cpt++;
                        } else if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                            if (c.estDouble()) {
                                carteCorrecte.add(c);
                                cpt += 2;
                            } else {
                                carteCorrecte.add(c);
                                cpt++;
                            }
                        }
                    }
                }
                int compteurCarteDouble = 0;
                for (CarteTransport c : carteCorrecte) {
                    if (c.estDouble()) {
                        compteurCarteDouble++;
                    }
                }
                if (compteurCarteDouble == carteCorrecte.size()) {
                    if (r.getLongueur() % 2 == 0) {
                        cpt = r.getLongueur();
                    } else {
                        log("vous possedez que des cartes doubles");
                        log("vous ne pouvez pas capturer cette route car sa longueur est impaire");
                        cpt = 0;
                    }
                }
                if (cpt >= r.getLongueur()) {
                    //demander au joueur de choisir les cartes qui vont bien puis compter le nombre de carte tant que le joueur n'a pas selectionner le bon nombre de carte pour capturer la route on lui redemande
                    List<String> choixCarteBateau = new ArrayList<>();
                    for (CarteTransport c : carteCorrecte) {
                        choixCarteBateau.add(c.getNom());
                    }
                    do {
                        if (r.getLongueur() - compterCarte(cartesTransportPosees) <= 1) {
                            for (CarteTransport c : carteCorrecte) {
                                if (c.estDouble()) {
                                    choixCarteBateau.remove(c.getNom());
                                }
                            }
                        }
                        String inputChoix = choisir("choisissez les cartes qui vont bien pour capturer la route :", choixCarteBateau, null, false);
                        for (CarteTransport c : carteCorrecte) {
                            if (c.getNom().equals(inputChoix)) {
                                cartesTransportPosees.add(c);
                                cartesTransport.remove(c);
                                choixCarteBateau.remove(inputChoix);
                            }
                        }
                    }
                    while (compterCarte(cartesTransportPosees) < r.getLongueur());
                    log("vous avez capturé la route " + r.getNom());
                    for (CarteTransport c : cartesTransportPosees) {
                        jeu.getPilesDeCartesBateau().defausser(c);
                    }
                    score += r.getScore();
                    cartesTransportPosees.clear();
                    routes.add(r);
                    if (jeu.getJoueurs().size() <= 3) {
                        jeu.getRoutesLibres().remove(r.getRouteParallele());
                    }
                    jeu.getRoutesLibres().remove(r);
                    cpt = 0;
                    tour = false;
                } else if (cpt != 0) log("vous n'avez pas assez de carte de la bonne couleur");
            }
            if (r.getNom().equals(inputTour) && r instanceof RoutePaire) {
                List<Integer> nbcarteCouleur = new ArrayList<>();
                List<Integer> OpposerNbCarteCouleur = new ArrayList<>();
                for (Couleur c : Couleur.values()) {
                    nbcarteCouleur.add(0);
                    OpposerNbCarteCouleur.add(0);
                }
                System.out.println(r.getCouleur());
                for (CarteTransport c : cartesTransport) {
                    if (c.getType().equals(TypeCarteTransport.JOKER) || c.getType().equals(TypeCarteTransport.WAGON)) {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            //ajoute un 1 dans la liste nbCarteCouleur
                            for (Couleur couleur : Couleur.values()) {
                                nbcarteCouleur.set(couleur.ordinal(), nbcarteCouleur.get(couleur.ordinal()) + 1);
                            }
                            System.out.println("joker");
                            System.out.println(nbcarteCouleur.toString());
                        }
                        nbcarteCouleur.set(c.getCouleur().ordinal(), nbcarteCouleur.get(c.getCouleur().ordinal()) + 1);
                        System.out.println(nbcarteCouleur.toString());

                    }
                }
                nbcarteCouleur.remove(Couleur.GRIS.ordinal());
                OpposerNbCarteCouleur.remove(Couleur.GRIS.ordinal());
                System.out.println(nbcarteCouleur.toString());
                System.out.println("--------------------");
                for (int i = 0; i < nbcarteCouleur.size(); i++) {
                    if (nbcarteCouleur.get(i) >= 2) {
                        for (CarteTransport c : cartesTransport) {
                            if (c.getCouleur().equals(Couleur.values()[i]) && c.getType().equals(TypeCarteTransport.WAGON)) {
                                carteCorrecte.add(c);
                                OpposerNbCarteCouleur.set(i, OpposerNbCarteCouleur.get(i) + 1);
                                cpt++;
                                nbcarteCouleur.set(i, nbcarteCouleur.get(i) - 1);
                                System.out.println(nbcarteCouleur.toString());
                            }
                        }
                    }
                }
                for (CarteTransport c : cartesTransport) {
                    if (c.getType().equals(TypeCarteTransport.JOKER)) {
                        carteCorrecte.add(c);
                        cpt++;
                    }
                }
                cpt /= 2;
                System.out.println(carteCorrecte.toString());
                if (cpt >= r.getLongueur()) {
                    //demander au joueur de choisir les cartes qui vont bien puis compter le nombre de carte tant que le joueur n'a pas selectionner le bon nombre de carte pour capturer la route on lui redemande
                    List<String> choixCarteWagon = new ArrayList<>();
                    List<Couleur> DebutCouleur = new ArrayList<>();
                    List<Integer> nbCouleurValide = new ArrayList<>();
                    List<CarteTransport> carteCorrecteTemp = new ArrayList<>();
                    List<CarteTransport> lastcarteJoker = new ArrayList<>();
                    int nbCouleur = 0;
                    int nbValide = 0;
                    int indexToRemove = -1;
                    boolean removeAllOne = false;
                    for (int i = 0; i < OpposerNbCarteCouleur.size(); i++) {
                        if (OpposerNbCarteCouleur.get(i) == 0) {
                            nbcarteCouleur.set(i, 0);
                        }
                    }
                    for (CarteTransport c : carteCorrecte) {
                        choixCarteWagon.add(c.getNom());
                    }
                    for (int i = 0; i < r.getLongueur(); i++) {
                        nbCouleurValide.add(0);
                    }
                    do {
                        String inputChoix = choisir("choisissez les cartes pour capturer la route :", choixCarteWagon, null, false);
                        for (CarteTransport c : carteCorrecte) {
                            if (c.getNom().equals(inputChoix)) {

                                if (c.getType().equals(TypeCarteTransport.JOKER)) {
                                    choixCarteWagon.remove(inputChoix);
                                    carteCorrecteTemp.add(c);
                                    cartesTransportPosees.add(c);
                                    cartesTransport.remove(c);
                                    lastcarteJoker.add(c);
                                    for (int i = 0; i < nbCouleurValide.size(); i++) {
                                        nbCouleurValide.set(i, nbCouleurValide.get(i) + 1);
                                    }
                                } else {
                                    if (!DebutCouleur.contains(c.getCouleur()) && nbCouleur < r.getLongueur()) {
                                        DebutCouleur.add(c.getCouleur());
                                        nbCouleurValide.set(DebutCouleur.indexOf(c.getCouleur()), nbCouleurValide.get(DebutCouleur.indexOf(c.getCouleur())) + 1);
                                        cartesTransportPosees.add(c);
                                        cartesTransport.remove(c);
                                        choixCarteWagon.remove(inputChoix);
                                        carteCorrecteTemp.add(c);
                                        nbCouleur++;
                                        for (int i = 0; i < nbcarteCouleur.size(); i++) {
                                            if (nbcarteCouleur.get(i) >= 1 && c.getCouleur().equals(Couleur.values()[i])) {
                                                removeAllOne = true;
                                                for (CarteTransport ct : carteCorrecte) {
                                                    if (!ct.getCouleur().equals(Couleur.values()[i]) && ct.getType().equals(TypeCarteTransport.WAGON) && OpposerNbCarteCouleur.get(ct.getCouleur().ordinal()) == 1) {
                                                        carteCorrecteTemp.add(ct);
                                                        choixCarteWagon.remove(ct.getNom());
                                                        nbcarteCouleur.set(ct.getCouleur().ordinal(), nbcarteCouleur.get(ct.getCouleur().ordinal()) - 1);
                                                        OpposerNbCarteCouleur.set(ct.getCouleur().ordinal(), OpposerNbCarteCouleur.get(ct.getCouleur().ordinal()) - 1);
                                                    }
                                                }
                                            }
                                        }
                                    } else if (DebutCouleur.contains(c.getCouleur())) {
                                        nbCouleurValide.set(DebutCouleur.indexOf(c.getCouleur()), nbCouleurValide.get(DebutCouleur.indexOf(c.getCouleur())) + 1);
                                        cartesTransportPosees.add(c);
                                        cartesTransport.remove(c);
                                        choixCarteWagon.remove(inputChoix);
                                        carteCorrecteTemp.add(c);
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < nbCouleurValide.size(); i++) {
                            if (nbCouleurValide.get(i) >= 2) {
                                nbValide++;
                                indexToRemove = i;
                            }
                        }
                        if (removeAllOne) {
                            for (CarteTransport ct : carteCorrecteTemp) {
                                choixCarteWagon.remove(ct.getNom());
                            }
                            carteCorrecte.removeAll(carteCorrecteTemp);
                            removeAllOne = false;
                            carteCorrecteTemp.clear();
                        }
                        if (indexToRemove != -1) {
                            if (lastcarteJoker.size() != 0) {
                                for (CarteTransport c : lastcarteJoker) {
                                    for (int i = 0; i < nbCouleurValide.size(); i++) {
                                        nbCouleurValide.set(i, nbCouleurValide.get(i) - 1);
                                    }
                                    choixCarteWagon.remove(c.getNom());
                                }
                                lastcarteJoker.clear();
                            }
                            nbCouleurValide.remove(indexToRemove);
                            DebutCouleur.remove(indexToRemove);
                            indexToRemove = -1;
                        }
                    }
                    while (nbValide < r.getLongueur());
                    for (CarteTransport c : cartesTransportPosees) {
                        jeu.getPilesDeCartesWagon().defausser(c);
                    }
                    score += r.getScore();
                    cartesTransportPosees.clear();
                    routes.add(r);
                    System.out.println("nb joueur : " + jeu.getJoueurs().size());
                    if (jeu.getJoueurs().size() <= 3) {
                        System.out.println("route parallele : " + r.getRouteParallele());
                        System.out.println("nb route libres : " + jeu.getRoutesLibres().size());
                        jeu.getRoutesLibres().remove(r.getRouteParallele());
                        System.out.println("nb route libres apres retrait: " + jeu.getRoutesLibres().size());
                        System.out.println("nb route libres : " + jeu.getRoutesLibres());
                    }
                    jeu.getRoutesLibres().remove(r);
                    cpt = 0;
                    tour = false;
                } else {
                    log("vous n'avez pas assez de carte de la bonne couleur");
                }
            }
        }
    }

    private int compterCarte(List<CarteTransport> carteTransportPosees) {
        int cpt = 0;
        for (CarteTransport c : carteTransportPosees) {
            if (c.estDouble()) {
                cpt += 2;
            } else {
                cpt++;
            }
        }
        return cpt;
    }

    private void conditionGagnante(Route r, int cpt) {
        for (CarteTransport c : cartesTransportPosees) {
            jeu.getPilesDeCartesWagon().defausser(c);
        }
        score += r.getScore();
        cartesTransportPosees.clear();
        routes.add(r);
        System.out.println("nb joueur : " + jeu.getJoueurs().size());
        if (jeu.getJoueurs().size() <= 3) {
            System.out.println("route parallele : " + r.getRouteParallele());
            System.out.println("nb route libres : " + jeu.getRoutesLibres().size());
            jeu.getRoutesLibres().remove(r.getRouteParallele());
            System.out.println("nb route libres apres retrait: " + jeu.getRoutesLibres().size());
            System.out.println("nb route libres : " + jeu.getRoutesLibres());
        }
        jeu.getRoutesLibres().remove(r);
        cpt = 0;
        tour = false;
    }

    private void joueurPiocheCarteVisible(List<CarteTransport> cartesTransport, String inputTour) {
        CarteTransport selectedCard = null;
        for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
            if (carteTransport.getNom().equals(inputTour)) {
                selectedCard = carteTransport;
                break;
            }
        }
        if (selectedCard != null) {
            if (compteur == 0 && selectedCard.getType().equals(TypeCarteTransport.JOKER)) {
                choisirPileJoueur(cartesTransport, selectedCard);
                compteur += 2;
                fin = false;
                tour = false;
            } else if (compteur == 1 && selectedCard.getType().equals(TypeCarteTransport.JOKER)) {
            } else if (compteur < 2 && (selectedCard.getType().equals(TypeCarteTransport.BATEAU) || selectedCard.getType().equals(TypeCarteTransport.WAGON))) {
                choisirPileJoueur(cartesTransport, selectedCard);
                compteur++;
                fin = true;
            }
            if (compteur >= 2) resetCounterAndTurn();
        }
    }


    private void joueurPiocheDansLeDeck(List<CarteTransport> cartesTransport, String inputTour) {
        cartesTransport.add(inputTour.equals("WAGON") ? jeu.piocherCarteWagon() : jeu.piocherCarteBateau());
        updateCounterAndTurn();
    }

    private void choisirPileJoueur(List<CarteTransport> cartesTransport, CarteTransport selectedCard) {
        cartesTransport.add(selectedCard);
        jeu.getCartesTransportVisibles().remove(selectedCard);
        PilesCartesTransport pilesDeCartesWagon = jeu.getPilesDeCartesWagon();
        PilesCartesTransport pilesDeCartesBateau = jeu.getPilesDeCartesBateau();
        if (!pilesDeCartesWagon.isEmpty() || !pilesDeCartesBateau.isEmpty()) {
            String choixPioche = choisir("Quelle carte voulez vous piocher ?", Arrays.asList("WAGON", "BATEAU"), null, false);
            if (choixPioche.equals("WAGON")) {
                CarteTransport carteWagon = pilesDeCartesWagon.piocher();
                if (carteWagon != null) jeu.getCartesTransportVisibles().add(carteWagon);
            } else {
                CarteTransport carteBateau = pilesDeCartesBateau.piocher();
                if (carteBateau != null) jeu.getCartesTransportVisibles().add(carteBateau);
            }
        }
    }

    private void updateCounterAndTurn() {
        compteur++;
        fin = true;
        if (compteur >= 2) resetCounterAndTurn();
    }

    private void resetCounterAndTurn() {
        compteur = 0;
        fin = false;
        tour = false;
    }


    private void prendreDestination() {
        jeu.PiocheCartesDestination(3, 4, this);
        tour = false;
    }

    private void echangerPions(String inputTour) {
        if (("PIONS WAGON".equals(inputTour) && nbPionsWagonEnReserve > 0 && nbPionsBateau > 0)
                || ("PIONS BATEAU".equals(inputTour) && nbPionsBateauEnReserve > 0 && nbPionsWagon > 0)) {
            boolean isWagon = "PIONS WAGON".equals(inputTour);
            int availablePions = isWagon ? nbPionsWagonEnReserve : nbPionsBateauEnReserve;
            int pionsToExchange = isWagon ? nbPionsBateau : nbPionsWagon;
            int maxExchange = Math.min(availablePions, pionsToExchange);
            List<String> choixPions = createExchangeChoices(maxExchange);
            String message = "<h3>Combien de pions " + (isWagon ? "wagon" : "bateau") + " voulez vous échanger? Au maximum " + choixPions.get(choixPions.size() - 1);
            String inputPions = choisir(message, choixPions, null, false);
            int pionsExchanged = Integer.parseInt(inputPions);
            updatePions(isWagon, pionsExchanged);
            score -= pionsExchanged;
            tour = false;
        }
    }

    private List<String> createExchangeChoices(int maxExchange) {
        List<String> choices = new ArrayList<>();
        for (int i = 1; i <= maxExchange; i++) choices.add(String.valueOf(i));
        return choices;
    }

    private void updatePions(boolean isWagon, int pionsExchanged) {
        if (isWagon) {
            nbPionsWagon += pionsExchanged;
            nbPionsWagonEnReserve -= pionsExchanged;
            nbPionsBateau -= pionsExchanged;
            nbPionsBateauEnReserve += pionsExchanged;
        } else {
            nbPionsWagon -= pionsExchanged;
            nbPionsWagonEnReserve += pionsExchanged;
            nbPionsBateau += pionsExchanged;
            nbPionsBateauEnReserve -= pionsExchanged;
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


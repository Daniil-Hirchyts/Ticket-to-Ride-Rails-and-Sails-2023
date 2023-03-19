package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.data.*;

import java.util.*;
import java.util.stream.Collectors;

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

    public void setNbPionsWagon(int nbPionsWagon) {
        this.nbPionsWagon = nbPionsWagon;
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
    public void jouerTour() {
        do {
            List<Bouton> boutons = Arrays.asList(
                    new Bouton("Echanger pions wagons", "PIONS WAGON"),
                    new Bouton("Echanger pions bateaux", "PIONS BATEAU")
            );
            List<String> choixTour = new ArrayList<>(Arrays.asList("WAGON", "BATEAU", "DESTINATION"));
            List<String> choixCarteVisible = jeu.getCartesTransportVisibles().stream().map(CarteTransport::getNom).toList();
            List<String> choixRoute = jeu.getRoutesLibres().stream().map(Route::getNom).toList();
            List<String> choixPort = jeu.getPortsLibres().stream().map(Ville::nom).toList();
            choixTour.addAll(choixRoute);
            choixTour.addAll(choixCarteVisible);
            choixTour.addAll(choixPort);
            String inputTour = choisir("<h3>Qu'elle choix d'action ferez vous?</h3>", choixTour, fin ? null : boutons, fin);
            if (fin && inputTour.equals("")) tour = false;
            if (inputTour.equals("WAGON") || inputTour.equals("BATEAU"))
                joueurPiocheDansLeDeck(cartesTransport, inputTour);
            else if (choixCarteVisible.contains(inputTour)) joueurPiocheCarteVisible(cartesTransport, inputTour);
            else if (!fin) {
                switch (inputTour) {
                    case "PIONS WAGON", "PIONS BATEAU" -> echangerPions(inputTour);
                    case "DESTINATION" -> prendreDestination();
                    default -> {
                        if (choixRoute.contains(inputTour)) capturerRoute(inputTour);
                        else if (choixPort.contains(inputTour)) construirePort(inputTour);
                    }
                }
            }
        } while (tour);
    }

    private void capturerRoute(String inputTour) {
        List<Route> routeTemp = new ArrayList<>(jeu.getRoutesLibres());
        for (Route r : routeTemp) {
            if (r.getNom().equals(inputTour) && r instanceof RouteTerrestre) capturerRouteTerrestre((RouteTerrestre) r);
            else if (r.getNom().equals(inputTour) && r instanceof RouteMaritime)
                capturerRouteMaritime((RouteMaritime) r);
            else if (r.getNom().equals(inputTour) && r instanceof RoutePaire) capturerRoutePaire((RoutePaire) r);
        }
    }

    private void capturerRouteTerrestre(RouteTerrestre r) {
        Map<Couleur, Integer> colorCount = new HashMap<>();
        int jokerCount = 0;
        for (CarteTransport c : cartesTransport) {
            if (c.getType().equals(TypeCarteTransport.JOKER)) jokerCount++;
            else if (c.getType().equals(TypeCarteTransport.WAGON))
                colorCount.put(c.getCouleur(), colorCount.getOrDefault(c.getCouleur(), 0) + 1);
        }
        Couleur chosenColor = null;
        if (r.getCouleur().equals(Couleur.GRIS)) {
            for (Map.Entry<Couleur, Integer> entry : colorCount.entrySet()) {
                if (entry.getValue() + jokerCount >= r.getLongueur()) {
                    chosenColor = entry.getKey();
                    break;
                }
            }
        } else if (colorCount.getOrDefault(r.getCouleur(), 0) + jokerCount >= r.getLongueur())
            chosenColor = r.getCouleur();
        if (chosenColor != null) {
            Couleur finalChosenColor = chosenColor;
            List<String> choixCarteWagon = cartesTransport.stream()
                    .filter(c -> c.getType().equals(TypeCarteTransport.JOKER) || c.getCouleur().equals(finalChosenColor))
                    .map(CarteTransport::getNom)
                    .collect(Collectors.toList());
            while (compterCarte(cartesTransportPosees) < r.getLongueur()) {
                String inputChoix = choisir("<h3>Choisissez les cartes pour capturer la route.</h3>", choixCarteWagon, null, false);
                CarteTransport selectedCard = cartesTransport.stream().filter(c -> c.getNom().equals(inputChoix)).findFirst().orElse(null);
                if (selectedCard != null) {
                    cartesTransportPosees.add(selectedCard);
                    cartesTransport.remove(selectedCard);
                    choixCarteWagon.remove(inputChoix);
                }
            }
            conditionGagnante(r);
        }
    }

    private void capturerRouteMaritime(RouteMaritime r) {
        Map<Couleur, Integer> colorCount = new HashMap<>();
        int jokerCount = 0;
        for (CarteTransport c : cartesTransport) {
            if (c.getType().equals(TypeCarteTransport.JOKER)) jokerCount++;
            else if (c.getType().equals(TypeCarteTransport.BATEAU))
                colorCount.put(c.getCouleur(), colorCount.getOrDefault(c.getCouleur(), 0) + 1);
        }
        Couleur chosenColor = null;
        if (r.getCouleur().equals(Couleur.GRIS)) {
            for (Map.Entry<Couleur, Integer> entry : colorCount.entrySet()) {
                if (entry.getValue() + jokerCount >= r.getLongueur()) {
                    chosenColor = entry.getKey();
                    break;
                }
            }
        } else if (colorCount.getOrDefault(r.getCouleur(), 0) + jokerCount >= r.getLongueur())
            chosenColor = r.getCouleur();
        if (chosenColor != null) {
            Couleur finalChosenColor = chosenColor;
            List<String> choixCarteBateau = cartesTransport.stream()
                    .filter(c -> c.getType().equals(TypeCarteTransport.JOKER) || c.getCouleur().equals(finalChosenColor) && c.getType().equals(TypeCarteTransport.BATEAU))
                    .map(CarteTransport::getNom)
                    .collect(Collectors.toList());
            while (compterCarte(cartesTransportPosees) < r.getLongueur()) {
                String inputChoix = choisir("<h3>Choisissez les cartes pour capturer la route maritime.</h3>", choixCarteBateau, null, false);
                CarteTransport selectedCard = cartesTransport.stream().filter(c -> c.getNom().equals(inputChoix)).findFirst().orElse(null);
                if (selectedCard != null) {
                    cartesTransportPosees.add(selectedCard);
                    cartesTransport.remove(selectedCard);
                    choixCarteBateau.remove(inputChoix);
                }
            }
            conditionGagnante(r);
        }
    }

    private void capturerRoutePaire(RoutePaire r) {
        // TODO: Implementation
        List<CarteTransport> carteCorrecte = new ArrayList<>();
        List<CarteTransport> carteCorrecteTemp = new ArrayList<>();
        List<CarteTransport> lastcarteJoker = new ArrayList<>();
        List<Integer> nbcarteCouleur = new ArrayList<>();
        List<Integer> OpposerNbCarteCouleur = new ArrayList<>();
        List<Integer> nbCouleurValide = new ArrayList<>();
        List<Couleur> DebutCouleur = new ArrayList<>();
        List<String> choixCarteWagon = new ArrayList<>();
        boolean removeAllOne = false;
        int nbCouleur = 0;
        int nbValide = 0;
        int indexToRemove = -1;
        int cpt = 0;
        for (Couleur c : Couleur.values()) {
            nbcarteCouleur.add(0);
            OpposerNbCarteCouleur.add(0);
        }
        for (CarteTransport c : cartesTransport) {
            if (c.getType().equals(TypeCarteTransport.JOKER) || c.getType().equals(TypeCarteTransport.WAGON)) {
                if (c.getType().equals(TypeCarteTransport.JOKER)) for (Couleur couleur : Couleur.values())
                    nbcarteCouleur.set(couleur.ordinal(), nbcarteCouleur.get(couleur.ordinal()) + 1);
                nbcarteCouleur.set(c.getCouleur().ordinal(), nbcarteCouleur.get(c.getCouleur().ordinal()) + 1);
            }
        }
        nbcarteCouleur.remove(Couleur.GRIS.ordinal());
        OpposerNbCarteCouleur.remove(Couleur.GRIS.ordinal());
        for (int i = 0; i < nbcarteCouleur.size(); i++) {
            if (nbcarteCouleur.get(i) >= 2) {
                for (CarteTransport c : cartesTransport) {
                    if (c.getCouleur().equals(Couleur.values()[i]) && c.getType().equals(TypeCarteTransport.WAGON)) {
                        carteCorrecte.add(c);
                        OpposerNbCarteCouleur.set(i, OpposerNbCarteCouleur.get(i) + 1);
                        cpt++;
                        nbcarteCouleur.set(i, nbcarteCouleur.get(i) - 1);
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
        if (cpt >= r.getLongueur()) {
            for (int i = 0; i < OpposerNbCarteCouleur.size(); i++)
                if (OpposerNbCarteCouleur.get(i) == 0) nbcarteCouleur.set(i, 0);
            for (CarteTransport c : carteCorrecte) choixCarteWagon.add(c.getNom());
            for (int i = 0; i < r.getLongueur(); i++) nbCouleurValide.add(0);
            do {
                String inputChoix = choisir("choisissez les cartes pour capturer la route :", choixCarteWagon, null, false);
                for (CarteTransport c : carteCorrecte) {
                    if (c.getNom().equals(inputChoix)) {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            dataCartePosees(c, cartesTransportPosees, carteCorrecteTemp, choixCarteWagon, inputChoix);
                            lastcarteJoker.add(c);
                            nbCouleurValide.replaceAll(integer -> integer + 1);
                        } else {
                            if (!DebutCouleur.contains(c.getCouleur()) && nbCouleur < r.getLongueur()) {
                                DebutCouleur.add(c.getCouleur());
                                nbCouleurValide.set(DebutCouleur.indexOf(c.getCouleur()), nbCouleurValide.get(DebutCouleur.indexOf(c.getCouleur())) + 1);
                                dataCartePosees(c, cartesTransportPosees, carteCorrecteTemp, choixCarteWagon, inputChoix);
                                nbCouleur++;
                                for (int i = 0; i < nbcarteCouleur.size(); i++) {
                                    if (OpposerNbCarteCouleur.get(i) == 1 && c.getCouleur().equals(Couleur.values()[i])) {
                                        removeAllOne = true;
                                        for (CarteTransport ct : carteCorrecte) {
                                            if (!ct.getCouleur().equals(Couleur.values()[i]) && ct.getType().equals(TypeCarteTransport.WAGON) && OpposerNbCarteCouleur.get(ct.getCouleur().ordinal()) == 1) {
                                                carteCorrecteTemp.add(ct);
                                                choixCarteWagon.remove(ct.getNom());
                                                //enleve la possibilité de prendre les carte qui sont toutes seuls si la carte choisi est une carte toute seule
                                                nbcarteCouleur.set(ct.getCouleur().ordinal(), nbcarteCouleur.get(ct.getCouleur().ordinal()) - 1);
                                                OpposerNbCarteCouleur.set(ct.getCouleur().ordinal(), OpposerNbCarteCouleur.get(ct.getCouleur().ordinal()) - 1);
                                            }
                                        }
                                    }
                                }
                            } else if (DebutCouleur.contains(c.getCouleur())) {
                                nbCouleurValide.set(DebutCouleur.indexOf(c.getCouleur()), nbCouleurValide.get(DebutCouleur.indexOf(c.getCouleur())) + 1);
                                dataCartePosees(c, cartesTransportPosees, carteCorrecteTemp, choixCarteWagon, inputChoix);

                            }
                        }
                    }
                }
                for (int i = 0; i < nbCouleurValide.size(); i++) {
                    if (nbCouleurValide.get(i) >= 2) {
                        nbValide++;
                        indexToRemove = i;
                        break;
                    }
                }
                if (removeAllOne) {
                    for (CarteTransport ct : carteCorrecteTemp) choixCarteWagon.remove(ct.getNom());
                    carteCorrecte.removeAll(carteCorrecteTemp);
                    removeAllOne = false;
                    carteCorrecteTemp.clear();
                }
                if (indexToRemove != -1) {
                    if (lastcarteJoker.size() != 0) {
                        for (CarteTransport c : lastcarteJoker) {
                            nbCouleurValide.replaceAll(integer -> integer - 1);
                            choixCarteWagon.remove(c.getNom());
                            nbcarteCouleur.replaceAll(integer -> integer - 1);
                            for (int i = 0; i < OpposerNbCarteCouleur.size(); i++) {
                                if (OpposerNbCarteCouleur.get(i) == 1) OpposerNbCarteCouleur.set(i, 0);
                            }
                        }
                        lastcarteJoker.clear();
                    }
                    nbCouleurValide.remove(indexToRemove);
                    DebutCouleur.remove(indexToRemove);
                    indexToRemove = -1;
                }
            }
            while (nbValide < r.getLongueur());
            for (CarteTransport c : cartesTransportPosees) jeu.getPilesDeCartesWagon().defausser(c);
            score += r.getScore();
            cartesTransportPosees.clear();
            routes.add(r);
            if (jeu.getJoueurs().size() <= 3) jeu.getRoutesLibres().remove(r.getRouteParallele());
            jeu.getRoutesLibres().remove(r);
            cpt = 0;
            tour = false;
        }
    }

    public void dataCartePosees(CarteTransport c, List<CarteTransport> cartesTransportPosees, List<CarteTransport> carteCorrecteTemp, List<String> choixCarteWagon, String inputChoix) {
        cartesTransportPosees.add(c);
        cartesTransport.remove(c);
        choixCarteWagon.remove(inputChoix);
        carteCorrecteTemp.add(c);
    }

    private int compterCarte(List<CarteTransport> carteTransportPosees) {
        int cpt = 0;
        for (CarteTransport c : carteTransportPosees) cpt += c.estDouble() ? 2 : 1;
        return cpt;
    }

    private void conditionGagnante(Route r) {
        Collection<Route> routesLibres = jeu.getRoutesLibres();
        for (CarteTransport c : cartesTransportPosees) jeu.getPilesDeCartesWagon().defausser(c);
        for (CarteTransport c : cartesTransportPosees) jeu.getPilesDeCartesBateau().defausser(c);
        score += r.getScore();
        cartesTransportPosees.clear();
        routes.add(r);
        if (jeu.getJoueurs().size() <= 3) routesLibres.remove(r.getRouteParallele());
        routesLibres.remove(r);
        tour = false;
    }

    //TODO construirePort()
    private void construirePort(String inputTour) {
        boolean peuxConstruirePort = true;
        List<CarteTransport> cartesCorrectes = new ArrayList<>();
        List<CarteTransport> cloneCartesCorrectesToRemove = new ArrayList<>();
        List<Integer> nbCartesDeCouleur = new ArrayList<>();
        List<String> choixCarteConstructionPort = new ArrayList<>();
        List<Integer> nbBateuxWagonPort = new ArrayList<>(Arrays.asList(0, 0));
        List<Couleur> couleurBateauWagon = new ArrayList<>(Arrays.asList(null, null));
        Ville ville = null;
        int nbCouleurBateauWagon = 0;
        int nbJoker = 0;
        int compteurPairValide = 0;
        for (Ville v : jeu.getPortsLibres()) {
            if (v.nom().equals(inputTour)) {
                for (Route r : routes) {
                    if (r.getVille1().equals(v) || r.getVille2().equals(v)) {
                        peuxConstruirePort = true;
                        ville = v;
                        break;
                    } else {
                        System.out.println("Vous ne pouvez pas construire un port si aucune de vos routes n'est relié à ce port");
                        peuxConstruirePort = false;
                    }
                }
            }
        }
        if (peuxConstruirePort) {
            for (CarteTransport c : cartesTransport) {
                if ((c.getType().equals(TypeCarteTransport.BATEAU) || c.getType().equals(TypeCarteTransport.WAGON)) && c.getAncre()) {
                    cartesCorrectes.add(c);
                } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                    cartesCorrectes.add(c);
                }
            }
            //compter le nombre de carte de chaque couleur
            for (Couleur couleur : Couleur.values()) {
                if (couleur.equals(Couleur.GRIS)) {
                    nbCartesDeCouleur.replaceAll(integer -> integer + 1);
                } else {
                    int nbCarteCouleur = 0;
                    for (CarteTransport c : cartesCorrectes) {
                        if (c.getCouleur().equals(couleur) && c.estDouble()) nbCarteCouleur += 2;
                        else if (c.getCouleur().equals(couleur)) nbCarteCouleur++;
                    }
                    nbCartesDeCouleur.add(nbCarteCouleur);
                }
            }
            for (int i = 0; i < nbCartesDeCouleur.size(); i++) {
                if (nbCartesDeCouleur.get(i) >= 4) {
                    for (CarteTransport c : cartesCorrectes) {
                        if (c.getCouleur().equals(Couleur.values()[i]) && !c.getType().equals(TypeCarteTransport.JOKER)) {
                            choixCarteConstructionPort.add(c.getNom());

                        }
                    }
                    compteurPairValide++;
                } else {
                    for (CarteTransport c : cartesCorrectes) {
                        if (c.getCouleur().equals(Couleur.values()[i]) && !c.getType().equals(TypeCarteTransport.JOKER)) {
                            cloneCartesCorrectesToRemove.add(c);
                            nbCartesDeCouleur.set(i, 0);
                        } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            nbCartesDeCouleur.set(i, 0);
                        }
                    }
                    if (cloneCartesCorrectesToRemove.size() != 0) {
                        cartesCorrectes.removeAll(cloneCartesCorrectesToRemove);
                        cloneCartesCorrectesToRemove.clear();
                    }
                }
            }
            for (CarteTransport c : cartesCorrectes) {
                if (c.getType().equals(TypeCarteTransport.JOKER)) {
                    choixCarteConstructionPort.add(c.getNom());
                    nbJoker++;
                }
            }
            if (compteurPairValide != 0) {
                //TODO choisirCarteConstructionPort(choixCarteConstructionPort)
                peuxConstruirePort = false;
                System.out.println("Vous pouvez construire un port");
                do {
                    String inputConstructionPort = choisir("Choisissez une carte pour construire un port", choixCarteConstructionPort, null, false);
                    for (CarteTransport c : cartesCorrectes) {
                        if (c.getNom().equals(inputConstructionPort)) {
                            if (c.getType().equals(TypeCarteTransport.BATEAU) && couleurBateauWagon.get(0) == null && nbBateuxWagonPort.get(0) < 2) {
                                dataCartePosees(c, cartesTransportPosees, cloneCartesCorrectesToRemove, choixCarteConstructionPort, inputConstructionPort);
                                nbBateuxWagonPort.set(0, nbBateuxWagonPort.get(0) + 1);
                                couleurBateauWagon.set(0, c.getCouleur());
                                for (CarteTransport carteTransport : cartesCorrectes) {
                                    if (!carteTransport.getCouleur().equals(couleurBateauWagon.get(0)) && carteTransport.getType().equals(TypeCarteTransport.BATEAU)) {
                                        cloneCartesCorrectesToRemove.add(carteTransport);
                                    }
                                }
                            } else if (c.getType().equals(TypeCarteTransport.BATEAU) && c.getCouleur().equals(couleurBateauWagon.get(0)) && nbBateuxWagonPort.get(0) < 2) {
                                dataCartePosees(c, cartesTransportPosees, cloneCartesCorrectesToRemove, choixCarteConstructionPort, inputConstructionPort);
                                nbBateuxWagonPort.set(0, nbBateuxWagonPort.get(0) + 1);
                            } else if (c.getType().equals(TypeCarteTransport.WAGON) && couleurBateauWagon.get(1) == null && nbBateuxWagonPort.get(1) < 2) {
                                dataCartePosees(c, cartesTransportPosees, cloneCartesCorrectesToRemove, choixCarteConstructionPort, inputConstructionPort);
                                nbBateuxWagonPort.set(1, nbBateuxWagonPort.get(1) + 1);
                                couleurBateauWagon.set(1, c.getCouleur());
                                for (CarteTransport carteTransport : cartesCorrectes) {
                                    if (!carteTransport.getCouleur().equals(couleurBateauWagon.get(1)) && carteTransport.getType().equals(TypeCarteTransport.WAGON)) {
                                        cloneCartesCorrectesToRemove.add(carteTransport);
                                    }
                                }
                            } else if (c.getType().equals(TypeCarteTransport.WAGON) && c.getCouleur().equals(couleurBateauWagon.get(1)) && nbBateuxWagonPort.get(1) < 2) {
                                dataCartePosees(c, cartesTransportPosees, cloneCartesCorrectesToRemove, choixCarteConstructionPort, inputConstructionPort);
                                nbBateuxWagonPort.set(1, nbBateuxWagonPort.get(1) + 1);
                            } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                                dataCartePosees(c, cartesTransportPosees, cloneCartesCorrectesToRemove, choixCarteConstructionPort, inputConstructionPort);
                            }
                            if (nbBateuxWagonPort.get(0) + nbBateuxWagonPort.get(1) == 4 - nbJoker) {
                                peuxConstruirePort = true;
                            }
                        }
                    }
                    cartesCorrectes.removeAll(cloneCartesCorrectesToRemove);
                } while (!peuxConstruirePort);
                for (CarteTransport c : cartesTransportPosees) {
                    if (c.getType().equals(TypeCarteTransport.BATEAU))
                        jeu.getPilesDeCartesBateau().defausser(c);
                    else if (c.getType().equals(TypeCarteTransport.WAGON) || c.getType().equals(TypeCarteTransport.JOKER))
                        jeu.getPilesDeCartesWagon().defausser(c);
                }
                cartesTransportPosees.clear();
                ports.add(ville);
                jeu.getPortsLibres().remove(ville);
                tour = false;
            } else
                System.out.println("Vous n'avez pas assez de carte pour construire un port");
        }
    }

    //TODO destinationEstComplete()
    boolean destinationEstComplete(Destination d) {
        return false;
    }

    //TODO calculerScoreFinal()
    public int calculerScoreFinal() {
        return 1;
    }

//    FIXME : DONE ---------------------------------------------------------------------------------

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
            } else {
                if (compteur != 1 || !selectedCard.getType().equals(TypeCarteTransport.JOKER)) {
                    if (compteur < 2 && (selectedCard.getType().equals(TypeCarteTransport.BATEAU) || selectedCard.getType().equals(TypeCarteTransport.WAGON))) {
                        choisirPileJoueur(cartesTransport, selectedCard);
                        compteur++;
                        fin = true;
                        if (compteur >= 2) resetCounterAndTurn();
                    }
                }
            }
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
            String choixPioche = choisir("<h3>Quelle carte voulez vous piocher?</h3>", Arrays.asList("WAGON", "BATEAU"), null, false);
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


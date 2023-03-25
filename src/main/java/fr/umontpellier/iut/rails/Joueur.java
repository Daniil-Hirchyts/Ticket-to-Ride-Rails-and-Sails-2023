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

    private boolean haveToRemove = false;
    private final Map<Destination, List<String>> ordreVillesACapturer = new HashMap<>();
    private final Map<Destination, List<String>> ordreVillesDejaCapturer = new HashMap<>();

    private final Map<Destination, List<String>> Historique = new HashMap<>();

    private Boolean encoreDesCartes = true;

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
            checker();
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
            String inputTour;
            if (nePeuxRienFaire()) {
                inputTour = choisir("<h3>Vous ne pouvez rien faire. Passez-votre tour</h3>", null, null, true);
            } else {
                inputTour = choisir("<h3>Qu'elle choix d'action ferez vous?</h3>", choixTour, fin ? null : boutons, fin);
            }
            if (fin && inputTour.equals("")) {
                tour = false;
                compteur = 0;
            }
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
        tour = true;
    }

    private void checker() {
        if (jeu.getPilesDeCartesBateau().isEmpty() || jeu.getPilesDeCartesWagon().isEmpty()) {
            jeu.getPilesDeCartesWagon().getPilePioche().addAll(jeu.getPilesDeCartesWagon().getPileDefausse());
            jeu.getPilesDeCartesBateau().getPilePioche().addAll(jeu.getPilesDeCartesBateau().getPileDefausse());
            jeu.getPilesDeCartesWagon().getPileDefausse().clear();
            jeu.getPilesDeCartesBateau().getPileDefausse().clear();
        }
        if (jeu.getCartesTransportVisibles().size() < 6 && jeu.getPilesDeCartesWagon().getPilePioche().size() + jeu.getPilesDeCartesBateau().getPilePioche().size() > 0) {
            while (jeu.getCartesTransportVisibles().size() < 6 && encoreDesCartes) {
                String choixPioche = choisir("<h3>Choisissez dans quelle pile piocher pour remplir la les carteVisible</h3>", Arrays.asList("WAGON", "BATEAU"), null, false);
                if (choixPioche.equals("WAGON")) {
                    CarteTransport carteWagon = jeu.getPilesDeCartesWagon().piocher();
                    if (carteWagon != null) jeu.getCartesTransportVisibles().add(carteWagon);
                } else {
                    CarteTransport carteBateau = jeu.getPilesDeCartesBateau().piocher();
                    if (carteBateau != null) jeu.getCartesTransportVisibles().add(carteBateau);
                }
                if (jeu.getPilesDeCartesWagon().getPilePioche().isEmpty() && jeu.getPilesDeCartesBateau().getPilePioche().isEmpty()) {
                    encoreDesCartes = false;
                }
            }
        }
        int compteurJoker = 0;
        int compteurWagon = 0;
        int compteurBateau = 0;
        for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
            if (carteTransport.getType().equals(TypeCarteTransport.JOKER)) compteurJoker++;
            else if (carteTransport.getType().equals(TypeCarteTransport.WAGON)) compteurWagon++;
            else compteurBateau++;
        }
        for (CarteTransport c : jeu.getPilesDeCartesWagon().getPilePioche()) {
            if (c.getType().equals(TypeCarteTransport.WAGON)) compteurWagon++;
        }
        compteurBateau += jeu.getPilesDeCartesBateau().getPilePioche().size();
        if ((compteurWagon > 0 && compteurBateau >= 3 && compteurJoker >= 3) || (compteurWagon == 0 && compteurBateau >= 4 && compteurJoker >= 3) || (compteurWagon >= 4 && compteurBateau < 3 && compteurJoker >= 3)) {
            while (compteurJoker >= 3) {
                for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                    if (carteTransport.getType().equals(TypeCarteTransport.WAGON) || carteTransport.getType().equals(TypeCarteTransport.JOKER)) {
                        jeu.getPilesDeCartesWagon().remiseDansLaPile(carteTransport);
                    } else if (carteTransport.getType().equals(TypeCarteTransport.BATEAU)) {
                        jeu.getPilesDeCartesBateau().remiseDansLaPile(carteTransport);
                    }
                }
                jeu.getCartesTransportVisibles().clear();
                jeu.getPilesDeCartesWagon().shuffle();
                jeu.getPilesDeCartesBateau().shuffle();
                //on pioche aléatoirement dans les deux piles de cartes pour remplir les cartes visibles
                if (compteurWagon > 0 && compteurBateau >= 3) {
                    for (int i = 0; i < 3; i++) {
                        jeu.getCartesTransportVisibles().add(jeu.getPilesDeCartesWagon().piocher());
                        jeu.getCartesTransportVisibles().add(jeu.getPilesDeCartesBateau().piocher());
                    }
                }
                if (compteurWagon == 0) {
                    for (int i = 0; i < 6; i++) {
                        CarteTransport selection;
                        double random = Math.random();
                        if (random < 0.5) {
                            selection = jeu.getPilesDeCartesWagon().piocher();
                            if (selection != null) jeu.getCartesTransportVisibles().add(selection);
                            else jeu.getCartesTransportVisibles().add(jeu.getPilesDeCartesBateau().piocher());
                        } else jeu.getCartesTransportVisibles().add(jeu.getPilesDeCartesBateau().piocher());
                    }
                }
                if (compteurBateau < 3) {
                    for (int i = 0; i < 6; i++) {
                        CarteTransport selection;
                        double random = Math.random();
                        if (random < 0.5) {
                            selection = jeu.getPilesDeCartesBateau().piocher();
                            if (selection != null) jeu.getCartesTransportVisibles().add(selection);
                            else jeu.getCartesTransportVisibles().add(jeu.getPilesDeCartesWagon().piocher());
                        } else jeu.getCartesTransportVisibles().add(jeu.getPilesDeCartesWagon().piocher());
                    }
                }
                compteurJoker = 0;
                System.out.println(jeu.getCartesTransportVisibles());
                for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                    if (carteTransport.getType().equals(TypeCarteTransport.JOKER)) {
                        compteurJoker++;
                    }
                }
            }

        }
        //si dans la pileWagon il y aU MOINS UN WAGON NON JOKER changer cartevisible pour eviter d'avoir trois joker si il y a au moins 3 bateaux dans cartevisible+piledecartebateaux
    }

    private boolean nePeuxRienFaire() {
        return destinations.isEmpty() && cartesTransportPosees.isEmpty() && jeu.getRoutesLibres().isEmpty() && jeu.getPortsLibres().isEmpty();
    }

    private void capturerRoute(String inputTour) {
        List<Route> routeTemp = new ArrayList<>(jeu.getRoutesLibres());
        for (Route r : routeTemp) {
            if (r.getNom().equals(inputTour) && r instanceof RouteTerrestre && nbPionsWagon >= r.getLongueur())
                capturerRouteTerrestre((RouteTerrestre) r);
            else if (r.getNom().equals(inputTour) && r instanceof RouteMaritime && nbPionsBateau >= r.getLongueur())
                capturerRouteMaritime((RouteMaritime) r);
            else if (r.getNom().equals(inputTour) && r instanceof RoutePaire && nbPionsWagon + nbPionsBateau > r.getLongueur())
                capturerRoutePaire((RoutePaire) r);
        }
    }

    public int getNombreTotalPions() {
        return nbPionsWagonEnReserve + nbPionsBateauEnReserve;
    }

    private void capturerRouteTerrestre(RouteTerrestre r) {
        Map<Couleur, Integer> colorCount = new HashMap<>();
        List<CarteTransport> carteCorrect = new ArrayList<>();
        List<CarteTransport> cloneCarteCorrect = new ArrayList<>();
        int jokerCount = 0;
        for (CarteTransport c : cartesTransport) {
            if (c.getType().equals(TypeCarteTransport.JOKER)) {
                jokerCount++;
                carteCorrect.add(c);
            } else if (c.getType().equals(TypeCarteTransport.WAGON)) {
                carteCorrect.add(c);
                colorCount.put(c.getCouleur(), colorCount.getOrDefault(c.getCouleur(), 0) + 1);
            }
        }
        List<Couleur> chosenColor = new ArrayList<>();
        if (r.getCouleur().equals(Couleur.GRIS)) {
            for (Map.Entry<Couleur, Integer> entry : colorCount.entrySet())
                if (entry.getValue() + jokerCount >= r.getLongueur()) chosenColor.add(entry.getKey());
        } else if (colorCount.getOrDefault(r.getCouleur(), 0) + jokerCount >= r.getLongueur())
            chosenColor.add(r.getCouleur());
        for (CarteTransport c : carteCorrect)
            if (!chosenColor.contains(c.getCouleur()) && !chosenColor.isEmpty()) cloneCarteCorrect.add(c);
        carteCorrect.removeAll(cloneCarteCorrect);
        for (CarteTransport c : cartesTransport)
            if (c.getType().equals(TypeCarteTransport.JOKER)) carteCorrect.add(c);
        if (routes.contains(r.getRouteParallele())) {
            chosenColor.clear();
        }
        if (!chosenColor.isEmpty()) {
            Couleur finalChosenColor = null;
            List<String> choixCarteWagon = carteCorrect.stream()
                    .map(CarteTransport::getNom)
                    .collect(Collectors.toList());
            while (cartesTransportPosees.size() < r.getLongueur()) {
                String inputChoix = choisir("<h3>Choisissez les cartes pour capturer la route.</h3>", choixCarteWagon, null, false);
                CarteTransport selectedCard = cartesTransport.stream().filter(c -> c.getNom().equals(inputChoix)).findFirst().orElse(null);
                for (CarteTransport c : carteCorrect) {
                    if (c.getNom().equals(inputChoix) && finalChosenColor == null && !c.getType().equals(TypeCarteTransport.JOKER)) {
                        setCarteRouteTerrestre(cartesTransportPosees, choixCarteWagon, cartesTransport, selectedCard, inputChoix);
                        finalChosenColor = c.getCouleur();
                    } else if (c.getNom().equals(inputChoix) && (c.getCouleur().equals(finalChosenColor) || c.getType().equals(TypeCarteTransport.JOKER))) {
                        setCarteRouteTerrestre(cartesTransportPosees, choixCarteWagon, cartesTransport, selectedCard, inputChoix);
                    }
                }
            }
            conditionGagnante(r);
        }
    }

    private void setCarteRouteTerrestre(List<CarteTransport> cartesTransportPosees, List<String> choixCarteWagon, List<CarteTransport> cartesTransport, CarteTransport selectedCard, String inputChoix) {
        cartesTransportPosees.add(selectedCard);
        cartesTransport.remove(selectedCard);
        choixCarteWagon.remove(inputChoix);
    }

    private void capturerRouteMaritime(RouteMaritime r) {
        Map<Couleur, Integer> colorCount = new HashMap<>();
        List<CarteTransport> carteCorrect = new ArrayList<>();
        List<CarteTransport> cloneCarteCorrect = new ArrayList<>();
        int jokerCount = 0;
        for (CarteTransport c : cartesTransport) {
            if (c.getType().equals(TypeCarteTransport.JOKER)) {
                jokerCount++;
                carteCorrect.add(c);
            } else if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                if (c.estDouble()) {
                    colorCount.put(c.getCouleur(), colorCount.getOrDefault(c.getCouleur(), 0) + 2);
                } else {
                    colorCount.put(c.getCouleur(), colorCount.getOrDefault(c.getCouleur(), 0) + 1);
                }
                carteCorrect.add(c);

            }
        }

        List<Couleur> chosenColor = new ArrayList<>();
        if (r.getCouleur().equals(Couleur.GRIS)) {
            for (Map.Entry<Couleur, Integer> entry : colorCount.entrySet()) {
                if (entry.getValue() + jokerCount >= r.getLongueur()) {
                    chosenColor.add(entry.getKey());
                }
            }
        } else if (colorCount.getOrDefault(r.getCouleur(), 0) + jokerCount >= r.getLongueur()) {
            chosenColor.add(r.getCouleur());
        }

        for (CarteTransport c : carteCorrect) {
            if (!chosenColor.contains(c.getCouleur()) && !chosenColor.isEmpty()) {
                cloneCarteCorrect.add(c);
            }
        }

        carteCorrect.removeAll(cloneCarteCorrect);

        for (CarteTransport c : cartesTransport) {
            if (c.getType().equals(TypeCarteTransport.JOKER)) {
                carteCorrect.add(c);
            }
        }
        cloneCarteCorrect.clear();
        if (routes.contains(r.getRouteParallele())) {
            chosenColor.clear();
        }
        if (!chosenColor.isEmpty()) {
            Couleur finalChosenColor = null;
            CarteTransport uneCarteSimpleAEtePoser = null;
            List<Integer> OpposerNbDoubleEtSimple = new ArrayList<>();
            List<Integer> nbDoubleEtSimple = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                nbDoubleEtSimple.add(0);
                OpposerNbDoubleEtSimple.add(0);
            }
            for (CarteTransport c : carteCorrect) {
                if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                    if (c.estDouble()) OpposerNbDoubleEtSimple.set(0, OpposerNbDoubleEtSimple.get(0) + 1);
                    else OpposerNbDoubleEtSimple.set(1, OpposerNbDoubleEtSimple.get(1) + 1);
                }
            }
            List<String> choixCarteBateau = carteCorrect.stream()
                    .map(CarteTransport::getNom)
                    .collect(Collectors.toList());
            int placedBateau = 0;
            while (placedBateau < r.getLongueur()) {
                String inputChoix = choisir("<h3>Choisissez les cartes pour capturer la route.</h3>", choixCarteBateau, null, false);
                CarteTransport selectedCard = cartesTransport.stream().filter(c -> c.getNom().equals(inputChoix)).findFirst().orElse(null);
                for (CarteTransport c : carteCorrect) {
                    if (c.estDouble() && (placedBateau + 2 <= r.getLongueur() || uneCarteSimpleAEtePoser == null) && c.getNom().equals(inputChoix) && finalChosenColor == null && !c.getType().equals(TypeCarteTransport.JOKER)) {
                        finalChosenColor = c.getCouleur();
                        setCartesDoubleSansCouleur(c,selectedCard,cloneCarteCorrect,nbDoubleEtSimple,OpposerNbDoubleEtSimple,haveToRemove,inputChoix,choixCarteBateau);
                    } else if (c.estDouble() && (placedBateau + 2 <= r.getLongueur() || uneCarteSimpleAEtePoser == null) && c.getNom().equals(inputChoix) && (c.getCouleur().equals(finalChosenColor) || c.getType().equals(TypeCarteTransport.JOKER))) {
                        setCartesDoubleSansCouleur(c,selectedCard,cloneCarteCorrect,nbDoubleEtSimple,OpposerNbDoubleEtSimple,haveToRemove,inputChoix,choixCarteBateau);
                    } else if (OpposerNbDoubleEtSimple.get(1) + jokerCount != 1 && r.getLongueur() != 1) {
                        if (!c.estDouble() && c.getNom().equals(inputChoix) && finalChosenColor == null && !c.getType().equals(TypeCarteTransport.JOKER)) {
                            if (uneCarteSimpleAEtePoser == null) {
                                uneCarteSimpleAEtePoser = selectedCard;
                            }
                            finalChosenColor = c.getCouleur();
                            setCartesSimplesSansCouleur(c,selectedCard,cloneCarteCorrect,nbDoubleEtSimple,OpposerNbDoubleEtSimple,haveToRemove,inputChoix,choixCarteBateau);
                        } else if (!c.estDouble() && c.getNom().equals(inputChoix) && (c.getCouleur().equals(finalChosenColor) || c.getType().equals(TypeCarteTransport.JOKER))) {
                            if (uneCarteSimpleAEtePoser == null) {
                                uneCarteSimpleAEtePoser = selectedCard;
                            }
                            setCartesSimplesSansCouleur(c,selectedCard,cloneCarteCorrect,nbDoubleEtSimple,OpposerNbDoubleEtSimple,haveToRemove,inputChoix,choixCarteBateau);
                        }
                    } else if (r.getLongueur() == 1 || r.getLongueur() - cartesTransportPosees.size() == 1) {
                        if (!c.estDouble() && c.getNom().equals(inputChoix) && finalChosenColor == null && !c.getType().equals(TypeCarteTransport.JOKER)) {
                            finalChosenColor = c.getCouleur();
                            setCartesSimplesSansCouleur(c,selectedCard,cloneCarteCorrect,nbDoubleEtSimple,OpposerNbDoubleEtSimple,haveToRemove,inputChoix,choixCarteBateau);
                        } else if (!c.estDouble() && c.getNom().equals(inputChoix) && (c.getCouleur().equals(finalChosenColor) || c.getType().equals(TypeCarteTransport.JOKER))) {
                            setCartesSimplesSansCouleur(c,selectedCard,cloneCarteCorrect,nbDoubleEtSimple,OpposerNbDoubleEtSimple,haveToRemove,inputChoix,choixCarteBateau);
                        }
                    }
                }
                if (haveToRemove) {
                    carteCorrect.removeAll(cloneCarteCorrect);
                    cloneCarteCorrect.clear();
                    haveToRemove = false;
                }
                placedBateau = nbDoubleEtSimple.get(0) * 2 + nbDoubleEtSimple.get(1);
            }
            conditionGagnante(r);
        }
    }

    private void setCartesSimplesSansCouleur(CarteTransport c, CarteTransport selectedCard, List<CarteTransport> cloneCarteCorrect, List<Integer> nbDoubleEtSimple, List<Integer> OpposerNbDoubleEtSimple, boolean haveToRemove, String inputChoix, List<String> choixCarteBateau) {
        changerLesCartes(c,selectedCard,cloneCarteCorrect,haveToRemove,inputChoix,choixCarteBateau);
        nbDoubleEtSimple.set(1, nbDoubleEtSimple.get(1) + 1);
        OpposerNbDoubleEtSimple.set(1, OpposerNbDoubleEtSimple.get(1) - 1);
    }
    private void setCartesDoubleSansCouleur(CarteTransport c, CarteTransport selectedCard, List<CarteTransport> cloneCarteCorrect, List<Integer> nbDoubleEtSimple, List<Integer> OpposerNbDoubleEtSimple, boolean haveToRemove, String inputChoix, List<String> choixCarteBateau) {
        changerLesCartes(c,selectedCard,cloneCarteCorrect,haveToRemove,inputChoix,choixCarteBateau);
        nbDoubleEtSimple.set(0, nbDoubleEtSimple.get(0) + 1);
        OpposerNbDoubleEtSimple.set(0, OpposerNbDoubleEtSimple.get(0) - 1);
    }

    private void changerLesCartes(CarteTransport c, CarteTransport selectedCard,List<CarteTransport> cloneCarteCorrect, boolean haveToRemove,String inputChoix, List<String> choixCarteBateau){
        cartesTransportPosees.add(selectedCard);
        cartesTransport.remove(selectedCard);
        choixCarteBateau.remove(inputChoix);
        haveToRemove = true;
        cloneCarteCorrect.add(c);
    }
    private void capturerRoutePaire(RoutePaire r) {
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
            conditionGagnante(r);
        }
    }

    public void dataCartePosees(CarteTransport c, List<CarteTransport> cartesTransportPosees, List<CarteTransport> carteCorrecteTemp, List<String> choixCarteWagon, String inputChoix) {
        cartesTransportPosees.add(c);
        cartesTransport.remove(c);
        choixCarteWagon.remove(inputChoix);
        carteCorrecteTemp.add(c);
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

    private void conditionGagnante(Route r) {
        Collection<Route> routesLibres = jeu.getRoutesLibres();
        for (CarteTransport c : cartesTransportPosees) {
            if (c.getType().equals(TypeCarteTransport.WAGON)) {
                jeu.getPilesDeCartesWagon().defausser(c);
                setNbPionsWagon(getNbPionsWagon() - 1);
            } else if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                if (!c.estDouble()) {
                    jeu.getPilesDeCartesBateau().defausser(c);
                    setNbPionsBateau(getNbPionsBateau() - 1);
                } else {
                    jeu.getPilesDeCartesBateau().defausser(c);
                    setNbPionsBateau(getNbPionsBateau() - 2);
                }
            } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                if (r instanceof RouteMaritime) {
                    jeu.getPilesDeCartesWagon().defausser(c);
                    setNbPionsBateau(getNbPionsBateau() - 1);
                } else {
                    jeu.getPilesDeCartesWagon().defausser(c);
                    setNbPionsWagon(getNbPionsWagon() - 1);
                }
            }
        }
        score += r.getScore();
        cartesTransportPosees.clear();
        routes.add(r);
        if (jeu.getJoueurs().size() <= 3) routesLibres.remove(r.getRouteParallele());
        routesLibres.remove(r);
        tour = false;
    }

    private void construirePort(String inputTour) {
        boolean peuxConstruirePort = true;
        List<CarteTransport> cartesCorrectes = new ArrayList<>();
        List<CarteTransport> cloneCartesCorrectesToRemove = new ArrayList<>();
        List<Integer> nbCartesDeCouleur = new ArrayList<>();
        List<String> choixCarteConstructionPort = new ArrayList<>();
        List<Integer> nbBateuxWagonPort = new ArrayList<>(Arrays.asList(0, 0));
        List<Couleur> couleurBateauWagon = new ArrayList<>(Arrays.asList(null, null));
        Ville ville = null;
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
                conditionGagnantePort(ville);
            } else
                System.out.println("Vous n'avez pas assez de carte pour construire un port");
        }
    }

    private void conditionGagnantePort(Ville ville) {
        for (CarteTransport c : cartesTransportPosees) {
            if (c.getType().equals(TypeCarteTransport.WAGON) || c.getType().equals(TypeCarteTransport.JOKER)) {
                jeu.getPilesDeCartesWagon().defausser(c);
                setNbPionsWagon(getNbPionsWagon() - 1);
            } else if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                if (!c.estDouble()) {
                    jeu.getPilesDeCartesBateau().defausser(c);
                    setNbPionsBateau(getNbPionsBateau() - 1);
                } else {
                    jeu.getPilesDeCartesBateau().defausser(c);
                    setNbPionsBateau(getNbPionsBateau() - 2);
                }
            }
        }
        cartesTransportPosees.clear();
        ports.add(ville);
        jeu.getPortsLibres().remove(ville);
        tour = false;
    }

    public boolean destinationEstComplete(Destination d) {
        String villeDépart = d.getVilles().get(0);
        String villeArrivée = d.getVilles().get(d.getVilles().size() - 1);
        Set<String> villesCapturées = new HashSet<>();
        for (Route route : routes) {
            villesCapturées.add(route.getVille1().nom());
            villesCapturées.add(route.getVille2().nom());
        }
        if (!villesCapturées.contains(villeDépart) || !villesCapturées.contains(villeArrivée)) {
            // Si la ville de départ ou d'arrivée ne sont pas capturées, la destination ne peut pas être complète
            return false;
        }
        boolean checkOrdreDestination = false;
        if (d.getValeurSimple() == d.getValeurMax()) {
            Historique.computeIfAbsent(d, k -> new ArrayList<>());
        }

        if (d.getValeurMax() != d.getValeurSimple()) {
            if (ordreVillesACapturer.get(d) == null) {
                ordreVillesACapturer.put(d, d.getVilles());
//                System.out.println("ordreVillesACapturer : " + ordreVillesACapturer.get(d));
                ordreVillesDejaCapturer.put(d, new ArrayList<>());
                checkOrdreDestination = true;
            }
        }
        String villeActuelle = villeDépart;
        Route routeDejaVue = null;
        List<Route> routeActuelle = new ArrayList<>(routes);
        while (true) {
            Route routeSuivante = null;
            for (Route route : routeActuelle) {
                if (route.getVille1().nom().equals(villeActuelle) && villesCapturées.contains(route.getVille2().nom())) {
                    // La route relie la ville actuelle à une ville non encore capturée
                    if (d.getValeurSimple() == d.getValeurMax()) {
                        for (String v : route.getNomVilles()) {
                            if (!Historique.get(d).contains(v)) {
                                Historique.get(d).add(v);
                            }
                        }
                        System.out.println("Historique : " + Historique.get(d));
                    }
                    if (checkOrdreDestination) {
                        for (String v : route.getNomVilles()) {
                            if (!ordreVillesDejaCapturer.get(d).contains(v)) {
                                ordreVillesDejaCapturer.get(d).add(v);
                            }
                        }
//                        System.out.println("ordreVillesDejaCapturer : " + ordreVillesDejaCapturer.get(d));
                    }
                    if (route.getVille2().nom().equals(villeArrivée)) {
                        // Si la ville d'arrivée est atteinte, la destination est complète
                        return true;
                    }
                    routeSuivante = route;
                    routeDejaVue = route;
                    break;
                } else if (route.getVille2().nom().equals(villeActuelle) && villesCapturées.contains(route.getVille1().nom())) {
                    // La route relie la ville actuelle à une ville non encore capturée
                    if (d.getValeurSimple() == d.getValeurMax()) {
                        for (String v : route.getNomVillesInverser()) {
                            if (!Historique.get(d).contains(v)) {
                                Historique.get(d).add(v);
                            }
                        }
                        System.out.println("Historique : " + Historique.get(d));
                    }
                    if (checkOrdreDestination) {
                        for (String v : route.getNomVillesInverser()) {
                            if (!ordreVillesDejaCapturer.get(d).contains(v)) {
                                ordreVillesDejaCapturer.get(d).add(v);
                            }
                        }
//                        System.out.println("ordreVillesDejaCapturer : " + ordreVillesDejaCapturer.get(d));
                    }
                    if (route.getVille1().nom().equals(villeArrivée)) {
                        // Si la ville d'arrivée est atteinte, la destination est complète
                        return true;
                    }
                    routeSuivante = route;
                    routeDejaVue = route;
                    break;
                }
            }
            if (routeSuivante == null) {
                // Si aucune route ne relie la ville actuelle à une ville non encore capturée, la destination n'est pas complète
                return false;
            }
            villesCapturées.add(routeSuivante.getVille1().nom());
            villesCapturées.add(routeSuivante.getVille2().nom());
            if (routeSuivante.getVille1().nom().equals(villeActuelle)) {
                villeActuelle = routeSuivante.getVille2().nom();
            } else {
                villeActuelle = routeSuivante.getVille1().nom();
            }
            routeActuelle.remove(routeDejaVue);
            routeDejaVue = null;
            log("ville actuelle : " + villeActuelle);
        }
    }

    //TODO calculerScoreFinal()
    public int calculerScoreFinal() {
        int scoreFinal = 0;

        for (Destination d : getDestinations()) {
            boolean isComplete = destinationEstComplete(d);
            boolean isCorrectOrder = false;
            if (isComplete && d.getValeurMax() != d.getValeurSimple()) {
                List<String> expectedOrder = ordreVillesACapturer.get(d);
                List<String> capturedOrder = ordreVillesDejaCapturer.get(d);
                isCorrectOrder = expectedOrder.equals(capturedOrder);
            }

            if (isComplete) {
                if (d.getValeurMax() == d.getValeurSimple()) scoreFinal += d.getValeurSimple() + getScorePort(d);
                else {
                    if (isCorrectOrder) scoreFinal += d.getValeurSimple() + getScorePort(d);
                }
            } else scoreFinal -= d.getPenalite();
        }

        scoreFinal += ((ports.size() - 3) * 4);
        scoreFinal += score;
        return scoreFinal;
    }

    public int getScorePort(Destination d){
        int scorePort;
        int compteur = 0;
        boolean itineraire = d.getVilles().size() > 2;
        if (itineraire){
            for (String v : ordreVillesDejaCapturer.get(d)){
                for (Ville p : ports){
                    if (p.nom().equals(v)){
                        compteur++;
                    }
                }
            }
        }
        else{
            for (String v : Historique.get(d)){
                for (Ville p : ports){
                    if (p.nom().equals(v)){
                        compteur++;
                    }
                }
            }
        }
        switch (compteur){
            case 0 -> scorePort = 0;
            case 1 -> scorePort = 20;
            case 2 -> scorePort = 30;
            default -> scorePort = 40;
        }
        return scorePort;
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
            } else {
                if (compteur != 1 || !selectedCard.getType().equals(TypeCarteTransport.JOKER)) {
                    if (compteur < 2 && (selectedCard.getType().equals(TypeCarteTransport.BATEAU) || selectedCard.getType().equals(TypeCarteTransport.WAGON))) {
                        choisirPileJoueur(cartesTransport, selectedCard);
                        if (jeu.getPilesDeCartesWagon().isEmpty() && jeu.getPilesDeCartesBateau().isEmpty() && jeu.getCartesTransportVisibles().size() < 2) {
                            for (CarteTransport carteTransport : jeu.getCartesTransportVisibles()) {
                                if (carteTransport.getType().equals(TypeCarteTransport.JOKER)){
                                    compteur += 2;
                                }
                            }
                        }
                        compteur++;
                        fin = true;
                        if (compteur >= 2) resetCounterAndTurn();
                    }
                }
            }
        }
    }

    private void joueurPiocheDansLeDeck(List<CarteTransport> cartesTransport, String inputTour) {
        //si le deck est vide, on remet les cartes dans le deck
        if (jeu.getPilesDeCartesWagon().isEmpty()) {
            jeu.getPilesDeCartesWagon().setPilePioche();
        } else if (jeu.getPilesDeCartesBateau().isEmpty()) {
            jeu.getPilesDeCartesBateau().setPilePioche();
        }
        if (jeu.getPilesDeCartesWagon().getNbCartes() > 0 && inputTour.equals("WAGON")) {
            cartesTransport.add(jeu.piocherCarteWagon());
            updateCounterAndTurn();
        } else if (jeu.getPilesDeCartesBateau().getNbCartes() > 0 && inputTour.equals("BATEAU")) {
            cartesTransport.add(jeu.piocherCarteBateau());
            updateCounterAndTurn();
        } else {
            log("Vous ne pouvez pas piocher dans cette pile");
        }
    }

    private void choisirPileJoueur(List<CarteTransport> cartesTransport, CarteTransport selectedCard) {
        cartesTransport.add(selectedCard);
        boolean carteNonTire = true;
        jeu.getCartesTransportVisibles().remove(selectedCard);
        PilesCartesTransport pilesDeCartesWagon = jeu.getPilesDeCartesWagon();
        PilesCartesTransport pilesDeCartesBateau = jeu.getPilesDeCartesBateau();
        if (!pilesDeCartesWagon.isEmpty() || !pilesDeCartesBateau.isEmpty()) {
            if (pilesDeCartesWagon.isEmpty())
                log("Veuillez choisir une carte dans la pile de cartes bateau. Car la pile de cartes wagon est vide");
            else if (pilesDeCartesBateau.isEmpty())
                log("Veuillez choisir une carte dans la pile de cartes wagon. Car la pile de cartes bateau est vide");
            while (carteNonTire) {
                String choixPioche = choisir("<h3>Quelle carte voulez vous piocher?</h3>", Arrays.asList("WAGON", "BATEAU"), null, false);
                if (choixPioche.equals("WAGON") && !jeu.getPilesDeCartesWagon().isEmpty()) {
                    CarteTransport carteWagon = pilesDeCartesWagon.piocher();
                    if (carteWagon != null) jeu.getCartesTransportVisibles().add(carteWagon);
                    //si il ya 3 joker dans la pile de carte visibles on remet les cartes dans le deck et on pioche aléatoirement dans les deux pile de cartes
                    carteNonTire = false;
                } else if (choixPioche.equals("BATEAU") && !jeu.getPilesDeCartesBateau().isEmpty()) {
                    CarteTransport carteBateau = pilesDeCartesBateau.piocher();
                    carteNonTire = false;
                    if (carteBateau != null) jeu.getCartesTransportVisibles().add(carteBateau);
                } else if (jeu.getPilesDeCartesWagon().isEmpty() && jeu.getPilesDeCartesBateau().isEmpty()) {
                    log("Vous ne pouvez plus piocher les piles de cartes");
                    carteNonTire = false;
                }
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
        if (jeu.getPileDeCartesDestination().size() >= 4) {
            jeu.PiocheCartesDestination(3, 4, this);
            tour = false;
        } else if (jeu.getPileDeCartesDestination().size() > 0 && jeu.getPileDeCartesDestination().size() < 4) {
            jeu.PiocheCartesDestination(jeu.getPileDeCartesDestination().size() - 1, jeu.getPileDeCartesDestination().size(), this);
            tour = false;
        } else {
            log("Il n'y a plus de cartes destination");
        }
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

    public String getScore() {
        return String.valueOf(score);
    }

    public enum CouleurJouer {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }
}


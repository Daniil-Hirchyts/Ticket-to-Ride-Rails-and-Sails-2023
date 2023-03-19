```java

private void capturerRoute(String inputTour) {
//Initialization of variables-----------------------------------------------------------------------------------------------------------
        List<CarteTransport> carteCorrecte = new ArrayList<>();
        int cpt = 0;
        List<Route> routeTemp = new ArrayList<>(jeu.getRoutesLibres());
        for (Route r : routeTemp) {
//Route Terrestre-----------------------------------------------------------------------------------------------------------------------
            if (r.getNom().equals(inputTour) && r instanceof RouteTerrestre) {
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
                    List<String> choixCarteWagon = new ArrayList<>();
                    for (CarteTransport c : carteCorrecte) choixCarteWagon.add(c.getNom());
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
                }
//Route Maritime------------------------------------------------------------------------------------------------------------------------
            } else if (r.getNom().equals(inputTour) && r instanceof RouteMaritime) {
                for (CarteTransport c : cartesTransport) {
                    if (!r.getCouleur().equals(Couleur.GRIS)) {
                        if (c.getType().equals(TypeCarteTransport.JOKER)) {
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
                for (CarteTransport c : carteCorrecte) if (c.estDouble()) compteurCarteDouble++;
                if (compteurCarteDouble == carteCorrecte.size()) {
                    if (r.getLongueur() % 2 == 0) cpt = r.getLongueur();
                    else cpt = 0;
                }
                if (cpt >= r.getLongueur()) {
                    List<String> choixCarteBateau = new ArrayList<>();
                    for (CarteTransport c : carteCorrecte) choixCarteBateau.add(c.getNom());
                    do {
                        if (r.getLongueur() - compterCarte(cartesTransportPosees) <= 1)
                            for (CarteTransport c : carteCorrecte)
                                if (c.estDouble()) choixCarteBateau.remove(c.getNom());
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
                    for (CarteTransport c : cartesTransportPosees) jeu.getPilesDeCartesBateau().defausser(c);
                    score += r.getScore();
                    cartesTransportPosees.clear();
                    routes.add(r);
                    if (jeu.getJoueurs().size() <= 3) jeu.getRoutesLibres().remove(r.getRouteParallele());
                    jeu.getRoutesLibres().remove(r);
                    cpt = 0;
                    tour = false;
                }
            }
//Route RoutePaire----------------------------------------------------------------------------------------------------------------------
            else if (r.getNom().equals(inputTour) && r instanceof RoutePaire) {
                List<Integer> nbcarteCouleur = new ArrayList<>();
                List<Integer> OpposerNbCarteCouleur = new ArrayList<>();
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
                    List<String> choixCarteWagon = new ArrayList<>();
                    List<Couleur> DebutCouleur = new ArrayList<>();
                    List<Integer> nbCouleurValide = new ArrayList<>();
                    List<CarteTransport> carteCorrecteTemp = new ArrayList<>();
                    List<CarteTransport> lastcarteJoker = new ArrayList<>();
                    int nbCouleur = 0;
                    int nbValide = 0;
                    int indexToRemove = -1;
                    boolean removeAllOne = false;
                    for (int i = 0; i < OpposerNbCarteCouleur.size(); i++)
                        if (OpposerNbCarteCouleur.get(i) == 0) nbcarteCouleur.set(i, 0);
                    for (CarteTransport c : carteCorrecte) choixCarteWagon.add(c.getNom());
                    for (int i = 0; i < r.getLongueur(); i++) nbCouleurValide.add(0);
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
                                    nbCouleurValide.replaceAll(integer -> integer + 1);
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
        }
    }
```

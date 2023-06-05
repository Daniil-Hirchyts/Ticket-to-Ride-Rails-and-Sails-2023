package fr.umontpellier.iut.rails.mecanique;

import fr.umontpellier.iut.rails.IGraphe;

import java.util.*;

public class Graphe implements IGraphe {

    private Map<Integer, HashSet<Arete>> mapAretes;

    //return le chemin en nomDeRoute entre depart et arrivee

    public Graphe() {
        mapAretes = new HashMap<>();
    }

    public Graphe(Collection<Route> ensembleDeRoutes) {
        this();
        for (Route r : ensembleDeRoutes) {
            Arete a = new Arete(r.getVille1().getId(), r.getVille2().getId(), r);
            ajouterArete(a);
            if (r.getRouteParallele() != null) {
                Arete a2 = new Arete(r.getVille1().getId(), r.getVille2().getId(), r.getRouteParallele());
                ajouterArete(a2);
            }
        }
    }

    public void ajouterArete(Arete a) {
        if (!existeArete(a)) {
            ajouterSommet(a.i());
            ajouterSommet(a.j());
            mapAretes.get(a.i()).add(a);
            mapAretes.get(a.j()).add(a);
        }
    }
    public boolean existeArete(Arete a) {
        return mapAretes.containsKey(a.i()) && mapAretes.get(a.i()).contains(a) && mapAretes.containsKey(a.j()) && mapAretes.get(a.j()).contains(a);
    }
    public void ajouterSommet(Integer v) {
        mapAretes.putIfAbsent(v, new HashSet<>());
    }
    public List<Integer> parcoursSansRepetition(int depart, int arrivee, boolean pondere) {
        if (!mapAretes.containsKey(depart) || !mapAretes.containsKey(arrivee)) {
            return null; // Sommet de départ ou d'arrivée non trouvé
        }

        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecesseurs = new HashMap<>();
        Set<Integer> visites = new HashSet<>();

        for (Integer sommet : mapAretes.keySet()) {
            distances.put(sommet, Integer.MAX_VALUE);
        }

        distances.put(depart, 0);

        while (!visites.contains(arrivee)) {
            Integer courant = obtenirSommetPlusProche(distances, visites);

            if (courant == null) {
                break; // Pas de chemin trouvé
            }

            visites.add(courant);

            if (!mapAretes.containsKey(courant)) {
                continue;
            }

            HashSet<Arete> aretes = mapAretes.get(courant);
            for (Arete arete : aretes) {
                int voisin = arete.j();
                int poids = pondere ? arete.route().getLongueur() : 1;
                int distance = distances.get(courant) + poids;

                if (distance < distances.get(voisin)) {
                    distances.put(voisin, distance);
                    predecesseurs.put(voisin, courant);
                }
            }
        }

        return construireChemin(depart, arrivee, predecesseurs);
    }

    private Integer obtenirSommetPlusProche(Map<Integer, Integer> distances, Set<Integer> visites) {
        int distanceMin = Integer.MAX_VALUE;
        Integer sommetPlusProche = null;

        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            Integer sommet = entry.getKey();
            int distance = entry.getValue();

            if (!visites.contains(sommet) && distance < distanceMin) {
                distanceMin = distance;
                sommetPlusProche = sommet;
            }
        }

        return sommetPlusProche;
    }

    private List<Integer> construireChemin(int depart, int arrivee, Map<Integer, Integer> predecesseurs) {
        List<Integer> chemin = new ArrayList<>();
        Integer courant = arrivee;

        while (courant != null && !courant.equals(depart)) {
            chemin.add(courant);
            courant = predecesseurs.get(courant);
        }

        if (courant != null && courant.equals(depart)) {
            chemin.add(depart);
            Collections.reverse(chemin);
        }

        return chemin;
    }

    public List<String> parcoursSansRepetition(List<Integer> listeSommets) {
        if (listeSommets == null || listeSommets.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> chemin = new ArrayList<>();
        Set<Integer> sommetsVisites = new HashSet<>();

        for (int i = 0; i < listeSommets.size() - 1; i++) {
            int depart = listeSommets.get(i);
            int arrivee = listeSommets.get(i + 1);

            List<Integer> sousChemin = parcoursSansRepetition(depart, arrivee, false);

            if (sousChemin.isEmpty()) {
                return new ArrayList<>();
            }

            sousChemin.remove(0);
            chemin.addAll(sousChemin);
            sommetsVisites.addAll(sousChemin);
        }

        chemin.add(0, listeSommets.get(0));
        List<String> returnthis = new ArrayList<>();
        for (int i = 0; i < chemin.size(); i++) {
            returnthis.add("R" + chemin.get(i));
        }
        return returnthis;
    }

}

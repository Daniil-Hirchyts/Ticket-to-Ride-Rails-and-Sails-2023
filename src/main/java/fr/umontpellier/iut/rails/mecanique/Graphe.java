package fr.umontpellier.iut.rails.mecanique;

import fr.umontpellier.iut.rails.IGraphe;

import java.util.*;

public class Graphe implements IGraphe {

    private Map<Integer, HashSet<Arete>> mapAretes;

    private static final int MAX_POIDS = Integer.MAX_VALUE;
    private static final int PAS_DE_PERE = -1;

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
    public int nbSommets() {
        return mapAretes.size();
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

    public boolean contientSommet(Integer v) {
        return mapAretes.containsKey(v);
    }
    public Set<Integer> getVoisins(int v) {
        if (contientSommet(v)) {
            HashSet<Integer> voisins = new HashSet<>();
            for (Arete a : mapAretes.get(v)) voisins.add(a.getAutreSommet(v));
            return voisins;
        }
        return Collections.emptySet();
    }
    public List<Integer> parcoursSansRepetition(int depart, int arrivee, boolean pondere) {
        return pondere
                ? parcoursPondere(depart, arrivee)
                : parcoursNonPondere(depart, arrivee);
    }

    private List<Integer> parcoursPondere(int depart, int arrivee) {
        List<Integer> chemin = initialiserChemin();
        if (depart == arrivee) {
            chemin.add(depart);
            return chemin;
        }
        List<Integer> sommetstous = initialiserSommetsTous();
        int[] pere = initialiserPere();
        int[] poids = initialiserPoids();
        poids[sommetstous.indexOf(depart)] = 0;
        PriorityQueue<Integer> file = new PriorityQueue<>(Comparator.comparingInt(v -> poids[sommetstous.indexOf(v)]));
        file.add(depart);
        while (!file.isEmpty()) {
            int sommet = file.poll();
            if (sommet == arrivee) break;

            mettreAJourVoisins(sommet, file, poids, pere, sommetstous);
        }
        return construireChemin(depart, arrivee, chemin, sommetstous, pere);
    }

    public Set<Integer> ensembleSommets() {
        return mapAretes.keySet();
    }
    private List<Integer> parcoursNonPondere(int depart, int arrivee) {
        List<Integer> chemin = initialiserChemin();
        if (depart == arrivee) {
            chemin.add(depart);
            return chemin;
        }
        boolean[] marque = new boolean[nbSommets()];
        int[] pere = initialiserPere();
        Queue<Integer> file = new LinkedList<>();
        file.add(depart);
        List<Integer> sommetstous = new ArrayList<>(ensembleSommets());
        marque[sommetstous.indexOf(depart)] = true;
        while (!file.isEmpty()) {
            int sommet = file.poll();
            for (int voisin : getVoisins(sommet)) {
                if (!marque[sommetstous.indexOf(voisin)]) {
                    marque[sommetstous.indexOf(voisin)] = true;
                    pere[sommetstous.indexOf(voisin)] = sommet;
                    file.add(voisin);
                }
            }
        }
        return getIntegers(depart, arrivee, chemin, sommetstous, pere);
    }

    private List<Integer> initialiserChemin() {
        return new ArrayList<>();
    }

    private List<Integer> initialiserSommetsTous() {
        List<Integer> sommetstous = new ArrayList<>();
        for (int i : ensembleSommets()) sommetstous.add(i);
        return sommetstous;
    }

    private int[] initialiserPere() {
        int[] pere = new int[nbSommets()];
        Arrays.fill(pere, PAS_DE_PERE);
        return pere;
    }

    private int[] initialiserPoids() {
        int[] poids = new int[nbSommets()];
        Arrays.fill(poids, MAX_POIDS);
        return poids;
    }

    public Arete getArete(int sommetDepart, int sommetArrivee) {
        Set<Arete> aretes = mapAretes.get(sommetDepart);
        if (aretes != null)
            for (Arete arete : aretes)
                if (arete.incidenteA(sommetArrivee)) return arete;
        return null;
    }

    private void mettreAJourVoisins(int sommet, PriorityQueue<Integer> file, int[] poids, int[] pere, List<Integer> sommetstous) {
        for (int voisin : getVoisins(sommet)) {
            int poidsVoisin = poids[sommetstous.indexOf(sommet)] + getArete(sommet, voisin).route().getLongueur();
            if (poidsVoisin < poids[sommetstous.indexOf(voisin)]) {
                pere[sommetstous.indexOf(voisin)] = sommet;
                poids[sommetstous.indexOf(voisin)] = poidsVoisin;
                file.remove(voisin);
                file.add(voisin);
            }
        }
    }

    private List<Integer> construireChemin(int depart, int arrivee, List<Integer> chemin, List<Integer> sommetstous, int[] pere) {
        if (pere[sommetstous.indexOf(arrivee)] == PAS_DE_PERE) return chemin;
        int sommet = arrivee;
        while (sommet != depart) {
            chemin.add(sommet);
            sommet = pere[sommetstous.indexOf(sommet)];
        }
        chemin.add(depart);
        Collections.reverse(chemin);
        return chemin;
    }


    private List<Integer> getIntegers(int depart, int arrivee, List<Integer> chemin, List<Integer> sommetstous, int[] pere) {
        if (pere[sommetstous.indexOf(arrivee)] == -1) return chemin;
        else {
            int sommet = arrivee;
            while (sommet != depart) {
                chemin.add(sommet);
                sommet = pere[sommetstous.indexOf(sommet)];
            }
            chemin.add(depart);
            Collections.reverse(chemin);
            return chemin;
        }
    }

    public List<Integer> parcoursSansRepetition(List<Integer> listeSommets) {
        if (listeSommets == null || listeSommets.isEmpty()) return new ArrayList<>();
        List<Integer> chemin = new ArrayList<>();
        Set<Integer> sommetsVisites = new HashSet<>();
        for (int i = 0; i < listeSommets.size() - 1; i++) {
            int depart = listeSommets.get(i);
            int arrivee = listeSommets.get(i + 1);
            List<Integer> sousChemin = parcoursSansRepetition(depart, arrivee, false);
            if (sousChemin.isEmpty()) return new ArrayList<>();
            if (sousChemin.size() > 1) sousChemin.remove(0);
            chemin.addAll(sousChemin);
            sommetsVisites.addAll(sousChemin);
        }
        chemin.add(0, listeSommets.get(0));
        return chemin;
    }

    public HashSet<Arete> getAretes() {
        HashSet<Arete> aretes = new HashSet<>();
        for (HashSet<Arete> hashSet : mapAretes.values()) {
            aretes.addAll(hashSet);
        }
        return aretes;
    }
}

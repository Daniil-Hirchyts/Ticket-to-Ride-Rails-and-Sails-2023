package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.mecanique.Arete;

import java.util.HashSet;
import java.util.List;

public interface IGraphe {

    List<Integer> parcoursSansRepetition(List<Integer> routes);

    HashSet<Arete> getAretes();
}

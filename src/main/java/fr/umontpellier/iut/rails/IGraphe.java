package fr.umontpellier.iut.rails;

import java.util.List;

public interface IGraphe {

    List<String> parcoursSansRepetition(List<Integer> routes);
}

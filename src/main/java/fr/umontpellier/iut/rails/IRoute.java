package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.mecanique.data.Ville;
import javafx.beans.property.ObjectProperty;

public interface IRoute {
    ObjectProperty<IJoueur> proprietaireProperty();
    String getNom();
    Ville getVille2();
    Ville getVille1();
}
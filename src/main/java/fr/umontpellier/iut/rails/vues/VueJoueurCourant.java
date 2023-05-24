package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 *
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends Pane {
    IJeu jeu;
    VBox VboxJoueurCourant;
    Label nomJoueur;

    List<ICarteTransport> cartesTransport;

    public VueJoueurCourant(IJeu jeu) {
        VboxJoueurCourant = new VBox();
        nomJoueur = new Label();
        this.jeu = jeu;
        cartesTransport = new ArrayList<>();
        VboxJoueurCourant.getChildren().add(nomJoueur);
        getChildren().add(VboxJoueurCourant);
    }

    public void creerBindings() {
        //add un listener qui change le nom du joueur courant
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            nomJoueur.setText(newValue.getNom());
        });
        //implémenter le listener qui change les cartes transport du joueur courant
    }

}

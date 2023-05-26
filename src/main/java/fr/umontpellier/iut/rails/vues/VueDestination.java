package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Cette classe représente la vue d'une carte Destination.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueDestination extends Button {

    private final IDestination destination;

    public VueDestination(IDestination destination) {
        setId("destinationButton");
        this.destination = destination;
        setText(destination.getVilles().toString());
        setOnAction(event -> {
            ((VueDuJeu) getScene().getRoot()).getJeu().uneDestinationAEteChoisie(destination);
        });
    }

    public IDestination getDestination() {
        return destination;
    }

}

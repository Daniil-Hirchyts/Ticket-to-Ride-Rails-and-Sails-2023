package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Cette classe représente la vue d'une carte Destination.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueDestinationChoisi extends Label {

    private final IDestination destination;

    public VueDestinationChoisi(IDestination destination) {
        setId("destinationLabel");
        this.destination = destination;
        setText(destination.getVilles().toString());
    }

    public IDestination getDestination() {
        return destination;
    }

}

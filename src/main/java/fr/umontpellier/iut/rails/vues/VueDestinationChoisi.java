package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Cette classe représente la vue d'une carte Destination.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueDestinationChoisi extends VBox {

    private final IDestination destination;

    public VueDestinationChoisi(IDestination destination) {
        setId("destinationTicket");
        this.destination = destination;
        for (String ville : destination.getVilles()) {
            Label label = new Label(ville);
            label.setId("destinationLabel");
            getChildren().add(label);
        }
    }

    public IDestination getDestination() {
        return destination;
    }

}

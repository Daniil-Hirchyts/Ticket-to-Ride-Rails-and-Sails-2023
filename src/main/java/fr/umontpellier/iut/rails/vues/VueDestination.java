package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Cette classe représente la vue d'une carte Destination.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueDestination extends VBox {
    protected final IDestination destination;

    public VueDestination(IDestination destination) {
        this(destination, false);
    }

    public VueDestination(IDestination destination, boolean clickable) {
        this.destination = destination;
        for (String ville : destination.getVilles()) {
            Label label = new Label(ville);
            label.setId("destinationLabel");
            getChildren().add(label);
        }

        if (clickable) {
            this.setOnMouseClicked(event -> {
                ((VueDuJeu) getScene().getRoot()).getJeu().uneDestinationAEteChoisie(destination);
            });
            setId("destinationTicketChoix");
        } else setId("destinationTicket");
    }

    public IDestination getDestination() {
        return destination;
    }
}

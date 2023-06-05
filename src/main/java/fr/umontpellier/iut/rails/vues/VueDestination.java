package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IRoute;
import fr.umontpellier.iut.rails.mecanique.data.Ville;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Cette classe représente la vue d'une carte Destination.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueDestination extends VBox {
    protected final IDestination destination;
    private VueDuJeu vueDuJeu;

    public VueDestination(IDestination destination, VueDuJeu vueDuJeu) {
        this(destination, false, vueDuJeu);
    }

    public VueDestination(IDestination destination, boolean clickable,VueDuJeu vueDuJeu) {
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

        this.setOnMouseEntered(event -> {
            List<Integer> villeToInteger = new ArrayList<>();
            //remove V from string
            for (String ville : destination.getVilles()) {

            }
            for (String route : vueDuJeu.getJeu().getGraphe().parcoursSansRepetition(villeToInteger)) {
                for (Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute()) {
                    if (rectangle.getId().equals(route)) {
                        rectangle.setId("routeGlow");
                    }
                }
            }
        });

        this.setOnMouseExited(event -> {
            for (Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute()) {
                if (rectangle.getId().equals("routeGlow")) {
                    rectangle.setId("rectPosee");
                }
            }
        });
    }

    public IDestination getDestination() {
        return destination;
    }
}

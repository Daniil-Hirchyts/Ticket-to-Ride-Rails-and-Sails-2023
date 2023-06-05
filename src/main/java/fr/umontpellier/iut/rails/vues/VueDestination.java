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

import java.util.HashSet;

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
            //TODO : DETERMINE LE CHEMIN LE PLUS COURT ENTRE LES VILLES DE LA DESTINATION ET MET EN SURBRILLANCE LES ROUTES CORRESPONDANTES
            for(Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute()){
                rectangle.setStyle("-fx-fill: #000000");
                rectangle.setStyle("-fx-opacity: 1");
            }

//            String ville1 = destination.getVilles().get(0);
//            String ville2 = destination.getVilles().get(1);
//            HashSet<String> villeCapacite = new HashSet<>();
//            for (String ville : DonneesGraphiques.getVilles().keySet()) {
//
//            }

        });
    }

    public IDestination getDestination() {
        return destination;
    }
}

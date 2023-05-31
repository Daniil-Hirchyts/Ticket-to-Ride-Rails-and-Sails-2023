package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

/**
 * Cette classe représente la vue d'une carte Transport.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueCarteTransport extends Pane {
    private final ICarteTransport carteTransport;


    public VueCarteTransport(ICarteTransport carteTransport, int nbCartes, boolean dansDeck) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/carteTransport.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.carteTransport = carteTransport;

        String type;
        if (carteTransport.estBateau()) {
            if (carteTransport.estDouble()) type = "DOUBLE";
            else type = "BATEAU";
        } else if (carteTransport.estWagon()) type = "WAGON";
        else if (carteTransport.estJoker()) type = "JOKER";
        else type = "";
        String ancre = carteTransport.getAncre() ? "-A" : "";
        String path = "images/cartesWagons/carte-" + type + "-" + carteTransport.getStringCouleur() + ancre + ".png";

        setStyle("-fx-background-image: url('" + path + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: cover;");

        if (!dansDeck) {
        setOnMouseClicked(event -> {
            ((VueDuJeu) getScene().getRoot()).getJeu().uneCarteTransportAEteChoisie(carteTransport);
        });
        } else {
            setOnMouseClicked(event -> {
                ((VueDuJeu) getScene().getRoot()).getJeu().uneCarteDuJoueurEstJouee(carteTransport);
            });
        }

    }

    public ICarteTransport getCarteTransport() {
        return carteTransport;
    }


    public void horizontalLayout() {
        setRotate(0);
    }

    public void verticalLayout() {
        setRotate(270);
    }

}

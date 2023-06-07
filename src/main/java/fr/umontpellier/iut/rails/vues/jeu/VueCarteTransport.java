package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.ICarteTransport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Cette classe représente la vue d'une carte Transport.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueCarteTransport extends Pane {
    private final ICarteTransport carteTransport;

    @FXML
    private Pane carteImage;

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

        carteImage.setStyle("-fx-background-image: url('" + path + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: cover;");


        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(0.1);
        colorAdjust.setContrast(0.11);
        colorAdjust.setBrightness(0.1);
        carteImage.setEffect(colorAdjust);


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

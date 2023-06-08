package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.ICarteTransport;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

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
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VueCarteTransport.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), this);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200), this);
        translateTransition.setFromY(0);
        translateTransition.setToY(-20);

        setOnMouseEntered(event -> {
            scaleTransition.playFromStart();
            translateTransition.playFromStart();
        });

        setOnMouseExited(event -> {
            scaleTransition.setRate(-1);
            translateTransition.setRate(-1);
            scaleTransition.play();
            translateTransition.play();
        });

        getStyleClass().add("carte");

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
        setId("carte-horizontal");
        setRotate(0);
    }

    public void verticalLayout() {
//        setId("carte-vertical");
        setRotate(270);
    }



}

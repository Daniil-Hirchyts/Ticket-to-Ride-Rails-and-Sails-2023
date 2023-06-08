package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.ICarteTransport;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;

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

    private void setupHoverTransition(double translateToX, double translateToY) {
        ScaleTransition scaleTransitionHover = new ScaleTransition(Duration.millis(200), this);
        scaleTransitionHover.setFromX(1);
        scaleTransitionHover.setFromY(1);
        scaleTransitionHover.setToX(1.25);
        scaleTransitionHover.setToY(1.25);

        TranslateTransition translateTransitionHover = new TranslateTransition(Duration.millis(200), this);
        translateTransitionHover.setFromX(0);
        translateTransitionHover.setFromY(0);
        translateTransitionHover.setToX(translateToX);
        translateTransitionHover.setToY(translateToY);

        setOnMouseEntered(event -> {
            scaleTransitionHover.playFromStart();
            translateTransitionHover.playFromStart();
        });

        ScaleTransition scaleTransitionRestore = new ScaleTransition(Duration.millis(200), this);
        scaleTransitionRestore.setToX(1);
        scaleTransitionRestore.setToY(1);

        TranslateTransition translateTransitionRestore = new TranslateTransition(Duration.millis(200), this);
        translateTransitionRestore.setToX(0);
        translateTransitionRestore.setToY(0);

        setOnMouseExited(event -> {
            scaleTransitionRestore.playFromStart();
            translateTransitionRestore.playFromStart();
        });
    }

    public void horizontalLayout() {
        setId("carte-horizontal");
        setRotate(0);
        setupHoverTransition(35, 0);
    }

    public void verticalLayout() {
        setId("carte-vertical");
        setRotate(270);
        setupHoverTransition(0, -35);
    }

    public Pane getCarteImage() {
return carteImage;
    }

    public void setCarteImagePosee() {
        setDisable(true);
        verticalLayout();
        carteImage.setOpacity(0.75);
        setStyle("-fx-background-color: #747474;");
    }
}

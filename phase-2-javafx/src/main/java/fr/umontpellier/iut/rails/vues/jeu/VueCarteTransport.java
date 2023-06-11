package fr.umontpellier.iut.rails.vues.jeu;

import java.io.IOException;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.vues.layout.SoundEffect;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class VueCarteTransport extends Pane {
    private final ICarteTransport carteTransport;
    private final SoundEffect soundEffect;
    private boolean dansDeck = false;
    private static final String FXML_PATH = "fxml/jeu/VueCarteTransport.fxml";
    private static final String IMAGE_STYLE = "-fx-background-image: url('%s'); " +
            "-fx-background-position: center center; " +
            "-fx-background-repeat: stretch; " +
            "-fx-background-size: cover;";
    @FXML
    private Pane carteImage;

    public VueCarteTransport(ICarteTransport carteTransport, int nbCartes, boolean dansDeck) {
        this.carteTransport = carteTransport;
        this.dansDeck = dansDeck;
        soundEffect = new SoundEffect();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VueCarteTransport.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getStyleClass().add("carte");

        String type;
        if (carteTransport.estBateau()) {
            if (carteTransport.estDouble()) type = "DOUBLE";
            else type = "BATEAU";
        } else if (carteTransport.estWagon()) type = "WAGON";
        else if (carteTransport.estJoker()) type = "JOKER";
        else type = "";
        String ancre = carteTransport.getAncre() ? "-A" : "";
        String path = "images/cartesWagons/carte-" + type + "-" + carteTransport.getStringCouleur() + ancre + ".png";

        carteImage.setStyle(String.format(IMAGE_STYLE, path));

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(0.1);
        colorAdjust.setContrast(0.11);
        colorAdjust.setBrightness(0.1);
        carteImage.setEffect(colorAdjust);

        setSoundEffect();
    }
    private void setSoundEffect() {
        if (!dansDeck) {
            setOnMouseClicked(event -> {
                soundEffect.getPiocherCarteVisible().play();
                ((VueDuJeu) getScene().getRoot()).getJeu().uneCarteTransportAEteChoisie(carteTransport);
            });
        } else {
            setOnMouseClicked(event -> {
                soundEffect.getPiocherCarteVisible().play();
                ((VueDuJeu) getScene().getRoot()).getJeu().uneCarteDuJoueurEstJouee(carteTransport);
            });
        }
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

        ScaleTransition scaleTransitionRestore = new ScaleTransition(Duration.millis(200), this);
        scaleTransitionRestore.setToX(1);
        scaleTransitionRestore.setToY(1);

        TranslateTransition translateTransitionRestore = new TranslateTransition(Duration.millis(200), this);
        translateTransitionRestore.setToX(0);
        translateTransitionRestore.setToY(0);

        setOnMouseEntered(event -> {
            scaleTransitionHover.playFromStart();
            translateTransitionHover.playFromStart();
        });

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
    public void setCarteImagePosee() {
        setDisable(true);
        verticalLayout();
        carteImage.setOpacity(0.75);
        setStyle("-fx-background-color: #747474;");
    }
}

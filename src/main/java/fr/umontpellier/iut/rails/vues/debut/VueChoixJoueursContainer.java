package fr.umontpellier.iut.rails.vues.debut;

import java.io.IOException;

import fr.umontpellier.iut.rails.vues.layout.SoundEffect;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class VueChoixJoueursContainer extends GridPane {

    @FXML
    private Pane interfaceOfChoose;
    @FXML
    private HBox topBarBackground;
    @FXML
    private VBox titleBox;
    @FXML
    private VBox vboxJoueurs;
    @FXML
    private VBox btnBox;
    @FXML
    private Button btnStartChoix;
    @FXML
    private Button btnQuitter;
    @FXML
    private Button validation;
    @FXML
    private ImageView finalTrain;
    @FXML
    private HBox backgroundBarBlur;
    private final Button backButton = new Button("");
    private final SoundEffect soundEffect;

    public VueChoixJoueursContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/debut/VueChoixJoueursContainer.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        soundEffect = new SoundEffect();

        startAnimationTrain();
        createBindings();

        backButton.setLayoutX(0);
        backButton.setLayoutY(0);
        backButton.setTranslateY(40);
        backButton.setTranslateX(20);
        backButton.setPrefHeight(50);
        backButton.setPrefWidth(50);
        backButton.setMaxHeight(Region.USE_PREF_SIZE);
        backButton.setMaxWidth(Region.USE_PREF_SIZE);
        backButton.setMinHeight(Region.USE_PREF_SIZE);
        backButton.setMinWidth(Region.USE_PREF_SIZE);

        setId("choixJoueursContainer");
        backButton.setId("btnExit");

        switchEtat(true);
        getChildren().add(backButton);
    }

    private void createBindings() {
        topBarBackground.prefWidthProperty().bind(this.widthProperty());
        finalTrain.fitHeightProperty().bind(this.heightProperty().divide(2));
        finalTrain.fitWidthProperty().bind(this.widthProperty().divide(2));
        interfaceOfChoose.prefWidthProperty().bind(this.widthProperty());
        interfaceOfChoose.prefHeightProperty().bind(this.heightProperty());
        vboxJoueurs.layoutXProperty().bind(interfaceOfChoose.widthProperty().divide(2).subtract(vboxJoueurs.widthProperty().divide(2)));
        vboxJoueurs.layoutYProperty().bind(interfaceOfChoose.heightProperty().divide(2).subtract(vboxJoueurs.heightProperty().divide(2)));
        validation.layoutXProperty().bind(interfaceOfChoose.widthProperty().divide(2).subtract(validation.widthProperty().divide(2)));
        validation.layoutYProperty().bind(interfaceOfChoose.heightProperty().multiply(0.98).subtract(validation.heightProperty()));
        validation.scaleXProperty().bind(interfaceOfChoose.widthProperty().divide(1920));
        validation.scaleYProperty().bind(interfaceOfChoose.heightProperty().divide(1080));

        btnQuitter.setOnAction(event -> {
            soundEffect.getButtonSoundEffect().play();
            System.exit(0);
        });

        btnStartChoix.setOnAction(event -> {
            soundEffect.getButtonSoundEffect().play();
            switchEtat(false);
        });

        backButton.setOnAction(event -> {
            soundEffect.getButtonSoundEffect().play();
            switchEtat(true);
        });
    }

    private void startAnimationTrain() {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(javafx.util.Duration.seconds(5));
        translateTransition.setNode(finalTrain);
        translateTransition.setFromX(-finalTrain.getFitWidth());
        translateTransition.setToX(finalTrain.getX());
        translateTransition.play();
    }

    public VBox getVboxJoueurs() {
        return vboxJoueurs;
    }

    public Button getValidation() {
        return validation;
    }

    private void switchEtat(boolean etat) {
        interfaceOfChoose.setVisible(!etat);
        titleBox.setVisible(etat);
        btnBox.setVisible(etat);
        backgroundBarBlur.setVisible(!etat);
        backButton.setVisible(!etat);
    }
}

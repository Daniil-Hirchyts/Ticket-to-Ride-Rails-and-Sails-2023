package fr.umontpellier.iut.rails.vues.debut;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static javafx.scene.text.Font.loadFont;

public class VueChoixJoueursContainer extends GridPane {

    @FXML
    private GridPane interfaceOfChoose;
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
    private Button backButton = new Button("");


    public VueChoixJoueursContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/debut/VueChoixJoueursContainer.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setId("choixJoueursContainer");
        topBarBackground.prefWidthProperty().bind(this.widthProperty());
        startAnimationTrain();
        createBindings();

        backButton.setLayoutX(0);
        backButton.setLayoutY(0);
        backButton.setTranslateY(40);
        backButton.setTranslateX(20);
        backButton.setId("btnExit");
        backButton.setPrefHeight(50);
        backButton.setPrefWidth(50);
        backButton.setMaxHeight(Region.USE_PREF_SIZE);
        backButton.setMaxWidth(Region.USE_PREF_SIZE);
        backButton.setMinHeight(Region.USE_PREF_SIZE);
        backButton.setMinWidth(Region.USE_PREF_SIZE);

        switchEtat(true);
        getChildren().add(backButton);
    }

    private void createBindings() {
        finalTrain.fitHeightProperty().bind(this.heightProperty().divide(2));
        finalTrain.fitWidthProperty().bind(this.widthProperty().divide(2));

        btnQuitter.setOnAction(event -> {
            System.exit(0);
        });

        btnStartChoix.setOnAction(event -> {
            switchEtat(false);
        });

        backButton.setOnAction(event -> {
            switchEtat(true);
        });
    }

    private void startAnimationTrain() {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(javafx.util.Duration.seconds(3));
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

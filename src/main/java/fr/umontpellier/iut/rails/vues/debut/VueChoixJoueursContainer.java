package fr.umontpellier.iut.rails.vues.debut;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    public VueChoixJoueursContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/choixJoueursContainer.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setId("choixJoueursContainer");
        topBarBackground.prefWidthProperty().bind(this.widthProperty());

        interfaceOfChoose.setVisible(false);
        backgroundBarBlur.setVisible(false);

        startAnimationTrain();
        createBindings();
    }

    private void createBindings() {
        finalTrain.fitHeightProperty().bind(this.heightProperty().divide(2));
        finalTrain.fitWidthProperty().bind(this.widthProperty().divide(2));

        btnQuitter.setOnAction(event -> {
            System.exit(0);
        });

        btnStartChoix.setOnAction(event -> {
            interfaceOfChoose.setVisible(true);
            titleBox.setVisible(false);
            btnBox.setVisible(false);
            backgroundBarBlur.setVisible(true);
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
}

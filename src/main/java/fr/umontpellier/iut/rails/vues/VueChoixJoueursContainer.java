package fr.umontpellier.iut.rails.vues;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.io.IOException;

public class VueChoixJoueursContainer extends GridPane {

    @FXML
    private ImageView finalTrain;
    @FXML
    private HBox topBarBackground;
    @FXML
    private Label title;
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

        startAnimationTrain();
        createBindings();
    }

    private void createBindings() {
        finalTrain.fitHeightProperty().bind(this.heightProperty().divide(2));
        finalTrain.fitWidthProperty().bind(this.widthProperty().divide(2));
    }

    private void startAnimationTrain() {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(javafx.util.Duration.seconds(5));
        translateTransition.setNode(finalTrain);
        translateTransition.setFromX(-finalTrain.getFitWidth());
        translateTransition.setToX(finalTrain.getX());
        translateTransition.play();
    }
}

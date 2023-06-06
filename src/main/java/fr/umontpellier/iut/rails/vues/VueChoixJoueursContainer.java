package fr.umontpellier.iut.rails.vues;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.io.IOException;

public class VueChoixJoueursContainer extends GridPane {

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
    }
}

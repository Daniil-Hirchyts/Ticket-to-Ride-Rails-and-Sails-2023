package fr.umontpellier.iut.rails.vues.debut;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class VueImageChoix extends GridPane {

    @FXML
    private ImageView avatar;
    @FXML
    private ImageView backgroundColorAvatar;
    @FXML
    private TextField textFieldJoueur;


    public VueImageChoix() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/debut/VueImageChoix.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setId("playerChoiceBackground");
        createBindings();
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public ImageView getBackgroundColorAvatar() {
        return backgroundColorAvatar;
    }

    public TextField getTextFieldJoueur() {
        return textFieldJoueur;
    }

    public void changeBackgroundImage(String url) {
        setStyle("-fx-background-image: url('" + url + "' )");
    }


    private void createBindings() {
        this.maxWidthProperty().bind(this.prefHeightProperty().multiply(3));
    }
}

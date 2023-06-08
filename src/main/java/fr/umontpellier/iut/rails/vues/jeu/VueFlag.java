package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class VueFlag extends Pane {
    @FXML
    private ImageView rubanFlag;
    @FXML
    private Circle circleFlag;
    @FXML
    private TextField textFieldChangePions;
    @FXML
    private ImageView iconFlag;
    @FXML
    private Label valueFlag;
    IJoueur joueur;

    public VueFlag(IJoueur joueur, Object o, Image icon, String type) {
        this.joueur = joueur;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VueFlag.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textFieldChangePions.setDisable(true);
        textFieldChangePions.setVisible(false);
        iconFlag.setImage(icon);
        switch (type) {
            case "wagons" -> valueFlag.textProperty().bind(((IntegerProperty) o).asString());
            case "bateaux" -> valueFlag.textProperty().bind(((IntegerProperty) o).asString());
            case "ports" -> valueFlag.setText(String.valueOf(o));
        }
        creerBindings();
    }

    public void creerBindings() {
        rubanFlag.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ruban/" + joueur.getCouleur() + ".png"))));
    }

    public TextField getTextFieldChangePions() {
        textFieldChangePions.setVisible(false);
        textFieldChangePions.setVisible(true);
        return textFieldChangePions;
    }

    public TextField getTextField() {
        return textFieldChangePions;
    }

    public ImageView getIconFlag() {
        return iconFlag;
    }

    public Circle getCircleFlag() {
        return circleFlag;
    }
}

package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class VueFlag extends Pane {

    @FXML
    private ImageView rubanFlag;
    @FXML
    private Circle circleFlag;
    @FXML
    private ImageView iconFlag;
    @FXML
    private Label valueFlag;


    IJoueur joueur;

    public VueFlag(IJoueur joueur, Object o, Image icon, String type) {
        this.joueur = joueur;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/flag.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iconFlag.setImage(icon);
        if (type.equals("wagons"))
            valueFlag.textProperty().bind(((IntegerProperty) o).asString());
        else if (type.equals("bateaux"))
            valueFlag.textProperty().bind(((IntegerProperty) o).asString());
        else if (type.equals("ports"))
            valueFlag.setText(String.valueOf(o));
        creerBindings();
    }

    public void creerBindings() {
        rubanFlag.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ruban/" + joueur.getCouleur() + ".png"))));
//        circleFlag.setStyle("-fx-fill: " + joueur.getCouleur() + ";");
//        iconFlag.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/flag/icon/" + joueur.getCouleur() + ".png"))));
//        valueFlag.textProperty().set(String.valueOf(joueur.getNbWagons()));
    }

    public Node getIcon() {
        return iconFlag;
    }
}

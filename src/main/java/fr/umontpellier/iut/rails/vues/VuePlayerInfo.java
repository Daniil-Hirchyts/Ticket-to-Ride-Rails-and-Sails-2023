package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class VuePlayerInfo extends HBox {

    IJoueur joueur;
    @FXML
    private Label playerName;
    @FXML
    private Label playerScore;
    @FXML
    private ImageView playerAvatar;
    @FXML
    private VBox playerColor;

    public VuePlayerInfo(IJoueur joueur) {
        this.joueur = joueur;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playerInfo.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void creerBindings() {
        playerName.textProperty().set(joueur.getNom());
        playerScore.textProperty().set(String.valueOf(joueur.getScore()));
        playerAvatar.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/Avatar/avatar-" + joueur.getCouleur().toString().toUpperCase() + ".png"))));
        playerColor.setStyle("-fx-background-color: " + VueJoueurCourant.getCouleurJoueur(joueur.getCouleur()) + ";");
    }
}

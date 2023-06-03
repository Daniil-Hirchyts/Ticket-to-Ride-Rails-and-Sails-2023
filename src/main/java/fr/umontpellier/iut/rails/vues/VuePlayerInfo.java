package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class VuePlayerInfo extends HBox {

    IJoueur joueur;
    @FXML
    private Pane playerBackground;
    @FXML
    private Pane playerBackgroundAvatarColor;
    @FXML
    private ImageView playerBackgroundAvatarImage;
    @FXML
    private Label playerLabelScore;
    @FXML
    private HBox playerLabelScoreImage;
    @FXML
    private Label playerNameLabel;
    @FXML
    private HBox flagsBox;
    public final String PNG = ".png";
    private ImageView avatar;

    public VuePlayerInfo(IJoueur joueur, ImageView avatar) {
        this.joueur = joueur;
        this.avatar = avatar;
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
        playerBackground.setStyle("-fx-background-image: url('/images/elements/player-interface/background/" + joueur.getCouleur() + PNG + "');");
        playerBackgroundAvatarColor.setStyle("-fx-background-image: url('/images/elements/player-interface/avatar/back-color/" + joueur.getCouleur() + PNG + "');");
        playerNameLabel.textProperty().set(joueur.getNom());
        playerLabelScore.textProperty().set(String.valueOf(joueur.getScore()));
        playerLabelScoreImage.setStyle("-fx-background-image: url('/images/elements/player-interface/label/" + joueur.getCouleur() + PNG + "');");
        playerBackgroundAvatarImage.setImage(avatar.getImage());
        for (int i = 0; i < 3; i++) {
            VueFlag vueFlag = new VueFlag(joueur);
            vueFlag.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
            flagsBox.getChildren().add(vueFlag);
        }
    }
}

package fr.umontpellier.iut.rails.vues.jeu;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class VuePlayerInfo extends HBox {

    private final IJoueur joueur;
    public final String PNG = ".png";
    private final ImageView avatar;
    private final IntegerProperty wagons = new SimpleIntegerProperty();
    private final IntegerProperty bateaux = new SimpleIntegerProperty();
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

    public VuePlayerInfo(IJoueur joueur, ImageView avatar) {
        this.joueur = joueur;
        this.avatar = avatar;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VuePlayerInfo.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        creerBindings();
        updateInformation();
        getStyleClass().add("big-shadow");
    }

    private void updateInformation() {
        int ports = joueur.getNbPorts();

        playerBackground.setStyle("-fx-background-image: url('/images/elements/player-interface/background/" + joueur.getCouleur() + PNG + "');");
        playerBackgroundAvatarColor.setStyle("-fx-background-image: url('/images/elements/player-interface/avatar/back-color/" + joueur.getCouleur() + PNG + "');");

        playerNameLabel.textProperty().set(joueur.getNom());
        playerLabelScore.textProperty().set(String.valueOf(joueur.getScore()));

        playerNameLabel.getStyleClass().add("labelsIMFellEnglish");
        playerLabelScore.getStyleClass().add("labelsIMFellEnglish");

        playerBackgroundAvatarImage.setImage(avatar.getImage());

        VueFlag pionsWagon = new VueFlag(joueur, wagons, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/train.png"))), "wagons");
        VueFlag pionsBateau = new VueFlag(joueur, bateaux, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ship.png"))), "bateaux");
        VueFlag pionsPort = new VueFlag(joueur, ports, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ancor.png"))), "ports");

        playerLabelScoreImage.setStyle("-fx-background-image: url('/images/elements/player-interface/label/" + joueur.getCouleur() + PNG + "');");
        pionsWagon.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
        pionsBateau.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
        pionsPort.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");

        flagsBox.getChildren().addAll(pionsWagon, pionsBateau, pionsPort);
    }

    public void creerBindings() {
        wagons.bind(joueur.nbPionsWagonsProperty());
        bateaux.bind(joueur.nbPionsBateauxProperty());
    }
}

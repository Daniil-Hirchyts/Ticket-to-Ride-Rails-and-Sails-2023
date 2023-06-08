package fr.umontpellier.iut.rails.vues.jeu;

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

import java.io.IOException;

import static java.lang.String.valueOf;
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
    private IntegerProperty wagons = new SimpleIntegerProperty();
    private IntegerProperty bateaux = new SimpleIntegerProperty();
    private int ports;

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
        //add to css class to change the style
        getStyleClass().add("big-shadow");
    }

    public void creerBindings() {
        wagons.bind(joueur.nbPionsWagonsProperty());
        bateaux.bind(joueur.nbPionsBateauxProperty());
        ports = joueur.getNbPorts();
        playerBackground.setStyle("-fx-background-image: url('/images/elements/player-interface/background/" + joueur.getCouleur() + PNG + "');");
        playerBackgroundAvatarColor.setStyle("-fx-background-image: url('/images/elements/player-interface/avatar/back-color/" + joueur.getCouleur() + PNG + "');");
        playerNameLabel.textProperty().set(joueur.getNom());
        playerNameLabel.getStyleClass().add("labelsIMFellEnglish");
        playerLabelScore.textProperty().set(String.valueOf(joueur.getScore()));
        playerLabelScore.getStyleClass().add("labelsIMFellEnglish");
        playerLabelScoreImage.setStyle("-fx-background-image: url('/images/elements/player-interface/label/" + joueur.getCouleur() + PNG + "');");
        playerBackgroundAvatarImage.setImage(avatar.getImage());
        VueFlag pionsWagon = new VueFlag(joueur, wagons, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/train.png"))), "wagons");
        VueFlag pionsBateau = new VueFlag(joueur, bateaux, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ship.png"))), "bateaux");
        VueFlag pionsPort = new VueFlag(joueur, ports, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ancor.png"))), "ports");
        pionsWagon.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
        pionsBateau.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
        pionsPort.setStyle("-fx-scale-x: 0.7; -fx-scale-y: 0.7;");
        flagsBox.getChildren().addAll(pionsWagon, pionsBateau, pionsPort);
    }
}

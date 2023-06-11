package fr.umontpellier.iut.rails.vues.fin;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class VueLoser extends Pane {
    @FXML
    private ImageView backgroundMedal;
    @FXML
    private ImageView backgroundColorRondelle;
    @FXML
    private ImageView playerAvatarLoserColor;
    @FXML
    private Label labelRank;
    @FXML
    private Label labelPseudo;
    @FXML
    private Label labelScore;

    public VueLoser(IJoueur joueur, int rank, ImageView Avatar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fin/VueLoser.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        labelScore.setText(String.valueOf(joueur.getScore()));
        labelPseudo.setText(joueur.getNom());
        labelRank.setText(String.valueOf(rank));

        playerAvatarLoserColor.setImage(Avatar.getImage());
        backgroundColorRondelle.setImage(new Image("images/elements/player-interface/avatar/back-color/" + joueur.getCouleur() + ".png"));
        backgroundMedal.setImage(new Image("images/resultatPage/loser/" + joueur.getCouleur() + ".png"));
    }

}

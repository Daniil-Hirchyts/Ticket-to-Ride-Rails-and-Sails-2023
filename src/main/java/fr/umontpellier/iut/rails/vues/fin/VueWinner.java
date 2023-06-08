package fr.umontpellier.iut.rails.vues.fin;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class VueWinner extends Pane {

    private IJoueur joueur;

    private ImageView Avatar;

    private int nbDestination;

    @FXML
    private ImageView backgroundWinner;
    @FXML
    private ImageView playerWinnerAvatarColor;
    @FXML
    private Label labelScore;
    @FXML
    private Label playerName;
    @FXML
    private Label labelNbDestination;
    public VueWinner(IJoueur joueur, int rank, ImageView Avatar) {
        this.joueur = joueur;
        this.nbDestination = nbDestination;
        this.Avatar = Avatar;
        System.out.println(Avatar);
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fin/VueWinner.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        creerBindings();
    }

    private void creerBindings() {
        labelScore.setText(String.valueOf(joueur.getScore()));
        playerName.setText(joueur.getNom());
        playerName.setStyle("--fx-font-size: 30px;");
        labelScore.setStyle("--fx-font-size: 25px;");
        labelNbDestination.setStyle("--fx-font-size: 25px;");
        labelNbDestination.setText(String.valueOf(nbDestination));
        playerWinnerAvatarColor.setImage(Avatar.getImage());
        backgroundWinner.setImage(new Image("images/resultatPage/winner/"+joueur.getCouleur()+".png"));
    }
}

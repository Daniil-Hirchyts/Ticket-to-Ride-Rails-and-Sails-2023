package fr.umontpellier.iut.rails.vues.fin;

import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.RailsIHM;
import fr.umontpellier.iut.rails.mecanique.Joueur;
import fr.umontpellier.iut.rails.mecanique.data.Destination;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;

/**
 * Cette classe affiche les scores en fin de partie.
 * On peut éventuellement proposer de rejouer, et donc de revenir à la fenêtre principale
 *
 */
public class VueResultats extends Pane {
    @FXML
    private GridPane containerPlayer;
    @FXML
    private HBox loserHBox;
    private static final double ASPECT_RATIO = 16.0/9.0;
    private Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs;
    private RailsIHM ihm;
    private ImageView avatarGagnant;
    private ImageView lumiereGagnant;

    public VueResultats(RailsIHM ihm, Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs) {
        this.ihm = ihm;
        this.avatarsJoueurs = avatarsJoueurs;
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fin/VueResultats.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        creerBindings();
    }

    private void creerBindings() {
        int nbDestinationsWinners = 0;
        List<Joueur> joueurs = ihm.getJeu().getJoueurs();
        joueurs.sort((j1, j2) -> j2.getScore() - j1.getScore());
        for (Destination d : joueurs.get(0).getDestinations())
            if (joueurs.get(0).destinationEstComplete(d)) nbDestinationsWinners++;
        VueWinner winner = new VueWinner(joueurs.get(0), 1, avatarsJoueurs.get(joueurs.get(0).getCouleur()));
        containerPlayer.add(winner, 0, 0);
        for (int i = 1; i < joueurs.size(); i++) {
            VueLoser loser = new VueLoser(joueurs.get(i), i+1, avatarsJoueurs.get(joueurs.get(i).getCouleur()));
            loserHBox.getChildren().add(loser);
        }
        containerPlayer.layoutXProperty().bind(this.widthProperty().divide(5.8).multiply(this.widthProperty().divide(1280)));
        containerPlayer.layoutYProperty().bind(this.heightProperty().divide(2).subtract(containerPlayer.heightProperty().divide(2)));
        String nomPng = avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().substring(75, avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().length()-4).substring(avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().substring(75, avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().length()-4).length()-8);
        avatarGagnant = new ImageView(new Image("images/resultatPage/character/"+nomPng+".png"));
        avatarGagnant.fitHeightProperty().bind(this.heightProperty().divide(this.heightProperty().divide(1300).multiply(2)));
        avatarGagnant.fitWidthProperty().bind(this.widthProperty().divide(this.widthProperty().divide(850).multiply(2)));
        avatarGagnant.layoutXProperty().bind(this.widthProperty().subtract(avatarGagnant.fitWidthProperty()));
        avatarGagnant.layoutYProperty().bind(this.heightProperty().subtract(avatarGagnant.fitHeightProperty()));
        lumiereGagnant = new ImageView(new Image("images/resultatPage/halo.png"));
        lumiereGagnant.fitHeightProperty().bind(this.heightProperty().divide(1.2));
        lumiereGagnant.fitWidthProperty().bind(this.widthProperty().divide(3));
        lumiereGagnant.layoutXProperty().bind(this.widthProperty().subtract(avatarGagnant.fitWidthProperty()));
        lumiereGagnant.layoutYProperty().bind(this.heightProperty().divide(12));
        containerPlayer.scaleXProperty().bind(this.widthProperty().divide(1300));
        containerPlayer.scaleYProperty().bind(this.heightProperty().divide(850));


        RotateTransition rt = new RotateTransition();
        rt.setNode(lumiereGagnant);
        rt.setByAngle(360);
        rt.setCycleCount(1000);
        rt.setDuration(javafx.util.Duration.millis(10000));
        rt.play();

        this.getChildren().addAll(lumiereGagnant, avatarGagnant);
    }

}

package fr.umontpellier.iut.rails.vues.fin;

import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.RailsIHM;
import fr.umontpellier.iut.rails.mecanique.Joueur;
import fr.umontpellier.iut.rails.mecanique.data.Destination;
import fr.umontpellier.iut.rails.vues.debut.VueChoixJoueurs;
import fr.umontpellier.iut.rails.vues.layout.SoundEffect;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Cette classe affiche les scores en fin de partie.
 * On peut éventuellement proposer de rejouer, et donc de revenir à la fenêtre principale
 */
public class VueResultats extends Pane {
    private final Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs;
    private final RailsIHM ihm;
    private final SoundEffect soundEffect;
    @FXML
    private GridPane containerPlayer;
    @FXML
    private HBox loserHBox;
    @FXML
    private Button btnExit;

    public VueResultats(RailsIHM ihm, Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs) {
        this.ihm = ihm;
        this.avatarsJoueurs = avatarsJoueurs;
        soundEffect = new SoundEffect();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fin/VueResultats.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        soundEffect.getMenuMusic().stop();
        soundEffect.getWinnerSound().play();
        soundEffect.getWinnerSound().setVolume(0.5);
        creerBindings();
        quitterButton();
    }

    private void creerBindings() {

        int nbDestinationsWinners = 0;
        List<Joueur> joueurs = ihm.getJeu().getJoueurs();

        joueurs.sort((j1, j2) -> j2.getScore() - j1.getScore());

        for (Destination d : joueurs.get(0).destinationsProperty())
            if (joueurs.get(0).destinationEstComplete(d)) nbDestinationsWinners++;

        VueWinner winner = new VueWinner(joueurs.get(0), avatarsJoueurs.get(joueurs.get(0).getCouleur()), nbDestinationsWinners);

        containerPlayer.layoutXProperty().bind(this.widthProperty().divide(5.8).multiply(this.widthProperty().divide(1280)));
        containerPlayer.layoutYProperty().bind(this.heightProperty().divide(2).subtract(containerPlayer.heightProperty().divide(2)));
        containerPlayer.scaleXProperty().bind(this.widthProperty().divide(1300));
        containerPlayer.scaleYProperty().bind(this.heightProperty().divide(850));
        containerPlayer.add(winner, 0, 0);

        for (int i = 1; i < joueurs.size(); i++) {
            VueLoser loser = new VueLoser(joueurs.get(i), i + 1, avatarsJoueurs.get(joueurs.get(i).getCouleur()));
            loserHBox.getChildren().add(loser);
        }

        String nomPng = avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().substring(75, avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().length() - 4).substring(avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().substring(75, avatarsJoueurs.get(joueurs.get(0).getCouleur()).getImage().getUrl().length() - 4).length() - 8);

        ImageView avatarGagnant = new ImageView(new Image("images/resultatPage/character/" + nomPng + ".png"));
        avatarGagnant.fitHeightProperty().bind(this.heightProperty().divide(this.heightProperty().divide(1300).multiply(2)));
        avatarGagnant.fitWidthProperty().bind(this.widthProperty().divide(this.widthProperty().divide(850).multiply(2)));
        avatarGagnant.layoutXProperty().bind(this.widthProperty().subtract(avatarGagnant.fitWidthProperty()));
        avatarGagnant.layoutYProperty().bind(this.heightProperty().subtract(avatarGagnant.fitHeightProperty()));

        ImageView lumiereGagnant = new ImageView(new Image("images/resultatPage/halo.png"));
        lumiereGagnant.fitHeightProperty().bind(this.heightProperty().divide(1.2));
        lumiereGagnant.fitWidthProperty().bind(this.widthProperty().divide(3));
        lumiereGagnant.layoutXProperty().bind(this.widthProperty().subtract(avatarGagnant.fitWidthProperty()));
        lumiereGagnant.layoutYProperty().bind(this.heightProperty().divide(12));

        RotateTransition rt = new RotateTransition();
        rt.setNode(lumiereGagnant);
        rt.setByAngle(360);
        rt.setCycleCount(1000);
        rt.setDuration(javafx.util.Duration.millis(10000));
        rt.play();

        getChildren().addAll(lumiereGagnant, avatarGagnant);
    }

    private void quitterButton() {
        btnExit.setOnMouseClicked(mouseEvent -> {
            soundEffect.getButtonSoundEffect().play();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quitter");
            alert.setHeaderText("Voulez-vous vraiment retourner au menu principal ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                soundEffect.getWinnerSound().stop();
                soundEffect.getButtonSoundEffect().stop();
                soundEffect.getButtonSoundEffect().play();
                backToMenu();
            } else {
                soundEffect.getButtonSoundEffect().play();
            }
        });
    }

    private void backToMenu() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
        RailsIHM railsIHM = new RailsIHM();
        railsIHM.start(new VueChoixJoueurs());
    }

}

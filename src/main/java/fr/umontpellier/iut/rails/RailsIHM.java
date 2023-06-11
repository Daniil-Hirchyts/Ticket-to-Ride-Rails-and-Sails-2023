package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.umontpellier.iut.rails.mecanique.Jeu;
import fr.umontpellier.iut.rails.vues.debut.VueChoixJoueurs;
import fr.umontpellier.iut.rails.vues.fin.VueResultats;
import fr.umontpellier.iut.rails.vues.jeu.VueDuJeu;
import fr.umontpellier.iut.rails.vues.layout.DonneesGraphiques;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class RailsIHM extends Application {

    private VueChoixJoueurs vueChoixJoueurs;
    private Stage primaryStage;
    private Jeu jeu;
    private BooleanProperty partieTerminee;
    private final boolean avecVueChoixJoueurs = true;
    private Map<IJoueur.CouleurJoueur, ImageView> avatars;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        debuterJeu();
    }

    public void debuterJeu() {
        if (avecVueChoixJoueurs) {
            vueChoixJoueurs = new VueChoixJoueurs();
            vueChoixJoueurs.setNomsDesJoueursDefinisListener(quandLesNomsJoueursSontDefinis);
            vueChoixJoueurs.show();
        } else demarrerPartie();
    }

    public void demarrerPartie() {
        List<String> nomsJoueurs;
        if (avecVueChoixJoueurs) nomsJoueurs = vueChoixJoueurs.getNomsJoueurs();
        else {
            nomsJoueurs = new ArrayList<>();
            nomsJoueurs.add("Guybrush");
            nomsJoueurs.add("Largo");
            nomsJoueurs.add("LeChuck");
            nomsJoueurs.add("Elaine");
        }

        jeu = new Jeu(nomsJoueurs.toArray(new String[0]));

        if (avecVueChoixJoueurs) avatars = vueChoixJoueurs.getAvatars();
        else {
            avatars = FXCollections.observableHashMap();
            int i = 1;
            for (IJoueur.CouleurJoueur couleur : IJoueur.CouleurJoueur.values())
                avatars.put(couleur, new ImageView(new Image("images/avatars/" + couleur.name() + "/avatar-" + (i++) + ".png")));
        }

        VueDuJeu vueDuJeu = new VueDuJeu(jeu, avatars);
        Scene scene = new Scene(vueDuJeu);
        vueDuJeu.creerBindings();
        jeu.run();
        primaryStage.setTitle("Partie | Les Aventuriers du Rails");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            this.arreterJeu();
            event.consume();
        });

        finPartieListener();


        primaryStage.show();
    }

    private void finPartieListener() {
        jeu.finDePartieProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                VueResultats vueResultats = new VueResultats(this, avatars);
                Scene sceneResultats = new Scene(vueResultats, Screen.getPrimary().getBounds().getWidth() * DonneesGraphiques.pourcentageEcran, Screen.getPrimary().getBounds().getHeight() * DonneesGraphiques.pourcentageEcran);
                primaryStage.setScene(sceneResultats);
            }
        });
    }

    private final ListChangeListener<String> quandLesNomsJoueursSontDefinis = change -> {
        if (!vueChoixJoueurs.getNomsJoueurs().isEmpty()) demarrerPartie();
    };

    public void arreterJeu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("On arrête de jouer ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) Platform.exit();
    }

    public Jeu getJeu() {
        return jeu;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
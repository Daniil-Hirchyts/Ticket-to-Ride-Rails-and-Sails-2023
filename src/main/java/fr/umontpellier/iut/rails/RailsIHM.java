package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.mecanique.Graphe;
import fr.umontpellier.iut.rails.mecanique.Jeu;
import fr.umontpellier.iut.rails.vues.debut.VueChoixJoueurs;
import fr.umontpellier.iut.rails.vues.fin.VueResultats;
import fr.umontpellier.iut.rails.vues.jeu.VueDuJeu;
import fr.umontpellier.iut.rails.vues.layout.DonneesGraphiques;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RailsIHM extends Application {

    private static final double ASPECT_RATIO = 16.0 / 9.0;
    private Map<IJoueur.CouleurJoueur, ImageView> avatars;
    private VueChoixJoueurs vueChoixJoueurs;
    private Stage primaryStage;
    private Jeu jeu;
    private BooleanProperty partieTerminee;
    private final boolean avecVueChoixJoueurs = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        debuterJeu();
    }

    public void debuterJeu() {

//        ChangeListener<Number> dimensionListener = (observable, oldValue, newValue) -> {
//            if (observable == primaryStage.widthProperty()) {
//                primaryStage.setHeight(primaryStage.getWidth() / ASPECT_RATIO);
//            } else if (observable == primaryStage.heightProperty()) {
//                primaryStage.setWidth(primaryStage.getHeight() * ASPECT_RATIO);
//            }
//        };
//
//        primaryStage.widthProperty().addListener(dimensionListener);
//        primaryStage.heightProperty().addListener(dimensionListener);



        if (avecVueChoixJoueurs) {
            vueChoixJoueurs = new VueChoixJoueurs();
            vueChoixJoueurs.setNomsDesJoueursDefinisListener(quandLesNomsJoueursSontDefinis);
//            vueChoixJoueurs.setFullScreen(true);
            vueChoixJoueurs.show();
        } else {
            demarrerPartie();
        }
    }

    public void demarrerPartie() {
        List<String> nomsJoueurs;
        if (avecVueChoixJoueurs)
            nomsJoueurs = vueChoixJoueurs.getNomsJoueurs();
        else {
            nomsJoueurs = new ArrayList<>();
            nomsJoueurs.add("Guybrush");
            nomsJoueurs.add("Largo");
            nomsJoueurs.add("LeChuck");
            nomsJoueurs.add("Elaine");
            //pour chacun des joueurs on ajoute son avatar dans la liste des avatars
        }
        jeu = new Jeu(nomsJoueurs.toArray(new String[0]));
        if (avecVueChoixJoueurs) avatars = vueChoixJoueurs.getAvatars();
        else {
            avatars = FXCollections.observableHashMap();
            int i = 1;
            for (IJoueur.CouleurJoueur couleur : IJoueur.CouleurJoueur.values()) {
                avatars.put(couleur, new ImageView(new Image("images/avatars/" + couleur.name() + "/avatar-" + (i++) + ".png")));
            }
        }
        VueDuJeu vueDuJeu = new VueDuJeu(jeu, avatars);
        Scene scene = new Scene(vueDuJeu, Screen.getPrimary().getBounds().getWidth() * DonneesGraphiques.pourcentageEcran, Screen.getPrimary().getBounds().getHeight() * DonneesGraphiques.pourcentageEcran); // la scene doit être créée avant de mettre en place les bindings
        vueDuJeu.creerBindings();
        jeu.run();
        primaryStage.setMinWidth(Screen.getPrimary().getBounds().getWidth() / 2);
        primaryStage.setMinHeight(Screen.getPrimary().getBounds().getHeight() / 2);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rails");
        primaryStage.centerOnScreen();
        primaryStage.setMaxWidth(Screen.getPrimary().getBounds().getWidth());
        primaryStage.setMaxHeight(Screen.getPrimary().getBounds().getHeight());
        Graphe graphe = new Graphe(jeu.getRoutesLibres());


        primaryStage.setScene(scene);
        VueResultats vueResultats = new VueResultats(this, avatars);
        Scene sceneResultats = new Scene(vueResultats, Screen.getPrimary().getBounds().getWidth() * DonneesGraphiques.pourcentageEcran, Screen.getPrimary().getBounds().getHeight() * DonneesGraphiques.pourcentageEcran);
//        primaryStage.setScene(sceneResultats);
//        TODO : A ACTIVER POUR LA VERSION FINALE
//        primaryStage.setOnCloseRequest(event -> {
//            this.arreterJeu();
//            event.consume();
//        });
//
        jeu.finDePartieProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                VueResultats vueResultats = new VueResultats(this, avatars);
                Scene sceneResultats = new Scene(vueResultats, Screen.getPrimary().getBounds().getWidth() * DonneesGraphiques.pourcentageEcran, Screen.getPrimary().getBounds().getHeight() * DonneesGraphiques.pourcentageEcran);
                primaryStage.setScene(sceneResultats);
            }
        });

        primaryStage.show();
    }

    private final ListChangeListener<String> quandLesNomsJoueursSontDefinis = change -> {
        if (!vueChoixJoueurs.getNomsJoueurs().isEmpty())
            demarrerPartie();
    };

    public void arreterJeu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("On arrête de jouer ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public Jeu getJeu() {
        return jeu;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
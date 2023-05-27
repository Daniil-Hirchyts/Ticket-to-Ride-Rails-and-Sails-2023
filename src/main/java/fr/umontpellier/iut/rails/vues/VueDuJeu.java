package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJeu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Cette classe correspond à la fenêtre principale de l'application.
 * <p>
 * Elle est initialisée avec une référence sur la partie en cours (Jeu).
 * <p>
 * On y définit les bindings sur les éléments internes qui peuvent changer
 * (le joueur courant, les cartes Transport visibles, les destinations lors de l'étape d'initialisation de la partie, ...)
 * ainsi que les listeners à exécuter lorsque ces éléments changent
 */
public class VueDuJeu extends GridPane {

    private final IJeu jeu;
    private VuePlateau plateau;
    @FXML
    private ImageView pileCard;
    @FXML
    private VBox rightVBox;
    @FXML
    private HBox bottomHBox;
    @FXML
    private ScrollPane consoleScrollPane;
    @FXML
    private VBox consoleHistory;

    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/gameScene.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plateau = new VuePlateau();
        setRowIndex(plateau, 1);
        setColumnIndex(plateau, 1);

        VueJoueurCourant vueJoueurCourant = new VueJoueurCourant(jeu);
        setColumnSpan(vueJoueurCourant, 3);
        setRowIndex(vueJoueurCourant, 2);
        setColumnIndex(vueJoueurCourant, 0);
        vueJoueurCourant.creerBindings();

        getChildren().addAll(vueJoueurCourant, plateau);

        ConsoleLogger.getInstance(consoleHistory, consoleScrollPane);
    }

    public void creerBindings() {
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        plateau.creerBindings();
        jeu.instructionProperty().addListener((observable, oldValue, newValue) -> {
            ConsoleLogger.getInstance(null, null).log(newValue);
        });
    }

    public IJeu getJeu() {
        return jeu;
    }
}

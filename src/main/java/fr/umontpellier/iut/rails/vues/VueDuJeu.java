package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.mecanique.data.Destination;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
    private ImageView destinationCard;
    @FXML
    private VBox centerBox;
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
        destinationCard.setImage(new Image("images/cartesWagons/destinations.png"));
        VueJoueurCourant vueJoueurCourant = new VueJoueurCourant(jeu);
        centerBox.getChildren().add(plateau);
        vueJoueurCourant.creerBindings();
        bottomHBox.getChildren().add(vueJoueurCourant);
    }

    public void creerBindings() {
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        plateau.creerBindings();
        jeu.instructionProperty().addListener((observable, oldValue, newValue) -> {
            Label label = new Label(newValue);
            consoleHistory.getChildren().addAll(label);
            consoleScrollPane.setVvalue(1);
        });
    }

    public IJeu getJeu() {
        return jeu;
    }

}

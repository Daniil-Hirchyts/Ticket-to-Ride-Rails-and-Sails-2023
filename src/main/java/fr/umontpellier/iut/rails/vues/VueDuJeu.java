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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
    private Label ActionJoueur;
    private Button btnPasserTour;
    private VuePlateau plateau;
    @FXML
    private VBox centerBox;
    private VBox VboxDestinations;
    @FXML
    private VBox rightVBox;
    @FXML
    private VBox playerVBox;

    private VueJoueurCourant vueJoueurCourant;
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
        VboxDestinations = new VBox();
        btnPasserTour = new Button("Passer");
        ActionJoueur = new Label();
        plateau = new VuePlateau();
        centerBox.getChildren().add(plateau);
        rightVBox.getChildren().addAll(ActionJoueur,btnPasserTour,VboxDestinations);
        VueJoueurCourant vueJoueurCourant = new VueJoueurCourant(jeu);
        vueJoueurCourant.creerBindings();
        playerVBox.getChildren().add(vueJoueurCourant);
        //
    }

    public void creerBindings() {
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        plateau.creerBindings();
        jeu.destinationsInitialesProperty().addListener(new ListChangeListener<IDestination>() {
            @Override
            public void onChanged(Change<? extends IDestination> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (IDestination destination : change.getAddedSubList()) {
                            System.out.println("On a ajouté la destination " + destination.getVilles().toString());
                            VboxDestinations.getChildren().add(new Label(destination.getVilles().toString()));
                        }
                    }
                    if (change.wasRemoved()){
                        for (IDestination destination : change.getRemoved()) {
                            System.out.println("On a enlevé la destination " + destination.getVilles().toString());
                            VboxDestinations.getChildren().remove(trouveLabelIDestination(destination));
                        }
                    }
                }
            }
        });
        btnPasserTour.setOnMouseClicked(actionPasserParDefaut);
        ActionJoueur.textProperty().bind(jeu.instructionProperty());
    }

    public Label trouveLabelIDestination(IDestination destination) {
        for (Node node : VboxDestinations.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(destination.getVilles().toString())) {
                    return label;
                }
            }
        }
        return null;
    }

    public IJeu getJeu() {
        return jeu;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        System.out.println("On a cliqué sur le bouton passer");
        getJeu().passerAEteChoisi();
    });

}

package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 *
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends GridPane {
    IJeu jeu;
    VBox VboxJoueurCourant;
    Label nomJoueur;

    List<ICarteTransport> cartesTransport;

    @FXML
    private HBox playerHBox;

    public VueJoueurCourant(IJeu jeu) {
        this.jeu = jeu;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/gameScene.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        VboxJoueurCourant = new VBox();
        nomJoueur = new Label();
        cartesTransport = new ArrayList<>();
        VboxJoueurCourant.getChildren().add(nomJoueur);
        playerHBox.getChildren().add(new Label("Cartes Transport :"));
    }

    public void creerBindings() {
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                nomJoueur.setText(newValue.getNom());
            }
        });
        for (IJoueur j : jeu.getJoueurs()) {
            j.cartesTransportProperty().addListener(new ListChangeListener<ICarteTransport>() {
                @Override
                public void onChanged(Change<? extends ICarteTransport> change) {
                    cartesTransport.clear();
                    cartesTransport.addAll(j.getCartesTransport());
                    for (ICarteTransport c : cartesTransport) {
                        playerHBox.getChildren().add(new VueCarteTransport(c,1));
                        playerHBox.getChildren().add(new Label(c.toString()));
                    }
                }
            });
        }
    }

}

package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Cette classe présente les éléments des joueurs autres que le joueur courant,
 * en cachant ceux que le joueur courant n'a pas à connaitre.
 * <p>
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueAutresJoueurs extends GridPane {
    IJeu jeu;

    public VueAutresJoueurs(IJeu jeu) {
        this.jeu = jeu;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playersPanel.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void creerBindings() {
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            updatePlayerViews(newValue);
        });
    }

    public void updatePlayerViews(IJoueur joueur) {
        getChildren().clear();
        getColumnConstraints().clear();
        for (int i = 0; i < jeu.getJoueurs().size(); i++) getColumnConstraints().add(new ColumnConstraints());
        for (int i = 0; i < jeu.getJoueurs().size(); i++) {
            if (jeu.getJoueurs().get(i) == joueur) continue;
            VuePlayerInfo vuePlayerInfo = new VuePlayerInfo(jeu.getJoueurs().get(i));
            vuePlayerInfo.creerBindings();
            add(vuePlayerInfo, i, 0);
            getColumnConstraints().get(i).prefWidthProperty().bind(widthProperty().divide(jeu.getJoueurs().size() - 1));
        }
    }


}

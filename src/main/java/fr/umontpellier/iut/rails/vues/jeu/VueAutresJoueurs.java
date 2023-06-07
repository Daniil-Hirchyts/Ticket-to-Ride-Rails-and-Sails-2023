package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.util.Map;

/**
 * Cette classe présente les éléments des joueurs autres que le joueur courant,
 * en cachant ceux que le joueur courant n'a pas à connaitre.
 * <p>
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueAutresJoueurs extends HBox {
    IJeu jeu;
    private Map<IJoueur.CouleurJoueur, ImageView> avatarList;

    public VueAutresJoueurs(IJeu jeu, Map<IJoueur.CouleurJoueur, ImageView> avatarList) {
        this.jeu = jeu;
        this.avatarList = avatarList;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/VueAutresJoueurs.fxml"));
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
        int nnJoueurs = jeu.getJoueurs().size();
        for (int i = 0; i < nnJoueurs; i++) {
            if (jeu.getJoueurs().get(i) == joueur) continue;
            VuePlayerInfo vuePlayerInfo = new VuePlayerInfo(jeu.getJoueurs().get(i), avatarList.get(jeu.getJoueurs().get(i).getCouleur()));
            vuePlayerInfo.creerBindings();
            if (nnJoueurs > 3) HBox.setHgrow(vuePlayerInfo, Priority.ALWAYS);
            getChildren().add(vuePlayerInfo);
        }
    }

}

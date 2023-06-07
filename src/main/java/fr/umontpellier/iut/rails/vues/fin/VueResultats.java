package fr.umontpellier.iut.rails.vues.fin;

import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.RailsIHM;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.Map;

/**
 * Cette classe affiche les scores en fin de partie.
 * On peut éventuellement proposer de rejouer, et donc de revenir à la fenêtre principale
 *
 */
public class VueResultats extends Pane {

    private static final double ASPECT_RATIO = 16.0/9.0;
    private Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs;
    private RailsIHM ihm;

    public VueResultats(RailsIHM ihm, Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs) {
        this.ihm = ihm;
        this.avatarsJoueurs = avatarsJoueurs;
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resultatPage.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

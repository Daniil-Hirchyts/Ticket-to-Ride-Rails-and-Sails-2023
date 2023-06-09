package fr.umontpellier.iut.rails.vues.debut;

import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.vues.layout.SoundEffect;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cette classe correspond à une nouvelle fenêtre permettant de choisir le nombre et les noms des joueurs de la partie.
 * <p>
 * Sa présentation graphique peut automatiquement être actualisée chaque fois que le nombre de joueurs change.
 * Lorsque l'utilisateur a fini de saisir les noms de joueurs, il demandera à démarrer la partie.
 */
public class VueChoixJoueurs extends Stage {

    private static final double ASPECT_RATIO = 16.0/9.0;
    private final ObservableList<String> nomsJoueurs;
    private Map<IJoueur.CouleurJoueur, ImageView> avatarsJoueurs;
    private List<Integer> compteursCouleurs = new ArrayList<>(5);
    private IntegerProperty nbJoueursProperty = new SimpleIntegerProperty(5);
    private ObservableList<TextField> textFields;
    private VueChoixJoueursContainer root;

    private SoundEffect soundEffect = new SoundEffect();

    public VueChoixJoueurs() {
        avatarsJoueurs = FXCollections.observableHashMap();
        nomsJoueurs = FXCollections.observableArrayList();
        textFields = FXCollections.observableArrayList();
        root = new VueChoixJoueursContainer();
        genererChampsJoueurs(nbJoueursProperty.get());
        setScene(new Scene(root, 1280, 800));
//        setFullScreen(true);
        setTitle("Choix des joueurs");
        creerBinding();
        show();
    }

    private void creerBinding() {
        ChangeListener<Number> dimensionListener = (observable, oldValue, newValue) -> {
            if (observable == this.widthProperty()) setHeight(this.getWidth() / ASPECT_RATIO);
            else if (observable == this.heightProperty()) setWidth(this.getHeight() * ASPECT_RATIO);
        };
        this.widthProperty().addListener(dimensionListener);
        this.heightProperty().addListener(dimensionListener);
        root.getValidation().setOnAction(event -> {
            nomsJoueurs.clear();
            setListeDesNomsDeJoueurs();
        });

        root.getVboxJoueurs().scaleXProperty().bind(this.widthProperty().divide(1600));
        root.getVboxJoueurs().scaleYProperty().bind(this.heightProperty().divide(900));
    }

    protected void genererChampsJoueurs(int nbJoueurs) {
        for (int i = 0; i < nbJoueurs; i++) {
            int finalI = i;
            IJoueur.CouleurJoueur couleur = IJoueur.CouleurJoueur.values()[i];

            String PNG = ".png";
            int position = i + 1;
            compteursCouleurs.add(i, position);

            VueImageChoix avatar = new VueImageChoix();
            avatar.changeBackgroundImage("/images/elements/player-choose/background/" + couleur + PNG);
            avatar.getAvatar().setImage(new Image("images/avatars/" + couleur + "/avatar-" + position + PNG));
            avatar.getBackgroundColorAvatar().setImage(new Image("images/elements/player-interface/avatar/back-color/" + couleur + PNG));
            avatar.getTextFieldJoueur().setId("playerChoiceTextField");

            avatar.setOnMouseClicked(event -> {
                soundEffect.getButtonSoundEffect().play();
                if (compteursCouleurs.get(finalI) == 5) compteursCouleurs.set(finalI, 1);
                else compteursCouleurs.set(finalI, compteursCouleurs.get(finalI) + 1);
                avatar.getAvatar().setImage(new Image("images/avatars/" + couleur + "/avatar-" + compteursCouleurs.get(finalI) + ".png"));
            });

            textFields.add(avatar.getTextFieldJoueur());
            avatarsJoueurs.put(couleur, avatar.getAvatar());
            root.getVboxJoueurs().getChildren().add(avatar);
        }
    }

    public List<String> getNomsJoueurs() {
        return nomsJoueurs;
    }

    /**
     * Définit l'action à exécuter lorsque la liste des participants est correctement initialisée
     */
    public void setNomsDesJoueursDefinisListener(ListChangeListener<String> quandLesNomsDesJoueursSontDefinis) {
        nomsJoueurs.addListener(quandLesNomsDesJoueursSontDefinis);
    }

    /**
     * Vérifie que tous les noms des participants sont renseignés
     * et affecte la liste définitive des participants
     */
    protected void setListeDesNomsDeJoueurs() {
        ArrayList<String> tempNamesList = new ArrayList<>();
        for (int i = 1; i <= getNombreDeJoueurs(); i++) {
            String name = getJoueurParNumero(i);
            if (name == null || name.equals("")) {
                tempNamesList.clear();
                break;
            } else
                tempNamesList.add(name);
        }
        if (!tempNamesList.isEmpty()) {
            hide();
            nomsJoueurs.clear();
            nomsJoueurs.addAll(tempNamesList);
        }
    }

    /**
     * Retourne le nombre de participants à la partie que l'utilisateur a renseigné
     */
    protected int getNombreDeJoueurs() {
        int nbJoueurs = 0;
        for (TextField textField : textFields) {
            if (!textField.getText().equals("")) nbJoueurs++;
        }
        return nbJoueurs;
    }

    /**
     * Retourne le nom que l'utilisateur a renseigné pour le ième participant à la partie
     *
     * @param playerNumber : le numéro du participant
     */
    protected String getJoueurParNumero(int playerNumber) {
        return textFields.get(playerNumber - 1).getText();
    }

    public Map<IJoueur.CouleurJoueur, ImageView> getAvatars() {
        return avatarsJoueurs;
    }

}

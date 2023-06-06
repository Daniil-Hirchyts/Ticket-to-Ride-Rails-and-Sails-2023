package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
    private int nbJoueurs;
    private List<Integer> compteursCouleurs;
    private VBox vboxJoueurs;
    private Button validation;
    private IntegerProperty nbJoueursProperty;
    private ObservableList<TextField> textFields;

    public ObservableList<String> nomsJoueursProperty() {
        return nomsJoueurs;
    }

    public VueChoixJoueurs() {
        nbJoueursProperty = new SimpleIntegerProperty(5);
        avatarsJoueurs = FXCollections.observableHashMap();
        nomsJoueurs = FXCollections.observableArrayList();
        textFields = FXCollections.observableArrayList();
        compteursCouleurs = new ArrayList<>(5);
        VueChoixJoueursContainer root = new VueChoixJoueursContainer();
        vboxJoueurs = new VBox();
        validation = new Button("Valider");
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        creerBinding();
        genererChampsJoueurs(nbJoueursProperty.get());
        setScene(new Scene(root, 1280, 800));
//        setFullScreen(true);
        setTitle("Choix des joueurs");
        show();
    }

    private void creerBinding() {
        ChangeListener<Number> dimensionListener = (observable, oldValue, newValue) -> {
            if (observable == this.widthProperty()) {
                setHeight(this.getWidth() / ASPECT_RATIO);
            } else if (observable == this.heightProperty()) {
                setWidth(this.getHeight() * ASPECT_RATIO);
            }
        };

        this.widthProperty().addListener(dimensionListener);
        this.heightProperty().addListener(dimensionListener);

        validation.setOnAction(event -> {
            nomsJoueurs.clear();
            setListeDesNomsDeJoueurs();
        });
    }

    protected void genererChampsJoueurs(int nbJoueurs) {
        for (int i = 0; i < nbJoueurs; i++) {
            int finalI = i;
            HBox playerTextField = new HBox();
            IJoueur.CouleurJoueur couleur = IJoueur.CouleurJoueur.values()[i];
            int position = i + 1;
            compteursCouleurs.add(i, position);
            ImageView avatar = new ImageView(new Image("images/avatars/" + couleur + "/avatar-" + position + ".png"));
            avatar.setOnMouseClicked(event -> {
                if (compteursCouleurs.get(finalI) == 5) compteursCouleurs.set(finalI, 1);
                else compteursCouleurs.set(finalI, compteursCouleurs.get(finalI) + 1);
                avatar.setImage(new Image("images/avatars/" + couleur + "/avatar-" + compteursCouleurs.get(finalI) + ".png"));
            });
            avatar.setFitHeight(60);
            avatar.setFitWidth(50);
            Label Joueurs = new Label();
            TextField nomJoueur = new TextField();
            Joueurs.setText("Joueur " + position + " : ");
            playerTextField.getChildren().addAll(avatar, Joueurs, nomJoueur);
            textFields.add(nomJoueur);
            avatarsJoueurs.put(couleur, avatar);
            vboxJoueurs.getChildren().add(playerTextField);
        }
        vboxJoueurs.getChildren().add(validation);
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
     * Définit l'action à exécuter lorsque le nombre de participants change
     */
    protected void setChangementDuNombreDeJoueursListener(ChangeListener<Integer> quandLeNombreDeJoueursChange) {
        nbJoueursProperty.addListener((InvalidationListener) quandLeNombreDeJoueursChange);
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

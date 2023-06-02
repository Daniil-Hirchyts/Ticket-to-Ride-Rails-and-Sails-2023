package fr.umontpellier.iut.rails.vues;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe correspond à une nouvelle fenêtre permettant de choisir le nombre et les noms des joueurs de la partie.
 *
 * Sa présentation graphique peut automatiquement être actualisée chaque fois que le nombre de joueurs change.
 * Lorsque l'utilisateur a fini de saisir les noms de joueurs, il demandera à démarrer la partie.
 */
public class VueChoixJoueurs extends Stage {

    private final ObservableList<String> nomsJoueurs;
    private int nbJoueurs;
    private Spinner<Integer> spinnerBox;
    private VBox spinnerVBox;
    private VBox vboxJoueurs;
    private Button validation;
    private ObservableList<TextField> textFields;



    public ObservableList<String> nomsJoueursProperty() {
        return nomsJoueurs;
    }

    public VueChoixJoueurs() {
        nomsJoueurs = FXCollections.observableArrayList();
        textFields = FXCollections.observableArrayList();
        vboxJoueurs = new VBox();
        spinnerVBox = new VBox();
        validation = new Button("Valider");

        spinnerBox = new Spinner();
        spinnerBox.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        spinnerVBox.getChildren().add(spinnerBox);
        spinnerVBox.getChildren().add(vboxJoueurs);
        creerBinding();
        Scene scene = new Scene(spinnerVBox);
        //change la taille de la fenêtre
        this.setWidth(300);
        this.setHeight(300);
        this.setScene(scene);
        this.setTitle("Choix des joueurs");
        this.show();
    }

    private void creerBinding() {
        validation.setOnAction(event -> {
            nomsJoueurs.clear();
            for (TextField textField : textFields) {
                nomsJoueurs.add(textField.getText());
            }
            setListeDesNomsDeJoueurs();
        });
        spinnerBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            vboxJoueurs.getChildren().clear();
            textFields.clear();
            int nbJoueurs = newValue.toString().charAt(0) - '0';
            genererChampsJoueurs(nbJoueurs);
        });
    }

    protected void genererChampsJoueurs(int nbJoueurs) {
        for (int i = 0; i < nbJoueurs; i++) {
            HBox playerTextField = new HBox();
            Label Joueurs = new Label();
            TextField nomJoueur = new TextField();
            Joueurs.setText("Joueur " + (i+1) + " : ");
            playerTextField.getChildren().addAll(Joueurs, nomJoueur);
            textFields.add(nomJoueur);
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
    protected void setChangementDuNombreDeJoueursListener(ChangeListener<Integer> quandLeNombreDeJoueursChange) {}

    /**
     * Vérifie que tous les noms des participants sont renseignés
     * et affecte la liste définitive des participants
     */
    protected void setListeDesNomsDeJoueurs() {
        ArrayList<String> tempNamesList = new ArrayList<>();
        for (int i = 1; i <= getNombreDeJoueurs() ; i++) {
            String name = getJoueurParNumero(i);
            if (name == null || name.equals("")) {
                tempNamesList.clear();
                break;
            }
            else
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
        return  nomsJoueurs.size();
    }

    /**
     * Retourne le nom que l'utilisateur a renseigné pour le ième participant à la partie
     * @param playerNumber : le numéro du participant
     */
    protected String getJoueurParNumero(int playerNumber) {
        return getNomsJoueurs().get(playerNumber-1);
    }

}

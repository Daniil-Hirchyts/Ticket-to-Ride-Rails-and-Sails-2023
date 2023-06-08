package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.*;
import fr.umontpellier.iut.rails.vues.layout.ConsoleLogger;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.Map;

import static javafx.beans.binding.Bindings.when;

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

    private IJeu jeu;
    private VueInstructionBar instructionBar;
    private final VuePlateau plateau;
    @FXML
    private ImageView pileCard;
    @FXML
    private HBox bottomHBox;
    @FXML
    private ScrollPane consoleScrollPane;
    @FXML
    private VBox consoleHistory;
    @FXML
    private GridPane topGridPane;
    @FXML
    private VBox piocheBox;
    @FXML
    private Pane pileBateauCarte;
    @FXML
    private Pane pileWagonCarte;
    @FXML
    private Pane borderTop;
    @FXML
    private Pane borderTopMask;
    @FXML
    private Pane borderDown;
    @FXML
    private Pane bottomColor;
    @FXML
    private Pane pileBateauCarteImage;
    @FXML
    private Pane pileWagonCarteImage;
    @FXML
    private VBox pileCartesDestinations;
    @FXML
    private TextField commandField;
    @FXML
    private Button btnPasser;
    @FXML
    private Button btnDestination;
    @FXML
    private VBox containerInstruction;
    @FXML
    private ImageView videBateau;
    @FXML
    private ImageView videWagon;

    public VueDuJeu(IJeu jeu, Map<IJoueur.CouleurJoueur, ImageView> avatarList) {
        this.jeu = jeu;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VueDuJeu.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videBateau.setVisible(false);
        videWagon.setVisible(false);

        setId("gameScene");
        borderTop.prefWidthProperty().bind(this.widthProperty());
//        borderTopMask.prefWidthProperty().bind(this.widthProperty());
        borderDown.prefWidthProperty().bind(this.widthProperty());
        bottomColor.prefWidthProperty().bind(this.widthProperty());

        String PNG = ".png";

        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            pileCartesDestinations.getChildren().clear();
            String bottom = "bottom-";
            bottomColor.setStyle("-fx-background-image: url('/images/background/bottom-colors/" + bottom + newValue.getCouleur().toString() + PNG + "');");
            for (IDestination d : newValue.getDestinations()) {
                pileCartesDestinations.getChildren().add(new VueDestination(d, this));
            }
            pileCartesDestinations.setSpacing(-pileCartesDestinations.getChildren().size()*1.5);
        });
        jeu.destinationsInitialesProperty().addListener(new ListChangeListener<IDestination>() {
            @Override
            public void onChanged(Change<? extends IDestination> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (IDestination destination : change.getAddedSubList()) {
                            ConsoleLogger.getInstance(null, null).log("Ajout de la destination " + destination.getVilles().toString());
                            pileCartesDestinations.getChildren().add(new VueDestination(destination, true, VueDuJeu.this));
                        }
                    }
                    if (change.wasRemoved()) {
                        for (IDestination destination : change.getRemoved()) {
                            ConsoleLogger.getInstance(null, null).log("Suppression de la destination " + destination.getVilles().toString());
                            pileCartesDestinations.getChildren().remove(trouverLabelDestination(destination));
                        }
                    }
                }
            }
        });
        plateau = new VuePlateau(jeu);
        setRowIndex(plateau, 1);
        setColumnIndex(plateau, 1);
        VueJoueurCourant vueJoueurCourant = new VueJoueurCourant(jeu, avatarList);
        add(vueJoueurCourant, 0, 2);
        setColumnSpan(vueJoueurCourant, 3);
        instructionBar = new VueInstructionBar(this);
        containerInstruction.getChildren().add(instructionBar);
        instructionBar.setTranslateY(20);
        getChildren().addAll(plateau);
        plateau.toBack();
        VueAutresJoueurs vueAutresJoueurs = new VueAutresJoueurs(jeu, avatarList);
        topGridPane.add(vueAutresJoueurs, 0, 0);
        vueAutresJoueurs.creerBindings();
        vueAutresJoueurs.setTranslateY(-15);
        vueAutresJoueurs.setScaleX(1.1);
        vueAutresJoueurs.setScaleY(1.1);
        ConsoleLogger.getInstance(consoleHistory, consoleScrollPane);
    }

    public VueDestination trouverLabelDestination(IDestination d) {
        for (Node n : pileCartesDestinations.getChildren())
            if (n instanceof VueDestination l)
                if (l.getDestination().equals(d))
                    return l;
        return null;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        ConsoleLogger.getInstance(null, null).log("Passage au joueur suivant");
        jeu.passerAEteChoisi();
    });

    public void creerBindings() {

        commandField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jeu.leNombreDePionsSouhaiteAEteRenseigne(commandField.getText());
                commandField.setText("");
            }
        });
        btnPasser.setOnMouseClicked(actionPasserParDefaut);
        btnDestination.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jeu.nouvelleDestinationDemandee();
            }
        });
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        plateau.creerBindings();
        jeu.instructionProperty().addListener((observable, oldValue, newValue) -> {
            ConsoleLogger.getInstance(null, null).log(newValue);
        });
        jeu.cartesTransportVisiblesProperty().addListener(new ListChangeListener<ICarteTransport>() {
            @Override
            public void onChanged(Change<? extends ICarteTransport> change) {
                piocheBox.getChildren().clear();
                for (ICarteTransport carteTransport : jeu.cartesTransportVisiblesProperty()) {
                    VueCarteTransport vueCarteTransport = new VueCarteTransport(carteTransport, 1, false);
                    vueCarteTransport.horizontalLayout();
                    piocheBox.getChildren().add(vueCarteTransport);

                }
            }
        });

        pileBateauCarte.setOnMouseClicked(mouseEvent -> {
            if (!jeu.piocheBateauVideProperty().get()) jeu.uneCarteBateauAEtePiochee();
            else ConsoleLogger.getInstance(null, null).log("La pioche de bateau est vide");
        });
        pileWagonCarte.setOnMouseClicked(mouseEvent -> {
            if (!jeu.piocheWagonVideProperty().get()) jeu.uneCarteWagonAEtePiochee();
            else ConsoleLogger.getInstance(null, null).log("La pioche de wagon est vide");
        });

        piocheBox.spacingProperty().bind(heightProperty().divide(when(heightProperty().lessThanOrEqualTo(960)).then(7).otherwise(heightProperty().subtract(1))).multiply(
                when(heightProperty().lessThanOrEqualTo(960)).then(-1).otherwise(1)));

        pileBateauCarteImage.setStyle("-fx-background-image: url('images/cartesWagons/dos-BATEAU.png'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: cover;");

        pileWagonCarteImage.setStyle("-fx-background-image: url('images/cartesWagons/dos-WAGON.png'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: cover;");

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(0.1);
        colorAdjust.setContrast(0.13);
        colorAdjust.setBrightness(0.1);
        pileBateauCarteImage.setEffect(colorAdjust);
        pileWagonCarteImage.setEffect(colorAdjust);

        jeu.piocheWagonVideProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                videWagon.setVisible(true);
                pileWagonCarteImage.setEffect(null);
                pileWagonCarteImage.setOpacity(0.75);
            }
            if (!newValue) {
                videWagon.setVisible(false);
                pileWagonCarteImage.setEffect(colorAdjust);
                pileWagonCarteImage.setOpacity(1);
            }
        });
        jeu.piocheBateauVideProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                videBateau.setVisible(true);
                pileBateauCarteImage.setEffect(null);
                pileBateauCarteImage.setOpacity(0.75);
            }
            if (!newValue) {
                videBateau.setVisible(false);
                pileBateauCarteImage.setEffect(colorAdjust);
                pileBateauCarteImage.setOpacity(1);
            }
        });
        jeu.piocheDestinationVideProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) btnDestination.setDisable(true);
            if (!newValue) btnDestination.setDisable(false);
        });

    }

    public VuePlateau getPlateau() {
        return plateau;
    }

    public VueDuJeu getVueDuJeu() {
        return this;
    }

    public IJeu getJeu() {
        return jeu;
    }

    public Button getBtnDestination() {
        return btnDestination;
    }

}

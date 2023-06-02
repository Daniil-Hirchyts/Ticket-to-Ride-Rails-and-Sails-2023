package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;

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

    private final IJeu jeu;

    private VuePlateau plateau;
    @FXML
    private ImageView pileCard;
    @FXML
    private VBox rightVBox;
    @FXML
    private HBox bottomHBox;
    @FXML
    private ScrollPane consoleScrollPane;
    @FXML
    private VBox consoleHistory;
    @FXML
    private GridPane topGridPane;
    @FXML
    private GridPane playersGrid;
    @FXML
    private VBox piocheBox;
    @FXML
    private Pane pileBateauCarte;
    @FXML
    private Pane pileWagonCarte;

    @FXML
    private Pane borderTop;
    @FXML
    private Pane borderDown;

    @FXML
    private Pane bottomColor;
    public static final String PNG = ".png";


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

        setId("gameScene");

        borderTop.prefWidthProperty().bind(this.widthProperty());
        borderDown.prefWidthProperty().bind(this.widthProperty());
        bottomColor.prefWidthProperty().bind(this.widthProperty());

        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            String bottom = "bottom-";
            bottomColor.setStyle("-fx-background-image: url('/images/background/bottom-colors/" + bottom + newValue.getCouleur().toString() + PNG + "');");

        });

        plateau = new VuePlateau(jeu);
        setRowIndex(plateau, 1);
        setColumnIndex(plateau, 1);

        VueJoueurCourant vueJoueurCourant = new VueJoueurCourant(jeu);
        add(vueJoueurCourant, 0, 2);
        setColumnSpan(vueJoueurCourant, 3);
        vueJoueurCourant.creerBindings();
        getChildren().addAll(plateau);

        VueAutresJoueurs vueAutresJoueurs = new VueAutresJoueurs(jeu);
        topGridPane.add(vueAutresJoueurs, 0, 0);
        vueAutresJoueurs.creerBindings();

        ConsoleLogger.getInstance(consoleHistory, consoleScrollPane);
    }

    public void creerBindings() {
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
                    vueCarteTransport.setId("carte-horizontal");
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

        pileBateauCarte.setStyle("-fx-background-image: url('images/cartesWagons/dos-BATEAU.png'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: cover;")
        ;

        pileWagonCarte.setStyle("-fx-background-image: url('images/cartesWagons/dos-WAGON.png'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: cover;");
    }

    public IJeu getJeu() {
        return jeu;
    }
}

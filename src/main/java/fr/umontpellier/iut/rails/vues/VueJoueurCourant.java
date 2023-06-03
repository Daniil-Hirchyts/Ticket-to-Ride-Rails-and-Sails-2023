package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;
import static javafx.beans.binding.Bindings.when;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 * <p>
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends GridPane {
    private IJeu jeu;
    private IJoueur joueur;
    private Map<IJoueur.CouleurJoueur, ImageView> avatarList;
    //    @FXML
//    private VBox infoVBox;
    //    @FXML
//    private VBox vboxDestinations;
    @FXML
    private Label playerName;
    @FXML
    private ImageView playerAvatar;
    @FXML
    private ImageView playerLabelColorBackground;
    //    @FXML
//    private Label pionsWagon;
//    @FXML
//    private Label pionsBateau;
//    @FXML
//    private Label pionsPort;
    @FXML
    private Label scoreLabel;
    @FXML
    private HBox playerCartesBox;
    //    @FXML
//    private Button btnPionsBateau;
//    @FXML
//    private Button btnPionsWagon;
    @FXML
    private HBox flagsBox;

    private IntegerProperty wagons = new SimpleIntegerProperty();
    private IntegerProperty bateaux = new SimpleIntegerProperty();
    private int ports;


    public VueJoueurCourant(IJeu jeu, Map<IJoueur.CouleurJoueur, ImageView> avatarList) {
        this.jeu = jeu;
        this.avatarList = avatarList;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playerInterface.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        creerBindings();
    }

    public void creerBindings() {
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                flagsBox.getChildren().clear();
                wagons.bind(newValue.nbPionsWagonsProperty());
                bateaux.bind(newValue.nbPionsBateauxProperty());
                ports = newValue.getNbPorts();
                VueFlag pionsWagon = new VueFlag(newValue, wagons, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/train.png"))), "wagons");
                VueFlag pionsBateau = new VueFlag(newValue, bateaux, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ship.png"))), "bateaux");
                VueFlag pionsPort = new VueFlag(newValue, ports, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ancor.png"))), "ports");
                pionsWagon.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");
                pionsBateau.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");
                pionsPort.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");
                flagsBox.getChildren().addAll(pionsWagon, pionsBateau, pionsPort);
                playerName.setText(newValue.getNom());
//                pionsWagon.textProperty().bind(newValue.nbPionsWagonsProperty().asString());
//                pionsBateau.textProperty().bind(newValue.nbPionsBateauxProperty().asString());
//                pionsPort.setText(valueOf(newValue.getNbPorts()));
                scoreLabel.setText(valueOf(newValue.getScore()));
                playerAvatar.setImage(avatarList.get(newValue.getCouleur()).getImage());
                playerLabelColorBackground.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/current-player/score-background/" + newValue.getCouleur().toString() + ".png"))));
            }
//            else {
//                pionsWagon.textProperty().unbind();
//                pionsBateau.textProperty().unbind();
//            }
        });

        for (IJoueur j : jeu.getJoueurs()) {
            j.cartesTransportProperty().addListener(new ListChangeListener<ICarteTransport>() {
                @Override
                public void onChanged(Change<? extends ICarteTransport> change) {
                    while (change.next()) {
                        playerCartesBox.getChildren().clear();
                        for (ICarteTransport carteTransport : change.getList()) {
                            ConsoleLogger.getInstance(null, null).log("Ajout de la carte " + carteTransport.getStringCouleur().toString());
                            VueCarteTransport vueCarteTransport = new VueCarteTransport(carteTransport, 1, true);
                            vueCarteTransport.verticalLayout();
                            vueCarteTransport.setId("carte-vertical");
                            playerCartesBox.getChildren().add(vueCarteTransport);
                        }
                    }
                }
            });
        }
        playerCartesBox.spacingProperty().bind(widthProperty().divide(8).multiply(-1));

        jeu.joueurCourantProperty().addListener(new ChangeListener<IJoueur>() {
            @Override
            public void changed(ObservableValue<? extends IJoueur> observableValue, IJoueur iJoueur, IJoueur iJoueur2) {
                joueur = iJoueur2;
                playerCartesBox.getChildren().clear();
                for (ICarteTransport c : joueur.getCartesTransport()) {
                    VueCarteTransport vueCarteTransport = new VueCarteTransport(c, 1, true);
                    vueCarteTransport.verticalLayout();
                    vueCarteTransport.setId("carte-vertical");
                    playerCartesBox.getChildren().add(vueCarteTransport);
                }
            }
        });

//        btnPionsWagon.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                jeu.nouveauxPionsWagonsDemandes();
//            }
//        });
//
//        btnPionsBateau.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                jeu.nouveauxPionsBateauxDemandes();
//            }
//        });
    }

    public static String getCouleurJoueur(IJoueur.CouleurJoueur couleurJoueur) {
        return switch (couleurJoueur) {
            case ROUGE -> "#d02a1b";
            case VERT -> "#6fae1e";
            case BLEU -> "#769799";
            case JAUNE -> "#f0dc00";
            case ROSE -> "#b74b97";
        };
    }
}

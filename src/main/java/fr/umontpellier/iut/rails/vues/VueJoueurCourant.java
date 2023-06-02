package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
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
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private String nomJoueur;
    @FXML
    private VBox infoVBox;
    @FXML
    private VBox vboxDestinations;
    @FXML
    private Button btnPasserTour;
    @FXML
    private Label playerName;
    @FXML
    private ImageView playerAvatar;
    @FXML
    private VBox colorBack;
    @FXML
    private Label pionsWagon;
    @FXML
    private Label pionsBateau;
    @FXML
    private Label pionsPort;
    @FXML
    private TextField commandField;
    @FXML
    private Button btnSendCommand;
    @FXML
    private Label scoreLabel;
    @FXML
    private HBox playerCartesBox;
    @FXML
    private Button btnPionsBateau;
    @FXML
    private Button btnPionsWagon;
    @FXML
    private Button btnDestination;
    public VueJoueurCourant(IJeu jeu) {
        this.jeu = jeu;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playerInterface.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        ConsoleLogger.getInstance(null, null).log("Passage au joueur suivant");
        jeu.passerAEteChoisi();
    });

    public void creerBindings() {
        btnPasserTour.setOnMouseClicked(actionPasserParDefaut);
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playerName.setText(newValue.getNom());
                nomJoueur = newValue.getNom();
                pionsWagon.textProperty().bind(newValue.nbPionsWagonsProperty().asString());
                pionsBateau.textProperty().bind(newValue.nbPionsBateauxProperty().asString());
                pionsPort.setText(valueOf(newValue.getNbPorts()));
                scoreLabel.setText(valueOf(newValue.getScore()));
                playerAvatar.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/Avatar/avatar-" + newValue.getCouleur().toString().toUpperCase() + ".png"))));
                colorBack.setStyle("-fx-background-color: " + getCouleurJoueur(newValue.getCouleur()) + ";");
            } else {
                pionsWagon.textProperty().unbind();
                pionsBateau.textProperty().unbind();
            }
        });
        for (IJoueur j : jeu.getJoueurs()) {
            j.cartesTransportProperty().addListener(new ListChangeListener<ICarteTransport>() {
                @Override
                public void onChanged(Change<? extends ICarteTransport> change) {
                    while (change.next()) {
                        playerCartesBox.getChildren().clear();
                        for (ICarteTransport carteTransport : change.getList()) {
                            ConsoleLogger.getInstance(null, null).log("Ajout de la carte " + carteTransport.getStringCouleur().toString());
                            VueCarteTransport vueCarteTransport = new VueCarteTransport(carteTransport, 1,true);
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

        commandField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jeu.leNombreDePionsSouhaiteAEteRenseigne(commandField.getText());
                commandField.setText("");
                btnSendCommand.fire();
            }
        });

        commandField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) btnSendCommand.fire();
            }
        });

        btnSendCommand.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                jeu.leNombreDePionsSouhaiteAEteRenseigne(commandField.getText());
                commandField.setText("");
            }
        });

        btnPionsWagon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jeu.nouveauxPionsWagonsDemandes();
            }
        });

        btnPionsBateau.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jeu.nouveauxPionsBateauxDemandes();
            }
        });

        btnDestination.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jeu.nouvelleDestinationDemandee();
            }
        });
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

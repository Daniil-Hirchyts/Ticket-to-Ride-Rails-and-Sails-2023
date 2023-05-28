package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 * <p>
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends GridPane {
    IJeu jeu;
    List<ICarteTransport> cartesTransport;
    @FXML
    private HBox playerHBox;
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
        cartesTransport = new ArrayList<>();
    }

    public VueDestination trouverLabelDestination(IDestination d) {
        for (Node n : vboxDestinations.getChildren())
            if (n instanceof VueDestination l)
                if (l.getText().equals(d.getVilles().toString())) return l;
        return null;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        ConsoleLogger.getInstance(null, null).log("Passage au joueur suivant");
        jeu.passerAEteChoisi();
    });

    public void creerBindings() {
        //TODO : creer les bindings pour les des pioches
        btnPasserTour.setOnMouseClicked(actionPasserParDefaut);
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playerName.setText(newValue.getNom());
                pionsWagon.setText(valueOf(newValue.getNbPionsWagon()));
                pionsBateau.setText(valueOf(newValue.getNbPionsBateau()));
                pionsPort.setText(valueOf(newValue.getNbPorts()));
                scoreLabel.setText(valueOf(newValue.getScore()));
                playerAvatar.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/Avatar/avatar-" + newValue.getCouleur().toString().toUpperCase() + ".png"))));
                colorBack.setStyle("-fx-background-color: " + getCouleurJoueur(newValue.getCouleur()) + ";");
            }
        });
        for (IJoueur j : jeu.getJoueurs())
            j.cartesTransportProperty().addListener(new ListChangeListener<ICarteTransport>() {
                @Override
                public void onChanged(Change<? extends ICarteTransport> change) {
                    cartesTransport.clear();
                    cartesTransport.addAll(j.getCartesTransport());
                    for (ICarteTransport c : cartesTransport) {
//                        playerHBox.getChildren().add(new VueCarteTransport(c, 1));
//                        playerHBox.getChildren().add(new Label(c:toString());
                    }
                }
            });
        jeu.destinationsInitialesProperty().addListener(new ListChangeListener<IDestination>() {
            @Override
            public void onChanged(Change<? extends IDestination> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (IDestination destination : change.getAddedSubList()) {
                            ConsoleLogger.getInstance(null, null).log("Ajout de la destination " + destination.getVilles().toString());
                            vboxDestinations.getChildren().add(new VueDestination(destination));
                        }
                    }
                    if (change.wasRemoved()) {
                        for (IDestination destination : change.getRemoved()) {
                            ConsoleLogger.getInstance(null, null).log("Suppression de la destination " + destination.getVilles().toString());
                            vboxDestinations.getChildren().remove(trouverLabelDestination(destination));
                        }
                    }
                }
            }
        });
    }

    public static String getCouleurJoueur(IJoueur.CouleurJoueur couleurJoueur) {
        return switch (couleurJoueur) {
            case ROUGE -> "#ff6565";
            case VERT -> "#65ff65";
            case BLEU -> "#6565ff";
            case JAUNE -> "#ffff65";
            case ROSE -> "#7f7f7f";
        };
    }
}

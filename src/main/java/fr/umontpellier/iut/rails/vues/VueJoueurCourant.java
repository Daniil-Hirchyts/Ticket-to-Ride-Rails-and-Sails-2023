package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.mecanique.data.Couleur;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private ImageView piocheWagon;
    @FXML
    private ImageView piocheBateau;
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
        piocheBateau.setImage(new Image("images/cartesWagons/dos-BATEAU.png"));
        piocheWagon.setImage(new Image("images/cartesWagons/dos-WAGON.png"));
    }

    public VueDestination trouverLabelDestination(IDestination d) {
        for (Node n : vboxDestinations.getChildren()) {
            if (n instanceof VueDestination) {
                VueDestination l = (VueDestination) n;
                if (l.getText().equals(d.getVilles().toString())) return l;
            }
        }
        return null;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        System.out.println("On a cliqué sur le bouton passer");
        jeu.passerAEteChoisi();
    });

    public void creerBindings() {
        //TODO : creer les bindings pour les des pioches
        btnPasserTour.setOnMouseClicked(actionPasserParDefaut);
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playerName.setText(newValue.getNom());
                playerAvatar.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/Avatar/avatar-" + newValue.getCouleur().toString().toUpperCase() + ".png"))));
                switch (getCouleurJoueur(newValue.getCouleur())) {
                    case "RED" -> colorBack.setStyle("-fx-background-color: #ff6565");
                    case "GREEN" -> colorBack.setStyle("-fx-background-color: #65ff65");
                    case "BLUE" -> colorBack.setStyle("-fx-background-color: #6565ff");
                    case "YELLOW" -> colorBack.setStyle("-fx-background-color: #ffff65");
                    case "BLACK" -> colorBack.setStyle("-fx-background-color: #7f7f7f");
                    default -> colorBack.setStyle("-fx-background-color: #ffffff");
                }
            }
        });
        for (IJoueur j : jeu.getJoueurs())
            j.cartesTransportProperty().addListener(new ListChangeListener<ICarteTransport>() {
                @Override
                public void onChanged(Change<? extends ICarteTransport> change) {
                    cartesTransport.clear();
                    cartesTransport.addAll(j.getCartesTransport());
                    for (ICarteTransport c : cartesTransport) {
                        playerHBox.getChildren().add(new VueCarteTransport(c, 1));
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
                            System.out.println("On a ajouté la destination " + destination.getVilles().toString());
                            vboxDestinations.getChildren().add(new VueDestination(destination));
                        }
                    }
                    if (change.wasRemoved()) {
                        for (IDestination destination : change.getRemoved()) {
                            System.out.println("On a enlevé la destination " + destination.getVilles().toString());
                            vboxDestinations.getChildren().remove(trouverLabelDestination(destination));
                        }
                    }
                }
            }
        });
    }

    private String getCouleurJoueur(IJoueur.CouleurJoueur couleurJoueur) {
        switch (couleurJoueur) {
            case ROUGE:
                return "RED";
            case VERT:
                return "GREEN";
            case BLEU:
                return "BLUE";
            case JAUNE:
                return "YELLOW";
            case ROSE:
                return "BLACK";
        }
        return "WHITE";
    }


}

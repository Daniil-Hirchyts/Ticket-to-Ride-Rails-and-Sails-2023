package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.vues.layout.ConsoleLogger;
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
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
    @FXML
    private Label playerName;
    @FXML
    private ImageView playerAvatar;
    @FXML
    private ImageView playerLabelColorBackground;
    @FXML
    private Label scoreLabel;
    @FXML
    private HBox playerCartesBox;
    private VueFlag pionsWagon;
    private VueFlag pionsBateau;
    private VueFlag pionsPort;

    @FXML
    private HBox flagsBox;

    private IntegerProperty wagons = new SimpleIntegerProperty();
    private IntegerProperty bateaux = new SimpleIntegerProperty();
    private int ports;


    public VueJoueurCourant(IJeu jeu, Map<IJoueur.CouleurJoueur, ImageView> avatarList) {
        this.jeu = jeu;
        this.avatarList = avatarList;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VueJoueurCourant.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        creerBindings();
        playerCartesBox.prefWidthProperty().bind(this.prefWidthProperty().subtract(450));
        this.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            spacingCalculator();
        });
        playerCartesBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            spacingCalculator();
        });
    }

    private void spacingCalculator() {
        double spacing = 0;
        double l = this.getPrefWidth() - 450;
        int n = playerCartesBox.getChildren().size();
        if (n > 1 && l > 0) spacing = (l - (130 * n)) / (n - 1);
        else spacing = 0;
        playerCartesBox.setSpacing(spacing);
    }

    public void creerBindings() {
        jeu.joueurCourantProperty().addListener((observable, oldValue, newValue) -> {
            flagsBox.getChildren().clear();
            wagons.bind(newValue.nbPionsWagonsProperty());
            bateaux.bind(newValue.nbPionsBateauxProperty());
            ports = newValue.getNbPorts();
            pionsWagon = new VueFlag(newValue, wagons, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/train.png"))), "wagons");
            pionsBateau = new VueFlag(newValue, bateaux, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ship.png"))), "bateaux");
            pionsPort = new VueFlag(newValue, ports, new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/player-interface/ancor.png"))), "ports");
            pionsWagon.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");
            pionsBateau.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");
            pionsPort.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");

            pionsWagon.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pionsWagon.getCircleFlag().setStyle("-fx-fill:" + getColorString(joueur.getCouleur().toString()) + ";");
                }
            });

            pionsWagon.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pionsWagon.getCircleFlag().setStyle("-fx-fill: -fx-brown-flag;");
                }
            });

            pionsWagon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pionsWagon.getTextFieldChangePions().requestFocus();
                    pionsWagon.getIconFlag().setVisible(false);
                    pionsWagon.getCircleFlag().setFill(Color.WHITE);
                    jeu.nouveauxPionsWagonsDemandes();
                    pionsWagon.getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent keyEvent) {
                            if (keyEvent.getCode() == KeyCode.ENTER) {
                                jeu.leNombreDePionsSouhaiteAEteRenseigne(pionsWagon.getTextField().getText());
                                pionsWagon.getIconFlag().setVisible(true);
                                pionsWagon.getCircleFlag().setStyle("-fx-fill: -fx-brown-flag;");
                                pionsWagon.getTextField().setText("");
                                pionsWagon.getTextField().setDisable(true);
                                pionsWagon.getTextField().setVisible(false);
                            }
                        }
                    });
                }
            });
            pionsBateau.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pionsBateau.getCircleFlag().setStyle("-fx-fill:" + getColorString(joueur.getCouleur().toString()) + ";");
                }
            });

            pionsBateau.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pionsBateau.getCircleFlag().setStyle("-fx-fill: -fx-brown-flag;");
                }
            });

            pionsBateau.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pionsBateau.getTextFieldChangePions().requestFocus();
                    pionsBateau.getIconFlag().setVisible(false);
                    pionsBateau.getCircleFlag().setFill(Color.WHITE);
                    jeu.nouveauxPionsBateauxDemandes();
                    pionsBateau.getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent keyEvent) {
                            if (keyEvent.getCode() == KeyCode.ENTER) {
                                jeu.leNombreDePionsSouhaiteAEteRenseigne(pionsBateau.getTextField().getText());
                                pionsBateau.getIconFlag().setVisible(true);
                                pionsBateau.getCircleFlag().setStyle("-fx-fill: -fx-brown-flag;");
                                pionsBateau.getTextField().setText("");
                                pionsBateau.getTextField().setDisable(true);
                                pionsBateau.getTextField().setVisible(false);
                            }
                        }
                    });
                }
            });

            flagsBox.getChildren().addAll(pionsWagon, pionsBateau, pionsPort);
            playerName.setText(newValue.getNom());
            scoreLabel.setText(valueOf(newValue.getScore()));
            playerAvatar.setImage(avatarList.get(newValue.getCouleur()).getImage());
            playerLabelColorBackground.setImage(new Image(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/elements/current-player/score-background/" + newValue.getCouleur().toString() + ".png"))));
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
                            playerCartesBox.getChildren().add(vueCarteTransport);
                        }
                    }
                }
            });

            j.cartesTransportPoseesProperty().addListener(new ListChangeListener<ICarteTransport>() {
                @Override
                public void onChanged(Change<? extends ICarteTransport> change) {
                    while (change.next()) {
                        for (ICarteTransport carteTransport : change.getList()) {
                            ConsoleLogger.getInstance(null, null).log("Ajout de la carte " + carteTransport.getStringCouleur().toString());
                            VueCarteTransport vueCarteTransport = new VueCarteTransport(carteTransport, 1, true);
                            vueCarteTransport.setCarteImagePosee();
                            playerCartesBox.getChildren().add(vueCarteTransport);
                        }
                    }
                }
            });
        }


        jeu.joueurCourantProperty().addListener(new ChangeListener<IJoueur>() {
            @Override
            public void changed(ObservableValue<? extends IJoueur> observableValue, IJoueur iJoueur, IJoueur iJoueur2) {
                joueur = iJoueur2;
                playerCartesBox.getChildren().clear();
                for (ICarteTransport c : joueur.getCartesTransport()) {
                    VueCarteTransport vueCarteTransport = new VueCarteTransport(c, 1, true);
                    vueCarteTransport.verticalLayout();
                    playerCartesBox.getChildren().add(vueCarteTransport);
                }
            }
        });
    }

    private String getColorString(String color) {
        return switch (color) {
            case "ROUGE" -> "#da0000";
            case "BLEU" -> "#005fa2";
            case "VERT" -> "#1b7000";
            case "JAUNE" -> "#ffd423";
            case "ROSE" -> "#a42bb9";
            default -> "-fx-brown-flag";
        };
    }
}

package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.mecanique.Graphe;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe représente la vue d'une carte Destination.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueDestination extends VBox {
    private Graphe graphe;
    protected final IDestination destination;
    private VueDuJeu vueDuJeu;

    public VueDestination(IDestination destination, VueDuJeu vueDuJeu) {
        this(destination, false, vueDuJeu);
    }

    public VueDestination(IDestination destination, boolean clickable, VueDuJeu vueDuJeu) {
        this.destination = destination;
        this.graphe = vueDuJeu.getJeu().getGraphe();
        for (String ville : destination.getVilles()) {
            Label label = new Label("➤ " + ville);
            label.setId("destinationLabel");
            label.getStyleClass().add("labelsIMFellEnglish");
            getChildren().add(label);
        }
        setSpacing(-6);
        ScaleTransition scaleTransitionHover = new ScaleTransition(Duration.millis(200), this);
        scaleTransitionHover.setFromX(1);
        scaleTransitionHover.setFromY(1);
        scaleTransitionHover.setToX(1.25);
        scaleTransitionHover.setToY(1.25);

        TranslateTransition translateTransitionHover = new TranslateTransition(Duration.millis(200), this);
        translateTransitionHover.setFromX(0);
        translateTransitionHover.setFromY(0);
        translateTransitionHover.setToX(-35);
        translateTransitionHover.setToY(0);

        ScaleTransition scaleTransitionRestore = new ScaleTransition(Duration.millis(200), this);
        scaleTransitionRestore.setToX(1);
        scaleTransitionRestore.setToY(1);

        TranslateTransition translateTransitionRestore = new TranslateTransition(Duration.millis(200), this);
        translateTransitionRestore.setToX(0);
        translateTransitionRestore.setToY(0);

        if (clickable) {
            this.setOnMouseClicked(event -> {
                ((VueDuJeu) getScene().getRoot()).getJeu().uneDestinationAEteChoisie(destination);
            });
            setId("destinationTicketChoix");
        } else setId("destinationTicket");

        this.setOnMouseEntered(event -> {
            List<Integer> villeToInteger = new ArrayList<>();
            for (String ville : destination.getVilles()) {
                if (vueDuJeu.getJeu().getIdVille(ville) != -1) {
                    vueDuJeu.getPlateau().getCercleDeLaVille(ville).getStyleClass().add("cercleVilleGlow");
                    Glow glow = new Glow();
                    vueDuJeu.getPlateau().getCercleDeLaVille(ville).setEffect(glow);
                    Transition transition = new Transition() {
                        {
                            setCycleDuration(Duration.millis(500));
                            setCycleCount(Animation.INDEFINITE);
                            setAutoReverse(true);
                        }

                        @Override
                        protected void interpolate(double frac) {
                            glow.setLevel(frac);
                        }
                    };
                    transition.play();
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), vueDuJeu.getPlateau().getCercleDeLaVille(ville));
                    scaleTransition.setFromX(1);
                    scaleTransition.setFromY(1);
                    scaleTransition.setToX(1.25);
                    scaleTransition.setToY(1.25);
                    scaleTransition.setCycleCount(Animation.INDEFINITE);
                    scaleTransition.setAutoReverse(true);
                    scaleTransition.play();
//                    Label label = new Label(ville);
//                    label.setStyle("-fx-background-color: rgb(106,78,57);");
//                    label.getStyleClass().add("labelsIMFellEnglish");
//                    label.setTranslateX(vueDuJeu.getPlateau().getCercleDeLaVille(ville).getCenterX());
//                    label.setTranslateY(vueDuJeu.getPlateau().getCercleDeLaVille(ville).getCenterY());
                    SVGPath star = new SVGPath();
                    star.setContent("M9.5 14.25l-5.584 2.936 1.066-6.218L.465 6.564l6.243-.907L9.5 0l2.792 5.657 6.243.907-4.517 4.404 1.066 6.218");
                    star.setFill(Color.valueOf("#5eff01"));
                    star.setTranslateX(vueDuJeu.getPlateau().getCercleDeLaVille(ville).getCenterX() - 9);
                    star.setTranslateY(vueDuJeu.getPlateau().getCercleDeLaVille(ville).getCenterY() - 9);
                    star.setScaleX(2);
                    star.setScaleY(2);
                    vueDuJeu.getPlateau().getChildren().addAll(star);
                    RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000), star);
                    rotateTransition.setFromAngle(0);
                    rotateTransition.setToAngle(360);
                    rotateTransition.setCycleCount(Animation.INDEFINITE);
                    rotateTransition.play();
                    star.setEffect(glow);
                    villeToInteger.add(vueDuJeu.getJeu().getIdVille(ville));
                }
            }
            for (String route : constructListRoute(vueDuJeu.getJeu().getGraphe().parcoursSansRepetition(villeToInteger)))
                for (Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute(route)) {
                    rectangle.getStyleClass().add("rectangleGlow");
                    rectangle.setEffect(new Glow());
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), rectangle);
                    scaleTransition.setFromX(1);
                    scaleTransition.setFromY(1);
                    scaleTransition.setToX(1.1);
                    scaleTransition.setToY(1.1);
                    scaleTransition.setCycleCount(Animation.INDEFINITE);
                    scaleTransition.setAutoReverse(true);
                    scaleTransition.play();
                }
            scaleTransitionHover.playFromStart();
            translateTransitionHover.playFromStart();
        });

        this.setOnMouseExited(event -> {
            List<Integer> villeToInteger = new ArrayList<>();
            for (String ville : destination.getVilles())
                if (vueDuJeu.getJeu().getIdVille(ville) != -1) {
                    vueDuJeu.getPlateau().getCercleDeLaVille(ville).getStyleClass().remove("cercleVilleGlow");
                    villeToInteger.add(vueDuJeu.getJeu().getIdVille(ville));
                }
            for (String route : constructListRoute(vueDuJeu.getJeu().getGraphe().parcoursSansRepetition(villeToInteger)))
                for (Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute(route)) {
                    rectangle.getStyleClass().remove("rectangleGlow");
                }
            vueDuJeu.getPlateau().getChildren().removeIf(node -> node instanceof SVGPath);
//            vueDuJeu.getPlateau().getChildren().removeIf(node -> node instanceof Label);
            scaleTransitionRestore.playFromStart();
            translateTransitionRestore.playFromStart();
        });
    }

    public List<String> constructListRoute(List<Integer> listeSommets) {
        List<String> listeRoute = new ArrayList<>();
        for (int i = 0; i < listeSommets.size() - 1; i++)
            listeRoute.add(graphe.getArete(listeSommets.get(i), listeSommets.get(i + 1)).route().getNom());
        return listeRoute;
    }

    public IDestination getDestination() {
        return destination;
    }
}

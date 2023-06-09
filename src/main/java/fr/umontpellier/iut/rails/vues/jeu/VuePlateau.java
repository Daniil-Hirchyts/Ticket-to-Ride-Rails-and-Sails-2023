package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.*;
import fr.umontpellier.iut.rails.vues.layout.ConsoleLogger;
import fr.umontpellier.iut.rails.vues.layout.DonneesGraphiques;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

/**
 * Cette classe présente les routes et les villes sur le plateau.
 * <p>
 * On y définit les handlers à exécuter lorsque qu'un élément du plateau a été choisi par l'utilisateur
 * ainsi que les bindings qui mettront à jour le plateau après la prise d'une route ou d'un port par un joueur
 */
public class VuePlateau extends Pane {

    private IJeu jeu;
    @FXML
    private ImageView mapMonde;
    @FXML
    private ImageView mapMondeFrame;

    private IDestination destinationListened;

    private Map<String, List<Rectangle>> RectangleDeLaRoute;
    private Map<String, Circle> cercleDesVilles;
    @FXML
    private Pane overlay;

    public VuePlateau(IJeu jeu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/jeu/VuePlateau.fxml"));
            this.jeu = jeu;
            this.destinationListened = null;
            this.RectangleDeLaRoute = new HashMap<>();
            this.cercleDesVilles = new HashMap<>();
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setMinSize(Screen.getPrimary().getBounds().getWidth() / 3, Screen.getPrimary().getBounds().getHeight() / 3);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(0.1);
        colorAdjust.setContrast(0.11);
        colorAdjust.setBrightness(0.1);
        mapMonde.setEffect(colorAdjust);
        mapMondeFrame.fitWidthProperty().bind(mapMonde.fitWidthProperty());
        mapMondeFrame.fitHeightProperty().bind(mapMonde.fitHeightProperty());
        mapMondeFrame.layoutXProperty().bind(mapMonde.layoutXProperty());
        mapMondeFrame.layoutYProperty().bind(mapMonde.layoutYProperty());
        overlay.setVisible(false);
    }

    public Pane getOverlay() {
        return overlay;
    }

    EventHandler<MouseEvent> choixRoute = event -> {
        ConsoleLogger.getInstance(null, null).log("On a cliqué sur une route");
        jeu.uneRouteAEteChoisie(event.getPickResult().getIntersectedNode().getId());
    };

    EventHandler<MouseEvent> choixPort = event -> {
        System.out.println("On a cliqué sur un port");
        jeu.unPortAEteChoisi(event.getPickResult().getIntersectedNode().getId());
    };

    public void creerBindings() {
        ajouterVilles();
        ajouterPorts();
        ajouterRoutes();
        bindRedimensionEtCentragePlateau();

        overlay.prefWidthProperty().bind(mapMondeFrame.fitWidthProperty());
        overlay.prefHeightProperty().bind(mapMondeFrame.fitHeightProperty());
    }

    private void ajouterPorts() {
        List<? extends IVille> listePorts = ((VueDuJeu) getScene().getRoot()).getJeu().getPorts();
        for (String nomPort : DonneesGraphiques.ports.keySet()) {
            DonneesGraphiques.DonneesCerclesPorts positionPortSurPlateau = DonneesGraphiques.ports.get(nomPort);
            IVille ville = listePorts.stream().filter(v -> v.getNom().equals(nomPort)).findAny().orElse(null);
            Circle cerclePort = new Circle(positionPortSurPlateau.centreX(), positionPortSurPlateau.centreY(), DonneesGraphiques.rayonInitial);
            cerclePort.setId(nomPort);
            getChildren().add(cerclePort);
            bindCerclePortAuPlateau(positionPortSurPlateau, cerclePort, ville);
            cerclePort.setOnMouseClicked(choixPort);
            cercleDesVilles.put(nomPort, cerclePort);
            Glow glow = new Glow();
            cerclePort.setOnMouseEntered(event -> cerclePort.setEffect(glow));
            cerclePort.setOnMouseExited(event -> cerclePort.setEffect(null));
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
        }
    }

    private void ajouterRoutes() {
        List<? extends IRoute> listeRoutes = ((VueDuJeu) getScene().getRoot()).getJeu().getRoutes();
        for (String nomRoute : DonneesGraphiques.routes.keySet()) {
            ArrayList<Rectangle> rectanglesRoute = new ArrayList<>();
            ArrayList<DonneesGraphiques.DonneesSegments> segmentsRoute = DonneesGraphiques.routes.get(nomRoute);
            IRoute route = listeRoutes.stream().filter(r -> r.getNom().equals(nomRoute)).findAny().orElse(null);
            for (DonneesGraphiques.DonneesSegments unSegment : segmentsRoute) {
                Rectangle rectangleSegment = new Rectangle(unSegment.getXHautGauche(), unSegment.getYHautGauche(), DonneesGraphiques.largeurRectangle, DonneesGraphiques.hauteurRectangle);
                rectangleSegment.setId(nomRoute);
                Glow glow = new Glow();
                rectangleSegment.setOnMouseEntered(event -> rectangleSegment.setEffect(glow));
                rectangleSegment.setOnMouseExited(event -> rectangleSegment.setEffect(null));
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
                rectangleSegment.setRotate(unSegment.getAngle());
                getChildren().add(rectangleSegment);
                rectangleSegment.setOnMouseClicked(choixRoute);
                bindRectangle(rectangleSegment, unSegment.getXHautGauche(), unSegment.getYHautGauche(), route);
                rectanglesRoute.add(rectangleSegment);
            }
            RectangleDeLaRoute.put(nomRoute, new ArrayList<>(rectanglesRoute));
            rectanglesRoute.clear();
        }
    }

    private void bindRedimensionEtCentragePlateau() {
        mapMonde.fitWidthProperty().bind(widthProperty());
        mapMonde.fitHeightProperty().bind(heightProperty());
        mapMonde.layoutXProperty().bind(new DoubleBinding() { // Pour maintenir le plateau au centre
            {
                super.bind(widthProperty(), heightProperty());
            }

            @Override
            protected double computeValue() {
                double imageViewWidth = mapMonde.getLayoutBounds().getWidth();
                return (getWidth() - imageViewWidth) / 2;
            }
        });
    }

    private void bindCerclePortAuPlateau(DonneesGraphiques.DonneesCerclesPorts port, Circle cerclePort, IVille ville) {
        cerclePort.centerXProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return mapMonde.getLayoutX() + port.centreX() * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        cerclePort.centerYProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return mapMonde.getLayoutY() + port.centreY() * mapMonde.getLayoutBounds().getHeight() / DonneesGraphiques.hauteurInitialePlateau;
            }
        });
        cerclePort.radiusProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return DonneesGraphiques.rayonInitial * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        cerclePort.fillProperty().bind(new ObjectBinding<Paint>() {
            {
                super.bind(ville.proprietaireProperty());
            }

            @Override
            protected Paint computeValue() {
                Paint couleur = Color.TRANSPARENT;
                if (ville.proprietaireProperty().getValue() != null) {
                    IJoueur.CouleurJoueur couleurJoueur = ville.proprietaireProperty().get().getCouleur();
                    ImageView pionsPosee = new ImageView(new Image("images/gares/gare-" + couleurJoueur + ".png"));
                    cerclePort.setId("circlePosee");
                    pionsPosee.layoutXProperty().bind(cerclePort.centerXProperty().subtract(10));
                    pionsPosee.layoutYProperty().bind(cerclePort.centerYProperty().subtract(20));
                    pionsPosee.fitHeightProperty().bind(cerclePort.radiusProperty().add(20));
                    pionsPosee.fitWidthProperty().bind(cerclePort.radiusProperty().add(10));
                    getChildren().add(pionsPosee);
                    return switch (ville.proprietaireProperty().get().getCouleur()) {
                        case ROUGE -> Color.valueOf("#d02a1b");
                        case VERT -> Color.valueOf("#6fae1e");
                        case BLEU -> Color.valueOf("#769799");
                        case JAUNE -> Color.valueOf("#f0dc00");
                        case ROSE -> Color.valueOf("#b74b97");
                    };
                }
                return couleur;
            }
        });
    }

    private void bindRectangle(Rectangle rect, double layoutX, double layoutY, IRoute route) {
        rect.widthProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return DonneesGraphiques.largeurRectangle * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.heightProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return DonneesGraphiques.hauteurRectangle * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.layoutXProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty(), mapMonde.xProperty());
            }

            @Override
            protected double computeValue() {
                return mapMonde.getLayoutX() + layoutX * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.xProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty(), mapMonde.xProperty());
            }

            @Override
            protected double computeValue() {
                return mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.layoutYProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return layoutY * mapMonde.getLayoutBounds().getHeight() / DonneesGraphiques.hauteurInitialePlateau;
            }
        });
        rect.yProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return mapMonde.getLayoutBounds().getHeight() / DonneesGraphiques.hauteurInitialePlateau;
            }
        });
        rect.fillProperty().bind(new ObjectBinding<Paint>() {
            {
                super.bind(route.proprietaireProperty());
            }

            @Override
            protected Paint computeValue() {
                Paint couleur = Color.TRANSPARENT;
                if (route.proprietaireProperty().getValue() != null) {

                    for (DonneesGraphiques.DonneesSegments segment : DonneesGraphiques.routes.get(route.getNom())) {
                        IJoueur.CouleurJoueur couleurJoueur = route.proprietaireProperty().get().getCouleur();
                        rect.setId("rectPosee");
                        ImageView pionsPosee = new ImageView(new Image("images/wagons/image-wagon-" + couleurJoueur + ".png"));
                        pionsPosee.layoutXProperty().bind(rect.layoutXProperty());
                        pionsPosee.layoutYProperty().bind(rect.layoutYProperty());
                        pionsPosee.rotateProperty().bind(rect.rotateProperty());
                        pionsPosee.fitHeightProperty().bind(rect.heightProperty().add(6));
                        pionsPosee.fitWidthProperty().bind(rect.widthProperty().add(2));
                        getChildren().add(pionsPosee);
                    }

                    return switch (route.proprietaireProperty().get().getCouleur()) {
                        case ROUGE -> Color.valueOf("#d02a1b");
                        case VERT -> Color.valueOf("#6fae1e");
                        case BLEU -> Color.valueOf("#769799");
                        case JAUNE -> Color.valueOf("#f0dc00");
                        case ROSE -> Color.valueOf("#b74b97");
                    };

                }
                return couleur;
            }
        });
    }

    public List<Rectangle> getRectangleDeLaRoute(String route) {
        List<Rectangle> Rectangles = RectangleDeLaRoute.get(route);
        return RectangleDeLaRoute.get(route);
    }

    public Circle getCercleDeLaVille(String ville) {
        return cercleDesVilles.get(ville);
    }

    public void setDestination(IDestination destinationListened) {
        this.destinationListened = destinationListened;
    }


    private void ajouterVilles() {
        List<? extends IVille> listeVilles = ((VueDuJeu) getScene().getRoot()).getJeu().getIVilles();
        for (String nomVille : DonneesGraphiques.villes.keySet()) {
            IVille ville = listeVilles.stream().filter(r -> r.getNom().equals(nomVille)).findAny().orElse(null);
            DonneesGraphiques.DonneesCerclesPorts positionVilleSurPlateau = DonneesGraphiques.villes.get(nomVille);
            Circle cercleVille = new Circle(positionVilleSurPlateau.centreX(), positionVilleSurPlateau.centreY(), DonneesGraphiques.rayonInitial);
            cercleVille.setId(nomVille);
            getChildren().add(cercleVille);
            bindCerclePortAuPlateau(positionVilleSurPlateau, cercleVille, ville);
            cercleVille.setOnMouseClicked(choixPort);
            cercleDesVilles.put(nomVille, cercleVille);
            Glow glow = new Glow();
            cercleVille.setOnMouseEntered(event -> cercleVille.setEffect(glow));
            cercleVille.setOnMouseExited(event -> cercleVille.setEffect(null));
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
        }
    }
}

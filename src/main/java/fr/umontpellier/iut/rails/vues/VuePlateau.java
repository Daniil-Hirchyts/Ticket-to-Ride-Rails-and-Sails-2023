package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.*;
import fr.umontpellier.iut.rails.mecanique.data.Couleur;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

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

    private IDestination destinationListened;

    private Map<String, List<Rectangle>> RectangleDeLaRoute;

    public VuePlateau(IJeu jeu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/plateau.fxml"));
            this.jeu = jeu;
            this.destinationListened = null;
            this.RectangleDeLaRoute = new HashMap<>();
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
                    System.out.println(cerclePort.centerXProperty().getValue() + " " + cerclePort.centerXProperty().getValue() + " " + cerclePort.radiusProperty().getValue());
                    //bind la position de l'image view à la position du rectangle
                    pionsPosee.layoutXProperty().bind(cerclePort.centerXProperty().subtract(10));
                    pionsPosee.layoutYProperty().bind(cerclePort.centerYProperty().subtract(20));
                    pionsPosee.fitHeightProperty().bind(cerclePort.radiusProperty().add(20));
                    pionsPosee.fitWidthProperty().bind(cerclePort.radiusProperty().add(10));
                    pionsPosee.setId("pionsPoseeW");
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
        //bind la couleur du rectangle à la couleur du joueur qui a construit la route
        rect.fillProperty().bind(new ObjectBinding<Paint>() {
            {
                super.bind(route.proprietaireProperty());
            }

            @Override
            protected Paint computeValue() {
                //si destination est over
                Paint couleur = Color.TRANSPARENT;
                if (destinationListened != null)  couleur = Color.BLACK;
                if (route.proprietaireProperty().getValue() != null) {

                    for (DonneesGraphiques.DonneesSegments segment : DonneesGraphiques.routes.get(route.getNom())) {
                        //creer une image View avec l'image des pions correspondant au joueur
                            IJoueur.CouleurJoueur couleurJoueur = route.proprietaireProperty().get().getCouleur();
                            rect.setId("rectPosee");
                            ImageView pionsPosee = new ImageView(new Image("images/wagons/image-wagon-" + couleurJoueur + ".png"));
                            System.out.println(rect.layoutXProperty().getValue() + " " + rect.layoutYProperty().getValue() + " " + rect.rotateProperty().getValue());
                            //bind la position de l'image view à la position du rectangle
                            pionsPosee.layoutXProperty().bind(rect.layoutXProperty());
                            pionsPosee.layoutYProperty().bind(rect.layoutYProperty());
                            pionsPosee.rotateProperty().bind(rect.rotateProperty());
                            pionsPosee.fitHeightProperty().bind(rect.heightProperty().add(4));
                            pionsPosee.fitWidthProperty().bind(rect.widthProperty());
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

    public void setDestination(IDestination destinationListened) {
        this.destinationListened = destinationListened;
    }



private void ajouterVilles() {
    for (String nomVille : DonneesGraphiques.villes.keySet()) {
        DonneesGraphiques.DonneesCerclesPorts positionVilleSurPlateau = DonneesGraphiques.villes.get(nomVille);
        Circle cercleVille = new Circle(positionVilleSurPlateau.centreX(), positionVilleSurPlateau.centreY(), DonneesGraphiques.rayonInitial);
        cercleVille.setId(nomVille);
        getChildren().add(cercleVille);
        bindCerclePortAuPlateau(positionVilleSurPlateau, cercleVille);
        cercleVille.setOnMouseClicked(choixPort);
    }
}
}

package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.mecanique.Graphe;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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

    public VueDestination(IDestination destination, boolean clickable,VueDuJeu vueDuJeu) {
        this.destination = destination;
        this.graphe = vueDuJeu.getJeu().getGraphe();
        for (String ville : destination.getVilles()) {
            Label label = new Label(ville);
            label.setId("destinationLabel");
            getChildren().add(label);
        }

        if (clickable) {
            this.setOnMouseClicked(event -> {
                ((VueDuJeu) getScene().getRoot()).getJeu().uneDestinationAEteChoisie(destination);
            });
            setId("destinationTicketChoix");
        } else setId("destinationTicket");

        this.setOnMouseEntered(event -> {
            List<Integer> villeToInteger = new ArrayList<>();
            for (String ville : destination.getVilles()) {
                int idVIlle = vueDuJeu.getJeu().getIdVille(ville);
                if (vueDuJeu.getJeu().getIdVille(ville) != -1){villeToInteger.add(vueDuJeu.getJeu().getIdVille(ville));}
            }
            for (String route : constructListRoute(vueDuJeu.getJeu().getGraphe().parcoursSansRepetition(villeToInteger))) {
                for(Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute(route)){
                    rectangle.setStyle("-fx-stroke: #000000; -fx-stroke-width: 2;");
                }
            }
        });

        this.setOnMouseExited(event -> {
            List<Integer> villeToInteger = new ArrayList<>();
            for (String ville : destination.getVilles()) {
                if (vueDuJeu.getJeu().getIdVille(ville) != -1){villeToInteger.add(vueDuJeu.getJeu().getIdVille(ville));}
            }
            for (String route : constructListRoute(vueDuJeu.getJeu().getGraphe().parcoursSansRepetition(villeToInteger))) {
                for(Rectangle rectangle : vueDuJeu.getPlateau().getRectangleDeLaRoute(route)){
                    rectangle.setStyle("-fx-stroke: transparent; -fx-stroke-width: 0;");
                }
            }
        });
    }

    public List<String> constructListRoute(List<Integer> listeSommets){
        List<String> listeRoute = new ArrayList<>();
        for (int i = 0; i < listeSommets.size()-1; i++) {
            listeRoute.add(graphe.getArete(listeSommets.get(i),listeSommets.get(i+1)).route().getNom());
        }
        return listeRoute;
    }

    public IDestination getDestination() {
        return destination;
    }
}

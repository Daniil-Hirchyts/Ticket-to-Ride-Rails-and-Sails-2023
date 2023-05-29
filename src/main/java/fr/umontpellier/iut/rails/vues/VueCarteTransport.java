package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Cette classe représente la vue d'une carte Transport.
 *
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueCarteTransport extends Pane {
    private ImageView imageView;
    private final ICarteTransport carteTransport;
    public VueCarteTransport(ICarteTransport carteTransport, int nbCartes) {
        this.carteTransport = carteTransport;
        if (carteTransport.estBateau()){
            if (carteTransport.estDouble()){
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-DOUBLE-"+ carteTransport.getStringCouleur()+".png",140,200,true,true));
            } else if (carteTransport.getAncre()){
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-BATEAU-"+ carteTransport.getStringCouleur()+"-A.png",140,200,true,true));
            } else {
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-BATEAU-"+ carteTransport.getStringCouleur()+".png",140,200,true,true));
            }
        } else if (carteTransport.estWagon()){
            if (carteTransport.getAncre()){
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-WAGON-"+ carteTransport.getStringCouleur()+"-A.png",140,200,true,true));
            } else {
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-WAGON-"+ carteTransport.getStringCouleur()+".png",140,200,true,true));
            }
        } else if (carteTransport.estJoker()){
            if (carteTransport.getAncre()){
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-JOKER-"+ carteTransport.getStringCouleur()+"-A.png",140,200,true,true));
            } else {
                this.imageView = new ImageView(new Image("images/cartesWagons/carte-JOKER-"+ carteTransport.getStringCouleur()+".png",140,200,true,true));
            }
        }
    }
    public ICarteTransport getCarteTransport() {
        return carteTransport;
    }

    public ImageView getImageView() {
        return imageView;
    }
}

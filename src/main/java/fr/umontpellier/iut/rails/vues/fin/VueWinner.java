package fr.umontpellier.iut.rails.vues.fin;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class VueWinner extends Pane {
    public VueWinner() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/fin/VueWinner.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

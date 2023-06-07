package fr.umontpellier.iut.rails.vues;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class VueLoser extends Pane {

    public VueLoser() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/templatePlayerLoser.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

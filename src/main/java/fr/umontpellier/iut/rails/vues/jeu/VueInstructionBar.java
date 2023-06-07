package fr.umontpellier.iut.rails.vues.jeu;

import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class VueInstructionBar extends Pane {

    VueDuJeu jeu;
    private static final Image instructionBarArrowUp = new Image("images/instructionPopUp/instructionBarArrowUp.png");
    private static final Image instructionBarArrowDown = new Image("images/instructionPopUp/instructionBarArrowDown.png");

    @FXML
    private Label labelInstruciton;

    @FXML
    private ImageView instructionBar;

    @FXML
    private ImageView instructionBarArrow;

    public VueInstructionBar(VueDuJeu jeu) {
        this.jeu = jeu;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/jeu/VueInstructionBar.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.instructionBarArrow.setImage(new Image("images/instructionPopUp/instructionBarArrowDown.png"));
        this.instructionBar.setImage(new Image("images/instructionPopUp/instructionBar.png"));
        this.labelInstruciton.setId("instructionLabel");
        creerBindings();
    }

    private void creerBindings() {
        instructionBarArrow.setOnMouseClicked(mouseEvent -> {
            if (instructionBarArrow.getImage().equals(instructionBarArrowUp)) {
                //TODO : Transition d'animation pour l'ouverture de la barre d'instruction et du instructionBarArrow et du label
                TranslateTransition tt = new TranslateTransition();
                TranslateTransition tt2 = new TranslateTransition();
                TranslateTransition tt3 = new TranslateTransition();

                tt2.setNode(instructionBarArrow);
                tt.setNode(instructionBar);
                tt3.setNode(labelInstruciton);

                tt2.setDuration(javafx.util.Duration.millis(700));
                tt3.setDuration(javafx.util.Duration.millis(700));
                tt.setDuration(javafx.util.Duration.millis(700));

                tt2.setByY(-50);
                tt3.setByY(-50);
                tt.setByY(-50);

                tt.setOnFinished(actionEvent -> {
                    System.out.println(instructionBarArrowDown);
                    instructionBarArrow.setImage(instructionBarArrowDown);
                });

                tt.play();
                tt2.play();
                tt3.play();


            } else {
                //TODO : Transition d'animation pour l'ouverture de la barre d'instruction et du instructionBarArrow et du label
                TranslateTransition tt = new TranslateTransition();
                TranslateTransition tt2 = new TranslateTransition();
                TranslateTransition tt3 = new TranslateTransition();

                tt2.setNode(instructionBarArrow);
                tt.setNode(instructionBar);
                tt3.setNode(labelInstruciton);

                tt2.setDuration(javafx.util.Duration.millis(700));
                tt3.setDuration(javafx.util.Duration.millis(700));
                tt.setDuration(javafx.util.Duration.millis(700));


                tt2.setByY(50);
                tt3.setByY(50);
                tt.setByY(50);

                tt.setOnFinished(actionEvent -> {
                    System.out.println(instructionBarArrowUp);
                    instructionBarArrow.setImage(instructionBarArrowUp);
                });

                tt.play();
                tt2.play();
                tt3.play();
            }
        });

        jeu.getJeu().instructionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                labelInstruciton.setText(newValue);
            }
        });
    }
}

package fr.umontpellier.iut.rails.vues;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ConsoleLogger {
    private static ConsoleLogger instance = null;
    private VBox consoleHistory;
    private ScrollPane consoleScrollPane;

    private ConsoleLogger(VBox consoleHistory, ScrollPane consoleScrollPane) {
        this.consoleHistory = consoleHistory;
        this.consoleScrollPane = consoleScrollPane;
    }

    public static ConsoleLogger getInstance(VBox consoleHistory, ScrollPane consoleScrollPane) {
        if (instance == null) instance = new ConsoleLogger(consoleHistory, consoleScrollPane);
        return instance;
    }

    public void log(String message) {
        Label label = new Label(message);
        consoleHistory.getChildren().addAll(label);
        consoleScrollPane.setVvalue(1);
        label.setId("consoleText");
    }
}

package org.example.restart;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.example.PrimaryController;

public class RestartController {

    @FXML
    private Pane restart;

    public void injectMainController(PrimaryController primaryController) {
//        this.controller = primaryController;
    }

    public void showOverlay() {
        restart.setVisible(true);
    }

    public void hideOverlay() {
        restart.setVisible(false);
    }

    public void initialize() {
        restart.setVisible(false);
    }
}

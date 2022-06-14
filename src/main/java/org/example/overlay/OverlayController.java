package org.example.overlay;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.example.PrimaryController;
import org.example.UserStorageAndConfiguration;

public class OverlayController {


    @FXML
    private Pane overlay;

    @FXML
    Label notConnected;

    public void injectMainController(PrimaryController primaryController){
//        this.controller = primaryController;
    }

    public void showOverlay() {
        overlay.setVisible(true);
    }

    public void hideOverlay() {
        overlay.setVisible(false);
    }

    public void initialize() {
        overlay.setVisible(false);
        UserStorageAndConfiguration.bindLocaleKey(notConnected,"overlay.notConnected");

    }
}

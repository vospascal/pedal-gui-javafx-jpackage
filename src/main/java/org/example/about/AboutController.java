package org.example.about;

import javafx.event.ActionEvent;
import org.example.PrimaryController;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {
    private PrimaryController controller;

    public void injectMainController(PrimaryController primaryController){
        this.controller = primaryController;
    }

    public void gui(ActionEvent actionEvent) {
        navigateUrl("https://github.com/vospascal/pedal-gui");
    }
    public void arduino(ActionEvent actionEvent) {
        navigateUrl("https://github.com/vospascal/pedal-arduino/");
    }
    public void paypal(ActionEvent actionEvent) {
        navigateUrl("https://www.paypal.com/donate/?business=TBPE6XCB2XBMW&item_name=pedalbox&currency_code=EUR");
    }

    public static boolean navigateUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    URI uri = new URI(url);
                    desktop.browse(uri);
                    return true;
                } catch (URISyntaxException | IOException ex) {
                    System.out.println(ex);
                }
            }
        }

        return false;
    }
}

package org.example.about;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.PrimaryController;
import org.example.UserStorageAndConfiguration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {

    private PrimaryController controller;

    public void injectMainController(PrimaryController primaryController){
        this.controller = primaryController;
    }
    @FXML
    public TextFlow aboutTextFlow;

    public void initialize() {
        Text textThankyou = new Text();
        textThankyou.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.label.thankyou"));

        Text textResources = new Text();
        textResources.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.label.resources"));


        Text textGuiLabel = new Text();
        textGuiLabel.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.label.resources.gui"));

        Hyperlink linkGui = new Hyperlink();
        linkGui.setOnAction(this::gui);
        linkGui.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.link.resources.gui"));

        Text textArduinoLabel = new Text();
        textArduinoLabel.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.label.resources.arduino"));

        Hyperlink linkArduino = new Hyperlink();
        linkArduino.setOnAction(this::arduino);
        linkArduino.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.link.resources.arduino"));


        Text textDonateLabel = new Text();
        textDonateLabel.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.label.donate"));

        Hyperlink donateLink = new Hyperlink();
        donateLink.setOnAction(this::paypal);
        donateLink.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.about.link.donate"));


        ObservableList list = aboutTextFlow.getChildren();
        list.addAll(
                textThankyou,  new Text("\n"),
                textResources,  new Text("\n"),
                textGuiLabel, linkGui,  new Text("\n"),
                textArduinoLabel, linkArduino,  new Text("\n"),
                textDonateLabel, donateLink,  new Text("\n")
        );
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

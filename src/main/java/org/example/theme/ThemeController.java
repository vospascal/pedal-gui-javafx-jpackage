package org.example.theme;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import org.example.App;

public class ThemeController {
    @FXML
    private CheckBox change_theme;
    @FXML
    private MenuItem dark;
    @FXML
    private MenuItem light;

    public void initialize() {
        dark.setOnAction(evt -> {
            try{
                App.getScene().getStylesheets().remove(App.themeLightUrl);
                App.getScene().getStylesheets().add(App.themeDarkUrl);
            }catch (Exception e) { }
        });

        light.setOnAction(evt -> {
            try{
                App.getScene().getStylesheets().remove(App.themeDarkUrl);
                App.getScene().getStylesheets().add(App.themeLightUrl);
            }catch (Exception e) {}
        });
    }
}

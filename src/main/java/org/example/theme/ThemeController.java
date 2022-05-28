package org.example.theme;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import org.example.App;
import org.example.UserStorageAndConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThemeController {
    @FXML
    private CheckBox change_theme;
    @FXML
    private MenuItem dark;
    @FXML
    private MenuItem light;
    @FXML
    private MenuItem future;
    @FXML
    private ChoiceBox changeLanguage;
    @FXML
    private ChoiceBox changeTheme;

    public void initialize() {

        UserStorageAndConfiguration config = UserStorageAndConfiguration.getInstance();
        List<String> langs = new ArrayList<>(config.getAvailableLanguages());

        String[] languageNames = new String[langs.size()];
        for (int n = 0; n < langs.size(); n++) {
            languageNames[n] = getLanguageName(langs.get(n).split("_")[0]);
        }

        ObservableList<String> languageOptions  = FXCollections.observableArrayList(languageNames);

        //Assign to ChoiceBox
        changeLanguage.setValue(getLanguageName(config.getActualLanguage().split("_")[0]));
        changeLanguage.setItems(languageOptions);

        changeLanguage.setOnAction(event -> {
            for (int n = 0; n < languageNames.length; n++) {
                if (languageNames[n].equalsIgnoreCase(changeLanguage.getValue().toString())){
                    config.setActualLanguage(langs.get(n));
                }
            }
            /// restart whole app
            try {
                new App().reloadStage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        List<String> themes = new ArrayList<>(config.getAvailableThemes());
        ObservableList<String> themeOptions  = FXCollections.observableArrayList(themes);
        changeTheme.setValue(config.getActualTheme());
        changeTheme.setItems(themeOptions);

        changeTheme.setOnAction(event -> {
            App.getScene().getStylesheets().remove(App.class.getResource("styles/themes/theme_" + UserStorageAndConfiguration.getInstance().getActualTheme().toLowerCase() + ".css").toExternalForm());

            for (String theme : themes) {
                if (theme.equalsIgnoreCase(changeTheme.getValue().toString())) {
                    config.setActualTheme(theme);
                    App.getScene().getStylesheets().add(App.class.getResource("styles/themes/theme_" + UserStorageAndConfiguration.getInstance().getActualTheme().toLowerCase() + ".css").toExternalForm());
                }
            }
        });


//        dark.setOnAction(evt -> {
//            try{
//                App.getScene().getStylesheets().remove(App.themeFutureUrl);
//                App.getScene().getStylesheets().remove(App.themeLightUrl);
//                App.getScene().getStylesheets().add(App.themeDarkUrl);
//            }catch (Exception e) { }
//        });
//
//        light.setOnAction(evt -> {
//            try{
//                App.getScene().getStylesheets().remove(App.themeFutureUrl);
//                App.getScene().getStylesheets().remove(App.themeDarkUrl);
//                App.getScene().getStylesheets().add(App.themeLightUrl);
//            }catch (Exception e) {}
//        });
//
//        future.setOnAction(evt -> {
//            try{
//                App.getScene().getStylesheets().remove(App.themeDarkUrl);
//                App.getScene().getStylesheets().remove(App.themeLightUrl);
//                App.getScene().getStylesheets().add(App.themeFutureUrl);
//            }catch (Exception e) {}
//        });
    }

    private String getLanguageName(String lng) {
        Locale loc = new Locale(lng);
        String str = loc.getDisplayLanguage(loc);
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

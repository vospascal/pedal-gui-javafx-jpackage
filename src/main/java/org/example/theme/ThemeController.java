package org.example.theme;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import org.example.App;
import org.example.PrimaryController;
import org.example.UserStorageAndConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThemeController {
    private PrimaryController controller;

    @FXML
    private ChoiceBox changeLanguage;

    @FXML
    private ChoiceBox changeTheme;

    @FXML
    private Label title_language;

    @FXML
    private Label title_theme;

    public void injectMainController(PrimaryController primaryController) {
        this.controller = primaryController;
    }

    public void initialize() {

//        title_language.setText(UserStorageAndConfiguration.getString("title.language"));
//        title_theme.setText(UserStorageAndConfiguration.getString("title.theme"));

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
                    this.controller.restart();
                }
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

    }

    private String getLanguageName(String lng) {
        Locale loc = new Locale(lng);
        String str = loc.getDisplayLanguage(loc);
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

package org.example.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.example.App;
import org.example.UserStorageAndConfiguration;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Util {
    private static ResourceBundle resourceBundle;

    private Util(){

    }

    public static Pane loadSource(String file) {
        Pane pane = null;

        try {
            pane = (Pane) Util.getParentRoot(file);
        } catch (IOException e) {
            System.err.println(Util.getString("bug.title") + " " + Util.getString("bug.panelLoad"));
            e.printStackTrace();
            System.exit(0);
        }

        if (pane == null){
            System.err.println(Util.getString("bug.title") + " " + Util.getString("bug.panelLoad"));
            System.exit(0);
        }
        return pane;
    }


    private static void setResourceBundleLanguage(){
        String[] language = UserStorageAndConfiguration.getInstance().getActualLanguage().split("_");
        Locale locale = new Locale(language[0], language[1]);
        resourceBundle = ResourceBundle.getBundle("/i18n/strings", locale);
    }

    public static Parent getParentRoot(String file) throws IOException {
        setResourceBundleLanguage();
        return FXMLLoader.load(Objects.requireNonNull(App.class.getResource(file + ".fxml")),
                resourceBundle);
    }

    public static String getString(String str){
        if (resourceBundle == null){
            setResourceBundleLanguage();
        }
        return resourceBundle.getString(str);
    }


}
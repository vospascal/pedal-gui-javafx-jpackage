package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;


import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    public static String themeDarkUrl = App.class.getResource("theme_dark.css").toExternalForm();
    public static String themeLightUrl = App.class.getResource("theme_light.css").toExternalForm();
    public static String themeFutureUrl = App.class.getResource("theme_future.css").toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"));

        // set dark theme
        if(!scene.getStylesheets().contains(themeLightUrl)) scene.getStylesheets().add(themeLightUrl);

        stage.setTitle("PedalBox");
        Image iconImage = new Image(getClass().getResource("assets/pedal.png").toString());
        stage.getIcons().add(iconImage);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest((WindowEvent we) -> {
            Platform.exit();
            System.exit(0);
        });

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Scene getScene() {
        return scene;
    }


}
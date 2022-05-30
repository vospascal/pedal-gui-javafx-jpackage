package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Stage stage;
    private static Scene scene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;
        startStage();
    }

    public void startStage() throws IOException {
        UserStorageAndConfiguration.loadData(); //Load user settings
        Parent root = UserStorageAndConfiguration.getParentRoot("primary");
        scene = new Scene(root);

        String theme = UserStorageAndConfiguration.getInstance().getActualTheme().toLowerCase();
        scene.getStylesheets().add(this.getClass().getResource("styles/themes/theme_" + theme + ".css").toExternalForm());

//        stage.setTitle(UserStorageAndConfiguration.getString("text.applicationTitle"));
        stage.titleProperty().bind(UserStorageAndConfiguration.createStringBinding("text.applicationTitle"));

        Image iconImage = new Image(this.getClass().getResource("assets/pedal.png").toString());
        stage.getIcons().add(iconImage);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest((WindowEvent we) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static Scene getScene() {
        return scene;
    }
}
module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires jdk.httpserver;
    requires java.net.http;

    opens org.example to javafx.fxml;
    opens org.example.brake to javafx.fxml;
    opens org.example.clutch to javafx.fxml;
    opens org.example.throttle to javafx.fxml;
    opens org.example.overlay to javafx.fxml;
    opens org.example.time to javafx.fxml;


    exports org.example;
}
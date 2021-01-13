module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;

//    exports org.example.brake;
//    exports org.example.clutch;
//    exports org.example.throttle;
//    exports org.example.overlay;
//    exports org.example.time;

    opens org.example to javafx.fxml;
    opens org.example.brake to javafx.fxml;
    opens org.example.clutch to javafx.fxml;
    opens org.example.throttle to javafx.fxml;
    opens org.example.overlay to javafx.fxml;
    opens org.example.time to javafx.fxml;


    exports org.example;
}
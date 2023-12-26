module WorkoutApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    exports application;
    exports application.controllers;
    exports application.utility;

    opens application;
    opens application.controllers;
    opens application.utility;
}
module WorkoutApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    exports application;
    exports application.controllers;

    opens application;
    opens application.controllers;
}
module org.example.workoutsapp.workoutsapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens org.example.workoutsapp.workoutsapp to javafx.fxml;
    opens org.example.workoutsapp.workoutsapp.controllers;
    opens org.example.workoutsapp.workoutsapp.utility;

    exports org.example.workoutsapp.workoutsapp;
    //exports org.example.workoutsapp.workoutsapp.controllers;
    //exports org.example.workoutsapp.workoutsapp.utility;
}
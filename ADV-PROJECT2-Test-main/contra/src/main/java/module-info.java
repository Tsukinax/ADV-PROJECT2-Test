module se233.contra {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    // Logging modules
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // Open packages for JavaFX
    opens se233.contra to javafx.fxml;
    opens se233.contra.controller to javafx.fxml;
    opens se233.contra.view to javafx.fxml;

    // Export packages
    exports se233.contra;
    exports se233.contra.controller;
    exports se233.contra.model;
    exports se233.contra.view;
    exports se233.contra.util;
    exports se233.contra.exception;
}
module fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;

    opens client to javafx.fxml;
    opens server to javafx.fxml, javafx.base;
    opens fx.controllers to javafx.fxml, javafx.graphics;
    opens fx to javafx.fxml, javafx.graphics;
    exports client;
    exports fx.controllers;
    exports fx;
}

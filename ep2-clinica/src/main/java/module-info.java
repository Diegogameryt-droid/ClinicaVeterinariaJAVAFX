module clinica {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens cl.duoc.clinica to javafx.fxml;
    opens cl.duoc.clinica.controller to javafx.fxml;
    opens cl.duoc.clinica.model to com.google.gson;

    exports cl.duoc.clinica;
}

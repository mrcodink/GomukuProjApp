module org.example.gomukuprojapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gomukuprojapp to javafx.fxml;
    exports org.example.gomukuprojapp;
}
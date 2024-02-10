module com.example.nomicosecitta {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.nomicosecitta to javafx.fxml;
    exports com.example.nomicosecitta;
}
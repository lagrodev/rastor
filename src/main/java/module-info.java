module com.cgvsu.rasterizationfxapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cgvsu.rasterizationfxapp to javafx.fxml;
    exports com.cgvsu.rasterizationfxapp;
    exports com.cgvsu.rasterizationfxapp.figure;
    opens com.cgvsu.rasterizationfxapp.figure to javafx.fxml;
}
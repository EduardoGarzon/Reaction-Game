module com.example.reactiongame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.reactiongame to javafx.fxml;
    exports com.example.reactiongame;
}
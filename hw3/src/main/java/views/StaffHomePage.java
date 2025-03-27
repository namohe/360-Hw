package views;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StaffHomePage {
    private final DatabaseHelper databaseHelper;

    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        
        // Header Section
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Staff Dashboard");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        headerBox.getChildren().addAll(welcomeLabel, logoutButton);
        mainLayout.setTop(headerBox);
        
        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Dashboard");
    }
}
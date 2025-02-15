package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {
	private final DatabaseHelper databaseHelper;

    public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, User!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(userLabel);
	    Scene userScene = new Scene(layout, 800, 400);
	    
	    Button questionspage = new Button("questions");
        questionspage.setOnAction(e -> {
            new questions(databaseHelper).show(primaryStage);
        });
        
        Button answerspage = new Button("answers");
        answerspage.setOnAction(e -> {
            new answer(databaseHelper).show(primaryStage, 1);
        });
       
        layout.getChildren().addAll(questionspage, answerspage);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}
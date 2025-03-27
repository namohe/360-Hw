package application;

import javafx.application.Application;
import javafx.stage.Stage;
import views.FirstPage;
import views.SetupLoginSelectionPage;

import java.sql.SQLException;

import helpers.DatabaseHelper;


public class StartCSE360 extends Application {	
	public static void main( String[] args )
	{
		 launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        try {
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
            if (databaseHelper.isDatabaseEmpty()) {
            	new FirstPage(databaseHelper).show(primaryStage);
            } else {
            	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
                
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
    }
}

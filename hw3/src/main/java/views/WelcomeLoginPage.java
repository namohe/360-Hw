package views;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import models.UserRole;

public class WelcomeLoginPage {

    private final DatabaseHelper databaseHelper;
    private String currentUser;

    public WelcomeLoginPage(DatabaseHelper databaseHelper, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle(StyleConstants.LOGIN_LAYOUT_STYLE);

        Label welcomeLabel = new Label("Welcome, " + user.getUserName() + "!");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);
        layout.getChildren().add(welcomeLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        layout.getChildren().add(logoutButton);

        // Check the user's role and add appropriate buttons for navigation
        if (user.hasRole(UserRole.ADMIN)) {
            Button adminPageButton = new Button("Go to Admin Page");
            adminPageButton.setOnAction(a -> new AdminHomePage(databaseHelper, user.getUserName()).show(primaryStage));
            layout.getChildren().add(adminPageButton);
        }

        if (user.hasRole(UserRole.INSTRUCTOR)) {
            Button instructorPageButton = new Button("Go to Instructor Page");
            instructorPageButton.setOnAction(a -> new InstructorHomePage(databaseHelper).show(primaryStage));
            layout.getChildren().add(instructorPageButton);
        }

        if (user.hasRole(UserRole.STAFF)) {
            Button staffPageButton = new Button("Go to Staff Page");
            staffPageButton.setOnAction(a -> new StaffHomePage(databaseHelper).show(primaryStage));
            layout.getChildren().add(staffPageButton);
        }

        if (user.hasRole(UserRole.REVIEWER)) {
            Button reviewerPageButton = new Button("Go to Reviewer Page");
            reviewerPageButton.setOnAction(a -> new ReviewerHomePage(databaseHelper, user.getUserName()).show(primaryStage));
            layout.getChildren().add(reviewerPageButton);
        }

        if (user.hasRole(UserRole.STUDENT)) {
            Button studentPageButton = new Button("Go to Student Page");
            studentPageButton.setOnAction(a -> new StudentHomePage(databaseHelper, currentUser).show(primaryStage));
            layout.getChildren().add(studentPageButton);
        }

        // Button to quit the application
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit();
        });
        layout.getChildren().add(quitButton);
        
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Welcome Page");
    }
}
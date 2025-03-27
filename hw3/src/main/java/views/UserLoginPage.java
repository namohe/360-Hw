package views;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.ReviewerRequestStatus;
import models.User;
import models.UserRole;

import java.sql.SQLException;
import java.util.Set;

import helpers.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {

    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input field for the user's username and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setStyle(StyleConstants.INPUT_FIELD_STYLE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle(StyleConstants.INPUT_FIELD_STYLE);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle(StyleConstants.ERROR_LABEL_STYLE);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(a -> {
            try {
                String userName = userNameField.getText();
                String password = passwordField.getText();

                // First check if user exists
                if (databaseHelper.doesUserExist(userName)) {
                    // Get roles and create user object
                    Set<UserRole> roles = databaseHelper.getUserRoles(userName);
                    User user = new User(userName, password);
                    
                    // Add all retrieved roles to the user
                    for (UserRole role : roles) {
                        user.addRole(role);
                    }
                    
                    // Attempt login
                    if (databaseHelper.login(user)) {
                        user.updateLastActive();
                        
                        if (roles.contains(UserRole.STUDENT)) {
                            ReviewerRequestStatus requestStatus = databaseHelper.wasReviewerRequestAccepted(userName);

                            // Show alerts based on reviewer status
                            if (requestStatus == ReviewerRequestStatus.ACCEPTED) {
                                // Show accepted alert
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Congratulations!");
                                alert.setHeaderText(null);
                                alert.setContentText("You have been accepted as a reviewer.");
                                alert.showAndWait();
                                
                                // Reset the request status to NO_REQUEST
                                databaseHelper.resetReviewerRequestStatus(userName);
                            } else if (requestStatus == ReviewerRequestStatus.DENIED) {
                                // Show denied alert
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Request Denied");
                                alert.setHeaderText(null);
                                alert.setContentText("Your request to be a reviewer has been denied.");
                                alert.showAndWait();
                                
                                // Reset the request status to NO_REQUEST
                                databaseHelper.resetReviewerRequestStatus(userName);
                            }
                        }
                        
                        if (roles.size() == 1) {
                            // Direct navigation based on single role
                            UserRole role = roles.iterator().next();
                            switch (role) {
                                case ADMIN:
                                    new AdminHomePage(databaseHelper, user.getUserName()).show(primaryStage);
                                    break;
                                case INSTRUCTOR:
                                    new InstructorHomePage(databaseHelper).show(primaryStage);
                                    break;
                                case STAFF:
                                    new StaffHomePage(databaseHelper).show(primaryStage);
                                    break;
                                case REVIEWER:
                                    new ReviewerHomePage(databaseHelper, user.getUserName()).show(primaryStage);
                                    break;
                                case STUDENT:
                                    new StudentHomePage(databaseHelper, userName).show(primaryStage);
                                    break;
                            }
                        } else {
                            // Show welcome page for users with multiple roles
                            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper, userName);
                            welcomeLoginPage.show(primaryStage, user);
                        }
                    } else {
                        errorLabel.setText("Invalid password. Please try again.");
                    }
                } else {
                    errorLabel.setText("Username does not exist.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
                errorLabel.setText("A database error occurred. Please try again.");
            }        
        });

        // Forgot Password Button
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setOnAction(e -> new ForgotPasswordPage(databaseHelper).show(primaryStage));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));
        
        VBox layout = new VBox(10);
        layout.setStyle(StyleConstants.LOGIN_LAYOUT_STYLE);
        layout.getChildren().addAll(userNameField, passwordField, loginButton, forgotPasswordButton, errorLabel, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}

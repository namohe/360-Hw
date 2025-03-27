package views;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;

import java.sql.SQLException;

import helpers.StyleConstants;
import helpers.UserNameRecognizer;
import helpers.DatabaseHelper;
import helpers.PasswordEvaluator;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;

    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setStyle(StyleConstants.FIELD_STYLE);
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle(StyleConstants.FIELD_STYLE);
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setStyle(StyleConstants.FIELD_STYLE);
        inviteCodeField.setMaxWidth(250);
        
        Label errorLabel = new Label();
        errorLabel.setStyle(StyleConstants.ERROR_LABEL_STYLE);
        
        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            
            System.out.println("Debug - Username: " + userName);
            System.out.println("Debug - Password: " + password);
            System.out.println("Debug - Invite Code: " + code);
            
            String validationError = UserNameRecognizer.checkForValidUserName(userName);
            if (!validationError.isEmpty()) {
                errorLabel.setText(validationError);
                System.out.println("Debug - Username validation failed: " + validationError);
                return;
            }
            String passwordValidationResult = PasswordEvaluator.evaluatePassword(password);
            if (!passwordValidationResult.equals("")) {
                errorLabel.setText(passwordValidationResult);
                return;
            }
            if(!databaseHelper.doesUserExist(userName)) {
                User validatedUser = databaseHelper.validateInvitationCode(code, userName, password);
                if (validatedUser != null) {
                    new WelcomeLoginPage(databaseHelper, userName).show(primaryStage, validatedUser);
                } else {
                    errorLabel.setText("Please enter a valid invitation code");
                }
            } else {
                errorLabel.setText("This userName is taken!!.. Please use another to setup an account");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        VBox layout = new VBox(10);
        layout.setStyle(StyleConstants.LOGIN_LAYOUT_STYLE);
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, setupButton, errorLabel, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
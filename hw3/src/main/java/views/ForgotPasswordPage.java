package views;

import helpers.DatabaseHelper;
import helpers.PasswordEvaluator;
import helpers.StyleConstants;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ForgotPasswordPage {
    private final DatabaseHelper databaseHelper;

    public ForgotPasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle(StyleConstants.LOGIN_LAYOUT_STYLE);

        Label titleLabel = new Label("Password Reset");
        titleLabel.setStyle(StyleConstants.WELCOME_LABEL_STYLE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(250);

        TextField otpField = new TextField();
        otpField.setPromptText("Enter one-time password");
        otpField.setMaxWidth(250);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setMaxWidth(250);

        Label messageLabel = new Label();

        Button resetButton = new Button("Reset Password");
        resetButton.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String otp = otpField.getText();
                String newPassword = newPasswordField.getText();

                // First check if OTP is valid without clearing it
                if (databaseHelper.validateOneTimePassword(username, otp)) {
                    // Then validate the new password
                    String passwordValidation = PasswordEvaluator.evaluatePassword(newPassword);
                    if (passwordValidation.isEmpty()) {
                        // Only clear OTP and update password if both validations pass
                        databaseHelper.clearOneTimePassword(username);
                        databaseHelper.updatePassword(username, newPassword);
                        messageLabel.setStyle(StyleConstants.SUCCESS_LABEL_STYLE);
                        messageLabel.setText("Password reset successful. Please login with your new password.");
                        
                        // Return to login page after 2 seconds
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                javafx.application.Platform.runLater(() -> {
                                    new UserLoginPage(databaseHelper).show(primaryStage);
                                });
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    } else {
                        messageLabel.setStyle(StyleConstants.ERROR_LABEL_STYLE);
                        messageLabel.setText(passwordValidation);
                    }
                } else {
                    messageLabel.setStyle(StyleConstants.ERROR_LABEL_STYLE);
                    messageLabel.setText("Invalid username or one-time password");
                }
            } catch (Exception ex) {
                messageLabel.setStyle(StyleConstants.ERROR_LABEL_STYLE);
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));

        layout.getChildren().addAll(
            titleLabel,
            usernameField,
            otpField,
            newPasswordField,
            resetButton,
            messageLabel,
            backButton
        );

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Password Reset");
        primaryStage.show();
    }
}

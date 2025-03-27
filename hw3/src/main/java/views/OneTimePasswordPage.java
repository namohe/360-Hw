package views;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OneTimePasswordPage {
    public void show(DatabaseHelper databaseHelper, Stage primaryStage, String currentUsername) {
        VBox layout = new VBox(10);
        layout.setStyle(StyleConstants.LOGIN_LAYOUT_STYLE);

        Label titleLabel = new Label("Set One-Time Password");
        titleLabel.setStyle(StyleConstants.WELCOME_LABEL_STYLE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(250);

        TextField otpField = new TextField();
        otpField.setPromptText("Enter one-time password");
        otpField.setMaxWidth(250);

        Label messageLabel = new Label();
        messageLabel.setStyle(StyleConstants.SUCCESS_LABEL_STYLE);

        Button setOTPButton = new Button("Set One-Time Password");
        setOTPButton.setOnAction(e -> {
            try {
                databaseHelper.setOneTimePassword(usernameField.getText(), otpField.getText());
                messageLabel.setText("One-time password set successfully");
                messageLabel.setStyle(StyleConstants.SUCCESS_LABEL_STYLE);
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
                messageLabel.setStyle(StyleConstants.ERROR_LABEL_STYLE);
            }
        });

        Button backButton = new Button("Back to Admin Home");
        backButton.setOnAction(e -> new AdminHomePage(databaseHelper, currentUsername).show(primaryStage));

        layout.getChildren().addAll(
            titleLabel,
            usernameField,
            otpField,
            setOTPButton,
            messageLabel,
            backButton
        );

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Set One-Time Password");
        primaryStage.show();
    }
}

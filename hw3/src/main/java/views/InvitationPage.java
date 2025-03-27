package views;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import helpers.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.UserRole;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {
	public void show(DatabaseHelper databaseHelper, Stage primaryStage, String currentUsername) {
		VBox layout = new VBox(10);
		layout.setStyle(StyleConstants.CENTERED_LAYOUT_STYLE);

		// Role selection
		VBox rolesBox = new VBox(5);
		rolesBox.setStyle(StyleConstants.CENTERED_LAYOUT_STYLE);
		Set<UserRole> selectedRoles = new HashSet<>();
		for (UserRole role : UserRole.values()) {
			CheckBox roleBox = new CheckBox(role.getValue());
			roleBox.setOnAction(e -> {
				if (roleBox.isSelected()) {
					selectedRoles.add(role);
				} else {
					selectedRoles.remove(role);
				}
			});
			rolesBox.getChildren().add(roleBox);
		}

		// Date picker for expiration
		DatePicker expirationPicker = new DatePicker();
		expirationPicker.setValue(LocalDate.now().plusDays(7)); // Default 7 days

		Button generateButton = new Button("Generate Invitation Code");
		Label codeLabel = new Label();

		generateButton.setOnAction(e -> {
			if (selectedRoles.isEmpty()) {
				new Alert(Alert.AlertType.ERROR, "Please select at least one role").show();
				return;
			}
			String code = databaseHelper.generateInvitationCode(selectedRoles, expirationPicker.getValue(), currentUsername);
			codeLabel.setText("Invitation Code: " + code);
		});

		Button backButton = new Button("Back to Admin Home");
		backButton.setOnAction(e -> new AdminHomePage(databaseHelper, currentUsername).show(primaryStage));

		layout.getChildren().addAll(
				new Label("Select Roles:"),
				rolesBox,
				new Label("Select Expiration Date:"),
				expirationPicker,
				generateButton,
				codeLabel,
				backButton
		);

		Scene scene = new Scene(layout, 800, 400);
		primaryStage.setScene(scene);
	}
}
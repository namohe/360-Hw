package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import models.UserRole;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import helpers.DatabaseHelper;
import helpers.StyleConstants;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */
public class AdminHomePage {
    private final DatabaseHelper databaseHelper;
    private final String currentAdminUsername;

    public AdminHomePage(DatabaseHelper databaseHelper, String adminUsername) {
        this.databaseHelper = databaseHelper;
        this.currentAdminUsername = adminUsername;
    }

    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
    
        // Header Section
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Admin Dashboard");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        headerBox.getChildren().addAll(welcomeLabel, logoutButton);
        mainLayout.setTop(headerBox);

        // Create a VBox for the main content
        VBox contentBox = new VBox(10); // 10 is spacing between elements
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label passwordMgtHeader = new Label("Password Management");
        passwordMgtHeader.setStyle(StyleConstants.SECTION_HEADER_STYLE);
        contentBox.getChildren().add(passwordMgtHeader);
        
        Button otpButton = new Button("Set One-Time Password");
        otpButton.setOnAction(e -> {
            new OneTimePasswordPage().show(databaseHelper, primaryStage, currentAdminUsername);
        });
        contentBox.getChildren().add(otpButton);

        Label userListHeader = new Label("User Management");
        userListHeader.setStyle(StyleConstants.SECTION_HEADER_STYLE);
        contentBox.getChildren().add(userListHeader);

        Button inviteButton = new Button("Generate Invitation Code");
        inviteButton.setOnAction(a -> new InvitationPage().show(databaseHelper, primaryStage, currentAdminUsername));
        contentBox.getChildren().add(inviteButton);

        try {
            List<User> users = databaseHelper.getUserList();
            for (User user : users) {
                long inactiveDays = ChronoUnit.DAYS.between(user.getLastActiveDate(), LocalDate.now());

                // Create horizontal layout for each user row
                javafx.scene.layout.HBox userRow = new javafx.scene.layout.HBox(10); // 10 is the spacing
                userRow.setAlignment(javafx.geometry.Pos.CENTER);

                // Create a more detailed user info label with inactivity period
                Label userLabel = new Label(String.format(
                    "Username: %s | Roles: %s | Inactive for: %d days",
                    user.getUserName(),
                    user.getRolesAsString(),
                    inactiveDays
                ));
                userRow.getChildren().add(userLabel);

                if (!user.getUserName().equals(currentAdminUsername)) {  // Don't allow deleting own account
                    Button deleteButton = new Button("Delete Account");
                    deleteButton.setOnAction(event -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                            "Are you sure you want to delete " + user.getUserName() + "?", 
                            ButtonType.YES, ButtonType.NO);
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.YES) {
                                try {
                                    databaseHelper.deleteUser(user.getUserName());
                                    refreshPage(primaryStage);
                                } catch (Exception ex) {
                                    new Alert(Alert.AlertType.ERROR, "Failed to delete user").show();
                                }
                            }
                        });
                    });
                    userRow.getChildren().add(deleteButton);
                }

                if (!user.getUserName().equals(currentAdminUsername)) {  // Don't allow editing own roles
                    Button manageRolesButton = new Button("Manage Roles");
                    manageRolesButton.setOnAction(event -> showRoleManagementDialog(user, primaryStage));
                    userRow.getChildren().add(manageRolesButton);
                }
                
                contentBox.getChildren().add(userRow);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load user list").show();
        }

        // Set the contentBox to the center region of BorderPane
        mainLayout.setCenter(contentBox);
        Scene adminScene = new Scene(mainLayout, 800, 400);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }

    private void refreshPage(Stage primaryStage) {
        show(primaryStage);
    }

    private void showRoleManagementDialog(User user, Stage primaryStage) {
        Dialog<Set<UserRole>> dialog = new Dialog<>();
        dialog.setTitle("Manage Roles for " + user.getUserName());
        
        VBox content = new VBox(10);
        Set<UserRole> selectedRoles = new HashSet<>(user.getRoles());
        
        // Create checkboxes for each role
        for (UserRole role : UserRole.values()) {
            CheckBox roleBox = new CheckBox(role.getValue());
            roleBox.setSelected(selectedRoles.contains(role));
            roleBox.setOnAction(e -> {
                if (roleBox.isSelected()) {
                    selectedRoles.add(role);
                } else {
                    selectedRoles.remove(role);
                }
            });
            content.getChildren().add(roleBox);
        }
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return selectedRoles;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(roles -> {
            if (!roles.isEmpty()) {
                try {
                    databaseHelper.updateUserRoles(user.getUserName(), roles);
                    refreshPage(primaryStage);
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, 
                            "Cannot update roles: " + e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.INFORMATION, "No roles selected. No changes made.").show();
            }
        });
    }
}

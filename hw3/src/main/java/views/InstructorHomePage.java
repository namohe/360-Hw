package views;

import java.sql.SQLException;
import java.util.List;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class InstructorHomePage {
    private final DatabaseHelper databaseHelper;
    private VBox requestsListBox;

    public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        
        // Header Section
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Instructor Dashboard");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        headerBox.getChildren().addAll(welcomeLabel, logoutButton);
        mainLayout.setTop(headerBox);
        
        VBox pendingRequestsSection = createPendingRequestsSection();
        mainLayout.setCenter(pendingRequestsSection);
        
        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor Dashboard");
    }
    
    private VBox createPendingRequestsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));

        Label header = new Label("Pending Reviewer Requests");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        requestsListBox = new VBox(5);
        try {
            List<String> pendingRequests = databaseHelper.getPendingReviewerRequests();
            for (String username : pendingRequests) {
                HBox requestBox = new HBox(10);
                Label usernameLabel = new Label(username);
                Button acceptButton = new Button("Accept");
                Button denyButton = new Button("Deny");

                acceptButton.setOnAction(e -> handleAcceptRequest(username));
                denyButton.setOnAction(e -> handleDenyRequest(username));

                requestBox.getChildren().addAll(usernameLabel, acceptButton, denyButton);
                requestsListBox.getChildren().add(requestBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        section.getChildren().addAll(header, requestsListBox);
        return section;
    }
    
    private void handleAcceptRequest(String username) {
        try {
            databaseHelper.updateReviewerRequestStatus(username, "ACCEPTED");
            databaseHelper.addReviewerRole(username);
            showAlert("Request accepted", "Request accepted for " + username, Alert.AlertType.INFORMATION);
            
            // Refresh the pending requests section
            refreshPendingRequests();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to accept request.", Alert.AlertType.ERROR);
        }
    }

    private void handleDenyRequest(String username) {
        try {
            databaseHelper.updateReviewerRequestStatus(username, "DENIED");
            showAlert("Request Denied", "Request denied for " + username, Alert.AlertType.INFORMATION);
            
            // Refresh the pending requests section
            refreshPendingRequests();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to deny request.", Alert.AlertType.ERROR);
        }
    }

    private void refreshPendingRequests() {
        // Clear the current list and repopulate it
        requestsListBox.getChildren().clear();
        try {
            List<String> pendingRequests = databaseHelper.getPendingReviewerRequests();
            for (String username : pendingRequests) {
                HBox requestBox = new HBox(10);
                Label usernameLabel = new Label(username);
                Button acceptButton = new Button("Accept");
                Button denyButton = new Button("Deny");

                acceptButton.setOnAction(e -> handleAcceptRequest(username));
                denyButton.setOnAction(e -> handleDenyRequest(username));

                requestBox.getChildren().addAll(usernameLabel, acceptButton, denyButton);
                requestsListBox.getChildren().add(requestBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
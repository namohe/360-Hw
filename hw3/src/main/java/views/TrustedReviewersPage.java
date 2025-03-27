package views;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.ReviewerRating;

import java.sql.SQLException;
import java.util.List;

public class TrustedReviewersPage {
    private final DatabaseHelper databaseHelper;
    private final String currentUser;
    private Stage primaryStage;

    public TrustedReviewersPage(DatabaseHelper databaseHelper, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        this.primaryStage = primaryStage; // Store the primary stage
        BorderPane mainLayout = new BorderPane();

        // Header Section
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Trusted Reviewers");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);

        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper, currentUser).show(primaryStage));

        headerBox.getChildren().addAll(welcomeLabel, backButton);
        mainLayout.setTop(headerBox);

        // Trusted Reviewers Section
        VBox trustedReviewersSection = createTrustedReviewersSection();
        mainLayout.setCenter(trustedReviewersSection);

        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trusted Reviewers");
        primaryStage.show();
    }

    private VBox createTrustedReviewersSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));

        Label header = new Label("Trusted Reviewers");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        VBox reviewersList = new VBox(5);
        try {
            List<ReviewerRating> trustedReviewers = databaseHelper.getTrustedReviewers(currentUser);
            for (ReviewerRating rating : trustedReviewers) {
                HBox reviewerBox = new HBox(10);
                reviewerBox.setAlignment(Pos.CENTER_LEFT);

                Label reviewerLabel = new Label(rating.getReviewerUsername() + " - Average Rating: " + String.format("%.2f", rating.getAverageRating()) + " (Weight: " + String.format("%.2f", rating.getWeight()) + ")");
                Button rateButton = new Button("Rate");
                rateButton.setOnAction(e -> showReviewerRatingDialog(rating.getReviewerUsername()));

                reviewerBox.getChildren().addAll(reviewerLabel, rateButton);
                reviewersList.getChildren().add(reviewerBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            reviewersList.getChildren().add(new Label("Failed to load trusted reviewers."));
        }

        section.getChildren().addAll(header, reviewersList);
        return section;
    }

    private void showReviewerRatingDialog(String reviewerUsername) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Rate Reviewer");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Rate Reviewer: " + reviewerUsername);
        titleLabel.setStyle(StyleConstants.HEADER_STYLE);

        Slider ratingSlider = new Slider(0, 5, 0);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setSnapToTicks(true);

        Button submitButton = new Button("Submit Rating");
        submitButton.setOnAction(e -> {
            int rating = (int) ratingSlider.getValue();
            try {
                databaseHelper.addReviewerRating(reviewerUsername, currentUser, rating, reviewerUsername);
                dialog.close();
                refreshTrustedReviewersList(); // Refresh the trusted reviewers list
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to submit rating.");
            }
        });

        layout.getChildren().addAll(titleLabel, ratingSlider, submitButton);
        Scene scene = new Scene(layout, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void refreshTrustedReviewersList() {
        VBox trustedReviewersSection = createTrustedReviewersSection();
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(trustedReviewersSection);
    }
}

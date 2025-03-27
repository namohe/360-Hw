package views;

import helpers.DatabaseHelper;

import models.ReviewFeedback;
import models.Review;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class ReviewsListPage {
    private final DatabaseHelper databaseHelper;
    private final String currentUser;
    
    public ReviewsListPage(DatabaseHelper databaseHelper, String currentUser) {
        this.currentUser = currentUser;
        this.databaseHelper = databaseHelper;
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Reviews List");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox reviewsContainer = new VBox(10);

        try {
            List<Review> reviews = databaseHelper.getAllReviews(); // Fetch all reviews
            for (Review review : reviews) {
                VBox reviewBox = createReviewBox(review); // ✅ Now uses `createReviewBox()`, which includes feedback
                reviewsContainer.getChildren().add(reviewBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new ReviewerHomePage(databaseHelper, currentUser).show(primaryStage));

        layout.getChildren().addAll(titleLabel, reviewsContainer, backButton);

        Scene scene = new Scene(layout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviews List");
        primaryStage.show();
    }

    private void showUpdateReviewDialog(Review review) {
        Stage dialog = new Stage();
        dialog.setTitle("Update Review");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextArea reviewField = new TextArea();
        reviewField.setText(review.getReviewText());
        reviewField.setWrapText(true);

        Button submitButton = new Button("Submit Updated Review");
        submitButton.setOnAction(e -> {
            try {
                String newReviewText = reviewField.getText().trim();
                if (!newReviewText.isEmpty()) {
                    Review updatedReview = new Review(
                        UUID.randomUUID().toString(),
                        review.getAnswerId(),
                        review.getReviewerUsername(),
                        newReviewText,
                        new Timestamp(System.currentTimeMillis())  
                    );
                    databaseHelper.addReview(updatedReview);
                    dialog.close();
                } else {
                    showErrorAlert("Review update cannot be empty.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to update review.");
            }
        });

        layout.getChildren().addAll(new Label("Update Review:"), reviewField, submitButton);
        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }


    private VBox createReviewBox(Review review) {
        VBox reviewBox = new VBox(5);
        reviewBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");

        Label reviewTextLabel = new Label("Review: " + review.getReviewText());
        reviewTextLabel.setWrapText(true);

        Label feedbackHeader = new Label("Student Feedback:");
        feedbackHeader.setStyle("-fx-font-weight: bold;");

        VBox feedbackList = new VBox(5);
        try {
            List<ReviewFeedback> feedbacks = databaseHelper.getFeedbackForReview(review.getReviewId());

            if (feedbacks.isEmpty()) {
                feedbackList.getChildren().add(new Label("No feedback yet."));
            } else {
                for (ReviewFeedback feedback : feedbacks) {
                    Label feedbackLabel = new Label("- " + feedback.getStudentUsername() + ": " + feedback.getFeedbackText());
                    feedbackLabel.setWrapText(true);
                    feedbackList.getChildren().add(feedbackLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

       
        try {
            List<Review> reviewHistory = databaseHelper.getReviewHistory(review.getReviewId());
            if (reviewHistory.size() > 1) {
                Label historyLabel = new Label("Previous Versions:");
                historyLabel.setStyle("-fx-font-weight: bold;");

                VBox historyBox = new VBox(5);
                for (int i = 1; i < reviewHistory.size(); i++) {
                    Label pastReviewLabel = new Label("Version " + (i + 1) + ": " + reviewHistory.get(i).getReviewText());
                    pastReviewLabel.setWrapText(true);
                    historyBox.getChildren().add(pastReviewLabel);
                }

                reviewBox.getChildren().addAll(historyLabel, historyBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button updateButton = new Button("Update Review");
        updateButton.setOnAction(e -> showUpdateReviewDialog(review));

        reviewBox.getChildren().addAll(reviewTextLabel, feedbackHeader, feedbackList, updateButton);
        return reviewBox;
    }
}
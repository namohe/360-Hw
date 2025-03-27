package views;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Answer;
import models.Review;
import models.AnswersList;
import models.PrivateMessage;
import views.ReviewsListPage;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class ReviewerHomePage {
    private final DatabaseHelper databaseHelper;
    private final String currentUser;

    public ReviewerHomePage(DatabaseHelper databaseHelper, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        VBox headerBox = createHeaderSection(primaryStage);
        VBox answersSection = createAnswersSection();

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(answersSection);

        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Dashboard");
        primaryStage.show();
    }

    private VBox createHeaderSection(Stage primaryStage) {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Reviewer Dashboard");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));
        
        // New "View Reviews" button
        Button viewReviewsButton = new Button("View Reviews");
        viewReviewsButton.setOnAction(e -> new ReviewsListPage(databaseHelper, currentUser).show(primaryStage));

        headerBox.getChildren().addAll(welcomeLabel, logoutButton, viewReviewsButton);
        return headerBox;
    }

    private VBox createAnswersSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));

        Label header = new Label("Pending Answers for Review");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        VBox answersList = new VBox(5);
        try {
            AnswersList pendingAnswers = databaseHelper.getAnswersPendingReview(); // Use the correct method
            for (Answer answer : pendingAnswers.searchAnswers("")) { // Use searchAnswers() to get the list
                VBox answerBox = createAnswerBox(answer);
                
                // Move button creation inside loop so "answer" exists
                Button privateMsgButton = new Button("Send Private Feedback");
                privateMsgButton.setOnAction(e -> openPrivateMessageModal(answer.getQuestionId(), answer.getAuthor()));

                Button viewMessagesButton = new Button("View Private Messages");
                viewMessagesButton.setOnAction(e -> showPrivateMessages(answer.getQuestionId()));

                //Add buttons to answerBox
                answerBox.getChildren().addAll(privateMsgButton, viewMessagesButton);
                
                
                Separator divider = new Separator();
                answersList.getChildren().addAll(answerBox, divider);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        section.getChildren().addAll(header, answersList);
        return section;
    }

    

    private VBox createAnswerBox(Answer answer) {
        VBox container = new VBox(5);
        container.setPadding(new Insets(10, 5, 10, 5));
        container.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");

        Label contentLabel = new Label("Answer: " + answer.getContent());
        contentLabel.setWrapText(true);

        Button reviewButton = new Button("Write Review");
        reviewButton.setOnAction(e -> showReviewDialog(answer));

        container.getChildren().addAll(contentLabel, reviewButton);
        return container;
    }
    
    
    
    
    private void openPrivateMessageModal(String questionId, String receiverUsername) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Send Private Feedback");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label instructionLabel = new Label("Enter your feedback for the student:");
        TextArea messageInput = new TextArea();
        messageInput.setPromptText("Type your message...");
        messageInput.setWrapText(true);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String messageContent = messageInput.getText().trim();
            if (!messageContent.isEmpty()) {
                DatabaseHelper.sendPrivateMessage(questionId, currentUser, receiverUsername, messageContent);
                showSuccessAlert("Message sent successfully!");
                dialog.close();
            } else {
                showErrorAlert("Message cannot be empty.");
            }
        });

        layout.getChildren().addAll(instructionLabel, messageInput, sendButton);
        Scene scene = new Scene(layout, 400, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    
    
    private void showPrivateMessages(String questionId) {
        List<PrivateMessage> messages = DatabaseHelper.getPrivateMessages(questionId, currentUser);

        if (messages.isEmpty()) {
            showErrorAlert("No private messages found.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Private Messages");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        for (PrivateMessage msg : messages) {
            Label messageLabel = new Label(msg.getSenderUsername() + ": " + msg.getMessageContent());
            layout.getChildren().add(messageLabel);
        }

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }


    
    

    private void showReviewDialog(Answer answer) {
        Stage dialog = new Stage();
        dialog.setTitle("Write Review");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextArea reviewField = new TextArea();
        reviewField.setPromptText("Write your review here...");
        reviewField.setWrapText(true);

        Button submitButton = new Button("Submit Review");
        submitButton.setOnAction(e -> {
            try {
                String reviewText = reviewField.getText().trim();
                if (!reviewText.isEmpty()) {
                    Review review = new Review(UUID.randomUUID().toString(), answer.getAnswerId(), currentUser, reviewText, new Timestamp(System.currentTimeMillis()));
                    databaseHelper.addReview(review);
                    dialog.close();
                } else {
                    showErrorAlert("Review cannot be empty.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to submit review.");
            }
        });

        layout.getChildren().addAll(new Label("Review for Answer:"), reviewField, submitButton);
        Scene scene = new Scene(layout, 400, 300);
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
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

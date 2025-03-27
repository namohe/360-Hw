package views;

import java.sql.SQLException;


import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import helpers.DatabaseHelper;
import helpers.StyleConstants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Question;
import models.QuestionsList;
import models.Answer;
import models.AnswersList;
import models.Review;
import models.ReviewFeedback;

import models.ReviewerRating;
import models.PrivateMessage;

import models.UserRole;



// TODO: implement update/delete questions and answers, add tests

public class StudentHomePage {
    private final DatabaseHelper databaseHelper;
    private String currentUser;
    private BorderPane mainLayout;
    private VBox questionsListBox;
    private QuestionsList questionsList;
    private TrustedSource trusted;
    public StudentHomePage(DatabaseHelper databaseHelper, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
        this.mainLayout = new BorderPane();
        this.trusted = new TrustedSource();
        }
    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();

        // Header Section with Search
        VBox headerBox = createHeaderSection();
        VBox searchBox = createSearchSection();
        
        // Add "Trusted Reviewers List" button to the header
        Button trustedReviewersButton = createTrustedReviewersButton();
        headerBox.getChildren().add(trustedReviewersButton);
        
        Button createTrustedPeoplebutton = createTrustedPeoplebutton();
        headerBox.getChildren().add(createTrustedPeoplebutton);

        VBox topSection = new VBox(20);
        topSection.getChildren().addAll(headerBox, searchBox);
        mainLayout.setTop(topSection);

        // Questions List Section in the middle
        VBox questionsSection = createQuestionsSection();
        mainLayout.setCenter(questionsSection);
        
        // Trusted Reviewers Reviews Section
        VBox trustedReviewsSection = createTrustedReviewersReviewsSection();
        mainLayout.setRight(trustedReviewsSection);

        // Ask Question Section at the bottom
        VBox askQuestionSection = createAskQuestionSection(primaryStage);
        mainLayout.setBottom(askQuestionSection);

        Scene scene = new Scene(mainLayout, 900, 700); // Increased width to accommodate the trusted reviewers section
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Dashboard");
        primaryStage.show();
    }

    private void refreshQuestionsList() {
        questionsListBox.getChildren().clear();
        try {
            QuestionsList questions = DatabaseHelper.getInstance().getQuestions();
            for (Question question : questions.searchQuestions("")) {
                VBox questionContainer = createQuestionItem(question);
                Separator divider = new Separator();
                questionsListBox.getChildren().addAll(questionContainer, divider);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private VBox createSearchSection() {
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(0, 20, 20, 20));
        searchBox.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setStyle(StyleConstants.FIELD_STYLE);
        searchField.setMaxWidth(400);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshQuestionsList();
        });

        searchBox.getChildren().add(searchField);
        return searchBox;
    }


    private VBox createHeaderSection() {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Student Dashboard");
        welcomeLabel.setStyle(StyleConstants.HEADER_STYLE);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show((Stage) logoutButton.getScene().getWindow()));

        // Request to be made Reviewer button
        Button requestReviewerButton = new Button("Request to be made Reviewer");
        requestReviewerButton.setMaxWidth(Double.MAX_VALUE);
        
        Set<UserRole> roles = databaseHelper.getUserRoles(currentUser );
		if (roles.contains(UserRole.REVIEWER)) {
		    headerBox.getChildren().addAll(welcomeLabel, logoutButton);
		} else {
		    requestReviewerButton.setOnAction(e -> handleReviewerRequest(requestReviewerButton));
		    headerBox.getChildren().addAll(welcomeLabel, logoutButton, requestReviewerButton);
		}
		
        
        return headerBox;
    }

    private VBox createAskQuestionSection(Stage primaryStage) {
        VBox section = new VBox(10);

        Label header = new Label("Ask a Question");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        TextField titleField = new TextField();
        titleField.setPromptText("Question Title");
        titleField.setStyle(StyleConstants.FIELD_STYLE);

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Question Description");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        descriptionField.setStyle(StyleConstants.FIELD_STYLE);

        Button submitButton = new Button("Submit Question");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        
        submitButton.setOnAction(e -> handleQuestionSubmission(titleField, descriptionField, primaryStage));

        section.getChildren().addAll(header, titleField, descriptionField, submitButton);
        return section;
    }
    private VBox createQuestionsSection() {
        VBox section = new VBox(10);

        Label header = new Label("Recent Questions");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        questionsListBox = new VBox(2);
        refreshQuestionsList();
        
        ScrollPane scrollPane = new ScrollPane(questionsListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        section.getChildren().addAll(header, scrollPane);
        return section;
    }
    
    
    
    private VBox createQuestionItem(Question question) {
        VBox container = new VBox(5);
        container.setPadding(new Insets(10, 5, 10, 5));

        // Create horizontal container for title and status indicators
        HBox headerContainer = new HBox(10);
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        
        Hyperlink titleLink = new Hyperlink(question.getTitle());
        titleLink.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLink.setOnAction(e -> showQuestionDetails(question));

        // Add author username
        Label authorLabel = new Label("by " + question.getAuthorUsername());
        authorLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");

        // Add resolution status indicator
        boolean isResolved = question.getResolvedBy() != null;
        Label statusLabel = new Label(isResolved ? "✓ Resolved" : "○ Open");
        statusLabel.setStyle(isResolved ? 
            "-fx-text-fill: #2ecc71; -fx-font-weight: bold;" : 
            "-fx-text-fill: #3498db; -fx-font-weight: bold;");

        // Add reply count indicator
        Label replyCountLabel = new Label();
        try {
            int replyCount = databaseHelper.getAnswersForQuestion(question.getQuestionId()).searchAnswers("").size();
            replyCountLabel.setText("\u2709 " + replyCount);
            replyCountLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        } catch (SQLException e) {
            e.printStackTrace();
            replyCountLabel.setText("\u2709 -");
        }
        
        Button linkButton = new Button("link a question");
        linkButton.setOnAction(e -> createlinkedquestion(question));
        
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerContainer.getChildren().addAll( titleLink, authorLabel, spacer, replyCountLabel, statusLabel, linkButton);
       

        Button privateMsgButton = new Button("Send Private Feedback");
        privateMsgButton.setOnAction(e -> openPrivateMessageModal(question.getQuestionId(), question.getAuthorUsername()));
        headerContainer.getChildren().addAll(privateMsgButton); // Add button to UI

            
        if (question.getAuthorUsername().equals(currentUser)) {
            Button viewMessagesButton = new Button("View Private Messages");
            viewMessagesButton.setOnAction(e -> showPrivateMessages(question.getQuestionId()));
            headerContainer.getChildren().addAll(viewMessagesButton);
        }

        
        Label descLabel = new Label(question.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #666666;");

        // Add tags flow pane
        FlowPane tagsPane = new FlowPane(5, 5);
        tagsPane.setPadding(new Insets(5, 0, 0, 0));
        
        for (String tag : question.getTags()) {
            Label tagLabel = new Label(tag);
            tagLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 2 8; -fx-background-radius: 4;");
            tagsPane.getChildren().add(tagLabel);
        
          
        }
        container.getChildren().addAll(headerContainer, descLabel, tagsPane);
       
        
        return container;
    }
    
    
    
    private void openPrivateMessageModal(String questionId, String receiverUsername) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Send Private Feedback");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label instructionLabel = new Label("Enter your feedback for the question author:");
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



    private void handleQuestionSubmission(TextField titleField, TextArea descriptionField, Stage primaryStage) {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (!title.isEmpty() && !description.isEmpty()) {
            try {
                Question question = new Question(title, description, List.of("general"), currentUser);
                databaseHelper.addQuestion(question);
                titleField.clear();
                descriptionField.clear();
                refreshQuestionsList();
            } catch (SQLException ex) {
                showErrorAlert("Failed to submit question");
                ex.printStackTrace();
            }
        } else {
            showErrorAlert("Please fill in all fields");
        }
    }
    private void createlinkedquestion(Question originalQuestion) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("link a Question");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label headerLabel = new Label("Ask a Question Related to: " + originalQuestion.getTitle());
        headerLabel.setStyle(StyleConstants.HEADER_STYLE);

        TextField titleField = new TextField();
        titleField.setPromptText("Question Title");
        titleField.setStyle(StyleConstants.FIELD_STYLE);

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Question Description");
        descriptionField.setText("linked to question :" + originalQuestion.getDescription() +" - ");
       // descriptionField.setPromptText(" continue after ");
//        descriptionField.setText("id " + originalQuestion.getlinkedQuestionId());
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        descriptionField.setStyle(StyleConstants.FIELD_STYLE);

        Button submitButton = new Button("Submit Related Question");
        submitButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            

            if (!title.isEmpty() && !description.isEmpty()) {
                try {
                   
                    Question linkedquestion = new Question(title, description, List.of("linked"), currentUser);
                    linkedquestion.setlinkedQuestionId(originalQuestion.getQuestionId());
                    databaseHelper.addQuestion(linkedquestion);
                    dialog.close();
                    refreshQuestionsList(); 
                   
                } catch (SQLException ex) {
                    showErrorAlert("Failed to submit question");
                    ex.printStackTrace();
                }
            } else {
                showErrorAlert("Please fill in all fields");
            }
        });
        

        Button BacktoOg = new Button("Jump to original question");
        BacktoOg.setOnAction(e ->{
        	dialog.close();
        	showQuestionDetails(originalQuestion);
        });
        
        layout.getChildren().addAll(headerLabel, titleField, descriptionField, submitButton);
        
        if(originalQuestion.getlinkedQuestionId() != null) {
        	 layout.getChildren().add(BacktoOg);
        }
        
        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void handleReviewerRequest(Button requestButton) {
        try {
            if (databaseHelper.doesReviewerRequestExist(currentUser )) {
                showErrorAlert("You already have a pending request.");
                return;
            }

            String requestId = UUID.randomUUID().toString();
            databaseHelper.addReviewerRequest(requestId, currentUser , "PENDING");

            requestButton.setText("Pending Request...");
            requestButton.setDisable(true);
        } catch (SQLException ex) {
            showErrorAlert("Failed to submit request.");
            ex.printStackTrace();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showQuestionDetails(Question question) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Question Details");
        dialog.setOnHiding(event -> refreshQuestionsList());

        BorderPane mainLayout = new BorderPane();

        // Header section
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        Label titleLabel = new Label(question.getTitle());
        titleLabel.setStyle(StyleConstants.HEADER_STYLE);

        Label descriptionLabel = new Label(question.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: #666666; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 4;");
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        headerBox.getChildren().addAll(titleLabel, descriptionLabel);
        mainLayout.setTop(headerBox);

        // Answers section
        VBox answersBox = new VBox(15);
        answersBox.setPadding(new Insets(20));

        Label answersLabel = new Label("Answers");
        answersLabel.setStyle(StyleConstants.SUBHEADER_STYLE);

        // Add search field for answers
        TextField searchAnswersField = new TextField();
        searchAnswersField.setPromptText("Search answers...");
        searchAnswersField.setStyle(StyleConstants.FIELD_STYLE);
        searchAnswersField.setMaxWidth(400);

        VBox answersListBox = new VBox(2);
        answersListBox.setPadding(new Insets(10));

        searchAnswersField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                AnswersList answers = databaseHelper.getAnswersForQuestion(question.getQuestionId());
                List<Answer> searchResults = answers.searchAnswers(newValue);
                
                answersListBox.getChildren().clear();
                for (Answer answer : searchResults) {
                    VBox answerContainer = createAnswerItem(question, answer);
                    Separator divider = new Separator();
                    answersListBox.getChildren().addAll(answerContainer, divider);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        try {
            AnswersList answers = databaseHelper.getAnswersForQuestion(question.getQuestionId());
            for (Answer answer : answers.searchAnswers("")) {
                VBox answerContainer = createAnswerItem(question, answer);
                Separator divider = new Separator();
                answersListBox.getChildren().addAll(answerContainer, divider);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ScrollPane scrollPane = new ScrollPane(answersListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        answersBox.getChildren().addAll(answersLabel, searchAnswersField, scrollPane);
        mainLayout.setCenter(answersBox);

        // Add answer section
        VBox addAnswerBox = new VBox(10);

        Label addAnswerLabel = new Label("Add Your Answer");
        addAnswerLabel.setStyle(StyleConstants.SUBHEADER_STYLE);

        TextArea newAnswerArea = new TextArea();
        newAnswerArea.setPromptText("Type your answer here");
        newAnswerArea.setWrapText(true);
        newAnswerArea.setPrefRowCount(3);
        newAnswerArea.setStyle(StyleConstants.FIELD_STYLE);

        Button submitAnswerButton = new Button("Submit Answer");
        submitAnswerButton.setMaxWidth(Double.MAX_VALUE);

        submitAnswerButton.setOnAction(e -> {
            String content = newAnswerArea.getText().trim();
            if (!content.isEmpty()) {
                try {
                    Answer answer = new Answer(content, currentUser);
                    databaseHelper.addAnswer(answer, question.getQuestionId());
                    VBox answerContainer = createAnswerItem(question, answer);
                    Separator divider = new Separator();
                    answersListBox.getChildren().addAll(answerContainer, divider);
                    newAnswerArea.clear();
                } catch (SQLException ex) {
                    showErrorAlert("Failed to submit answer");
                    ex.printStackTrace();
                }
            } else {
                showErrorAlert("Please enter an answer");
            }
        });

        addAnswerBox.getChildren().addAll(addAnswerLabel, newAnswerArea, submitAnswerButton);
        mainLayout.setBottom(addAnswerBox);

        Scene dialogScene = new Scene(mainLayout, 600, 700);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private VBox createAnswerItem(Question question, Answer answer) {
        VBox container = new VBox(5);
        container.setPadding(new Insets(10, 5, 10, 5));

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label authorLabel = new Label(answer.getAuthor());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label solutionLabel = new Label("✓ Solution");
        solutionLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        
        Button markSolutionButton = new Button("Mark as Solution");
        markSolutionButton.setVisible(false);
        
        // Show solution label if this answer is marked as solution
        solutionLabel.setVisible(answer.getAnswerId().equals(question.getResolvedBy()));
        
        // Show toggle button only for question author
        if (question.getAuthorUsername().equals(currentUser)) {
            markSolutionButton.setVisible(true);
            markSolutionButton.setText(solutionLabel.isVisible() ? "Unmark Solution" : "Mark as Solution");
        }

        markSolutionButton.setOnAction(e -> {
            try {        
                // If trying to mark a new solution when one exists already
                if (!solutionLabel.isVisible() && question.getResolvedBy() != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Solution Already Exists");
                    alert.setHeaderText(null);
                    alert.setContentText("This question already has a marked solution. Please unmark the existing solution first.");
                    alert.showAndWait();
                    return;
                }

                String newResolvedBy = solutionLabel.isVisible() ? null : answer.getAnswerId();
                question.setResolvedBy(newResolvedBy);
                solutionLabel.setVisible(!solutionLabel.isVisible());
                markSolutionButton.setText(solutionLabel.isVisible() ? "Unmark Solution" : "Mark as Solution");
            } catch (SQLException ex) {
                showErrorAlert("Failed to update solution status");
                ex.printStackTrace();
            }
            refreshQuestionsList();
        });
        
        Button viewReviewsButton = new Button("View Reviews");
        viewReviewsButton.setOnAction(e -> showReviewsDialog(answer));

        headerBox.getChildren().addAll(authorLabel, spacer, solutionLabel, markSolutionButton, viewReviewsButton);

        Label contentLabel = new Label(answer.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #666666;");

        container.getChildren().addAll(headerBox, contentLabel);
        return container;
    }

    private VBox createTrustedReviewersReviewsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));

        Label header = new Label("Latest Reviews from Trusted Reviewers");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        VBox reviewsList = new VBox(5);
        ScrollPane scrollPane = new ScrollPane(reviewsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        section.getChildren().addAll(header, scrollPane);

        // Start a background thread to fetch reviews
        startReviewPolling(reviewsList);

        return section;
    }
    
    
    private void startReviewPolling(VBox reviewsList) {
        Thread reviewPollingThread = new Thread(() -> {
            while (true) {
                try {
                    // Fetch the latest reviews from trusted reviewers
                    List<Review> latestReviews = databaseHelper.getAllTrustedReviews(currentUser ); 

                    // Clear the current list and add the latest reviews at the top
                    Platform.runLater(() -> {
                        reviewsList.getChildren().clear();
                        for (Review review : latestReviews) {
                            Label reviewLabel = new Label(review.getReviewerUsername() + ": " + review.getReviewText());
                            reviewsList.getChildren().add(reviewLabel);
                        }
                    });

                    // Sleep for a specified interval before checking again
                    Thread.sleep(5000); // Check every 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Exit the loop if interrupted
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        reviewPollingThread.setDaemon(true); // Allow the thread to exit when the application closes
        reviewPollingThread.start();
    }
    
    private void showReviewsDialog(Answer answer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Reviews for Answer");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Reviews for Answer: " + answer.getContent());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-weight: bold;");

        VBox reviewsList = new VBox(5);
        try {
            List<Review> reviews = databaseHelper.getReviewsForAnswer(answer.getAnswerId(), currentUser);
            if (reviews.isEmpty()) {
                reviewsList.getChildren().add(new Label("No reviews yet."));
            } else {
                for (Review review : reviews) {
                    VBox reviewBox = new VBox(5);
                    reviewBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");

                    // Reviewer's name
                    Label reviewerLabel = new Label("Reviewer: " + review.getReviewerUsername());
                    reviewerLabel.setStyle("-fx-font-weight: bold;");
                    Label reviewTextLabel = new Label(review.getReviewText());
                    reviewTextLabel.setWrapText(true);

                    // ✅ "Leave Feedback" Button (existing functionality)
                    Button feedbackButton = new Button("Leave Feedback");
                    feedbackButton.setOnAction(e -> showFeedbackDialog(review));

                    // ✅ "Rate Reviewer" Button (new functionality)
                    Button rateReviewerButton = new Button("Rate Reviewer");
                    rateReviewerButton.setOnAction(e -> showReviewerRatingDialog(review.getReviewerUsername(), review.getReviewId()));

                    // ✅ "Add to Trusted Reviewers" Button (new functionality)
                    Button addToTrustedButton = new Button("Add to Trusted Reviewers");
                    addToTrustedButton.setOnAction(e -> {
                    	try {
                            Set<UserRole> roles = databaseHelper.getUserRoles(currentUser);

                            if (roles.contains(UserRole.STUDENT) || roles.contains(UserRole.ADMIN)) { 
                                databaseHelper.addToTrustedReviewers(review.getReviewerUsername(), currentUser);
                                showSuccessAlert("Reviewer added to trusted list!");
                            } else {
                                showErrorAlert("Error, action not valid wiht your privileges.");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showErrorAlert("Failed to add reviewer to trusted list.");
                        }
                    });

                    // Add all buttons to the review box
                    reviewBox.getChildren().addAll(reviewerLabel, reviewTextLabel, feedbackButton, rateReviewerButton, addToTrustedButton);
                    reviewsList.getChildren().add(reviewBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            reviewsList.getChildren().add(new Label("Failed to load reviews."));
        }

        ScrollPane scrollPane = new ScrollPane(reviewsList);
        scrollPane.setFitToWidth(true);

        layout.getChildren().addAll(titleLabel, scrollPane);
        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }
    private void showFeedbackDialog(Review review) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Leave Feedback");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label reviewLabel = new Label("Review by " + review.getReviewerUsername() + ":");
        Label reviewText = new Label(review.getReviewText());
        reviewText.setWrapText(true);

        TextArea feedbackField = new TextArea();
        feedbackField.setPromptText("Write your feedback...");
        feedbackField.setWrapText(true);

        Button submitButton = new Button("Submit Feedback");
        submitButton.setOnAction(e -> {
            try {
                String feedbackText = feedbackField.getText().trim();
                if (!feedbackText.isEmpty()) {
                    ReviewFeedback feedback = new ReviewFeedback(
                        UUID.randomUUID().toString(),
                        review.getReviewId(),
                        currentUser,
                        feedbackText,
                        new Timestamp(System.currentTimeMillis())
                    );
                    databaseHelper.addReviewFeedback(feedback);
                    dialog.close();
                } else {
                    showErrorAlert("Feedback cannot be empty.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to submit feedback.");
            }
        });

        layout.getChildren().addAll(reviewLabel, reviewText, feedbackField, submitButton);
        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void showReviewerRatingDialog(String reviewerUsername, String ratingId) {
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
                // Retrieve the reviewId for the reviewer and the student
                String reviewId = databaseHelper.getReviewIdForReviewer(reviewerUsername);
                if (reviewId != null) {
                    databaseHelper.addReviewerRating(reviewerUsername, currentUser, rating, reviewId);
                    dialog.close();
                } else {
                    showErrorAlert("No review found for this reviewer.");
                }
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
    
    
    private void addToTrustedReviewers(String reviewerUsername) {
        try {
           
            databaseHelper.addToTrustedReviewers(reviewerUsername, currentUser);
            showSuccessAlert("Reviewer added to trusted list!");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to add reviewer to trusted list.");
        }
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showTrustedsources() {
    	
    	Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Trusted Source List");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Trusted List");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> trustedPeopleList = new ListView<>();
        trustedPeopleList.setPrefHeight(200);

        
        trustedPeopleList.setItems(FXCollections.observableArrayList(trusted.gettrustedsources()));

       
        TextField addPersonField = new TextField();
        addPersonField.setPromptText("Enter username to add");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String newPerson = addPersonField.getText().trim();
            if (!newPerson.isEmpty() && !trusted.gettrustedsources().contains(newPerson)) {
                trusted.Addtrusted(newPerson); 
                trustedPeopleList.setItems(FXCollections.observableArrayList(trusted.gettrustedsources())); 
                addPersonField.clear();
            }
        });

        HBox addPersonBox = new HBox(10, addPersonField, addButton);
        addPersonBox.setAlignment(Pos.CENTER_LEFT);

        // Remove trusted person button
        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> {
            String selectedPerson = trustedPeopleList.getSelectionModel().getSelectedItem();
            if (selectedPerson != null) {
                trusted.removetrusted(selectedPerson); // Remove from the trusted list
                trustedPeopleList.setItems(FXCollections.observableArrayList(trusted.gettrustedsources())); // Refresh the list
            }
        });

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());

        // Add components to layout
        layout.getChildren().addAll(titleLabel,trustedPeopleList,addPersonBox,removeButton,closeButton );

        // Scene and stage setup
        Scene scene = new Scene(layout, 300, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private VBox createTrustedPeopleSection() {
        VBox section = new VBox(10);

        Label header = new Label("Trusted People");
        header.setStyle(StyleConstants.SUBHEADER_STYLE);

        // Button to manage trusted people
        Button manageTrustedButton = new Button("Manage Trusted People");
        manageTrustedButton.setMaxWidth(Double.MAX_VALUE);
        manageTrustedButton.setOnAction(e -> showTrustedsources());

        section.getChildren().addAll(header, manageTrustedButton);
        return section;
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

                Label reviewerLabel = new Label(rating.getReviewerUsername() + " - Rating: " + String.format("%.2f", rating.getAverageRating()) + " (Weight: " + String.format("%.2f", rating.getWeight()) + ")");
                Button rateButton = new Button("Update Rating");
                rateButton.setOnAction(e -> showReviewerRatingDialog(rating.getReviewerUsername(), rating.getReviewId()));

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
    
    private Button createTrustedReviewersButton() {
        Button trustedReviewersButton = new Button("Trusted Reviewers List");
        trustedReviewersButton.setOnAction(e -> showTrustedReviewersList());
        return trustedReviewersButton;
    }
    private Button createTrustedPeoplebutton() {
        Button trustedsButton = new Button("Trusted Source List");
        trustedsButton.setOnAction(e -> showTrustedsources());
        return trustedsButton;
    }
    
    private void showTrustedReviewersList() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Trusted Reviewers List");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Trusted Reviewers List");
        titleLabel.setStyle(StyleConstants.HEADER_STYLE);

        VBox reviewersList = new VBox(5);
        try {
            List<ReviewerRating> trustedReviewers = databaseHelper.getTrustedReviewers(currentUser);
            for (ReviewerRating rating : trustedReviewers) {
                HBox reviewerBox = new HBox(10);
                reviewerBox.setAlignment(Pos.CENTER_LEFT);

                // Display reviewer username, average rating, and weight
                Label reviewerLabel = new Label(rating.getReviewerUsername() + 
                    " - Average Rating: " + String.format("%.2f", rating.getAverageRating()) + 
                    " (Weight: " + String.format("%.2f", rating.getWeight()) + ")");

                // "Rate Reviewer" button
                Button rateButton = new Button("Rate Reviewer");
                rateButton.setOnAction(e -> {
                    try {
                        String reviewId = databaseHelper.getReviewIdForReviewer(rating.getReviewerUsername());
                        if (reviewId != null) {
                            showReviewerRatingDialog(rating.getReviewerUsername(), reviewId);
                            refreshTrustedReviewersList(dialog); // Refresh the list after rating
                        } else {
                            showErrorAlert("No review found for this reviewer.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showErrorAlert("Failed to retrieve review ID.");
                    }
                });

                reviewerBox.getChildren().addAll(reviewerLabel, rateButton);
                reviewersList.getChildren().add(reviewerBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            reviewersList.getChildren().add(new Label("Failed to load trusted reviewers."));
        }

        ScrollPane scrollPane = new ScrollPane(reviewersList);
        scrollPane.setFitToWidth(true);

        layout.getChildren().addAll(titleLabel, scrollPane);
        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void refreshTrustedReviewersList(Stage dialog) {
        VBox layout = (VBox) dialog.getScene().getRoot();
        layout.getChildren().clear();

        Label titleLabel = new Label("Trusted Reviewers List");
        titleLabel.setStyle(StyleConstants.HEADER_STYLE);

        VBox reviewersList = new VBox(5);
        try {
            List<ReviewerRating> trustedReviewers = databaseHelper.getTrustedReviewers(currentUser);
            for (ReviewerRating rating : trustedReviewers) {
                HBox reviewerBox = new HBox(10);
                reviewerBox.setAlignment(Pos.CENTER_LEFT);

                Label reviewerLabel = new Label(rating.getReviewerUsername() + 
                    " - Average Rating: " + String.format("%.2f", rating.getAverageRating()) + 
                    " (Weight: " + String.format("%.2f", rating.getWeight()) + ")");

                Button rateButton = new Button("Rate Reviewer");
                rateButton.setOnAction(e -> {
                    showReviewerRatingDialog(rating.getReviewerUsername(), rating.getRatingId());
                    refreshTrustedReviewersList(dialog); // Refresh the list after rating
                });

                reviewerBox.getChildren().addAll(reviewerLabel, rateButton);
                reviewersList.getChildren().add(reviewerBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            reviewersList.getChildren().add(new Label("Failed to load trusted reviewers."));
        }

        ScrollPane scrollPane = new ScrollPane(reviewersList);
        scrollPane.setFitToWidth(true);

        layout.getChildren().addAll(titleLabel, scrollPane);
    }
    
}
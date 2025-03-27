package models;


import java.sql.Timestamp;

/**
 * Represents a way to check feedback on reviewers 
 */
public class ReviewFeedback {
    private String feedbackId;
    private String reviewId;
    private String studentUsername;
    private String feedbackText;
    private Timestamp timestamp;

    public ReviewFeedback(String feedbackId, String reviewId, String studentUsername, String feedbackText, Timestamp timestamp) {
        this.feedbackId = feedbackId;
        this.reviewId = reviewId;
        this.studentUsername = studentUsername;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
    }

    // Getters
    public String getFeedbackId() { return feedbackId; }
    public String getReviewId() { return reviewId; }
    public String getStudentUsername() { return studentUsername; }
    public String getFeedbackText() { return feedbackText; }
    public Timestamp getTimestamp() { return timestamp; }
}

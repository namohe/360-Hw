package models;

import java.sql.Timestamp;
import java.util.UUID;


/**
 * Allows reviewer to leave a review on a question
 */
public class Review {
    private String reviewId;
    private String answerId;
    private String reviewerUsername;
    private String reviewText;
    private Timestamp timestamp;
    private String parentReviewId;

    // New fields to store question title and answer content
    private String questionTitle;
    private String answerContent;

    public Review(String reviewId, String answerId, String reviewerUsername, String reviewText, Timestamp timestamp) {
        this.reviewId = reviewId;
        this.answerId = answerId;
        this.reviewerUsername = reviewerUsername;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }
    
    public Review(String reviewId, String answerId, String reviewerUsername, String reviewText, Timestamp timestamp, String parentReviewId) {
        this.reviewId = reviewId;
        this.answerId = answerId;
        this.reviewerUsername = reviewerUsername;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
        this.parentReviewId = parentReviewId;
    }

    // ✅ Add Getter and Setter for Question Title
    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    // ✅ Add Getter and Setter for Answer Content
    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public String getParentReviewId() {
        return parentReviewId;
    }
}

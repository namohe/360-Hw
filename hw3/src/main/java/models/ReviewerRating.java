package models;


/**
 * Represents a review rating system for the user
 */
public class ReviewerRating {
    private String ratingId;
    private String reviewerUsername;
    private String studentUsername;
    private double averageRating;
    private double weight;
    private String reviewId; // Add this field

    // Updated constructor
    public ReviewerRating(String ratingId, String reviewerUsername, String studentUsername, double averageRating, double weight, String reviewId) {
        this.ratingId = ratingId;
        this.reviewerUsername = reviewerUsername;
        this.studentUsername = studentUsername;
        this.averageRating = averageRating;
        this.weight = weight;
        this.reviewId = reviewId; // Initialize the new field
    }

    // Add getter for reviewId
    public String getReviewId() {
        return reviewId;
    }

    // Existing getters and setters
    public String getRatingId() { return ratingId; }
    public String getReviewerUsername() { return reviewerUsername; }
    public String getStudentUsername() { return studentUsername; }
    public double getAverageRating() { return averageRating; }
    public double getWeight() { return weight; }
}
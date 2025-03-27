package models;

import java.util.Date;
import java.util.UUID;

/**
 * Represents an answer in a Q and A system
 * Contains information about the answer content, author, and associated question
 */
public class Answer {
    // ID of the question this answer belongs to
    private String questionId;
    // Unique identifier for this answer
    private String answerId;
    // The actual text content of the answer
    private String content;
    // Name of the person who wrote the answer
    private String author;
    // When the answer was created
    private Date timestamp;

    /**
     * Creates a new Answer with the given content and author
     * @param content The text of the answer
     * @param author The name of the person writing the answer
     * @throws IllegalArgumentException if content is null or empty
     */
    public Answer(String content, String author) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer content cannot be empty");
        }
        
        this.answerId = UUID.randomUUID().toString();
        this.content = content;
        this.author = author;
        this.timestamp = new Date();
    }

    /**
     * @return The ID of the question this answer belongs to
     */
    public String getQuestionId() {
        return questionId;
    }

    /**
     * @param questionId Sets the question ID this answer belongs to
     */
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    /**
     * @return The unique identifier of this answer
     */
    public String getAnswerId() {
        return answerId;
    }

    /**
     * @param answerId Sets the answer's unique identifier
     */
    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    /**
     * @return The text content of the answer
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the answer content
     * @param content The new text content
     * @throws IllegalArgumentException if content is null or empty
     */
    public void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer content cannot be empty");
        }
        this.content = content;
    }

    /**
     * @return The name of the answer's author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author Sets the name of the answer's author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return The timestamp when this answer was created
     */
    public Date getTimestamp() {
        return timestamp;
    }
    private String questionTitle; // Add this field

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
}
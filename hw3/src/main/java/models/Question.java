package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import helpers.DatabaseHelper;

/**
 * Represents a question in the Q and A system.
 * This class manages question details including title, description, tags, answers and resolution status.
 */
public class Question {
    /** Unique identifier for the question */
    private String questionId;
    /** Title of the question */
    private String title;
    /** Detailed description of the question */
    private String description;
    /** Set of tags associated with the question */
    private Set<String> tags;
    /** List of answers to this question */
    private List<Answer> answers;
    /** Username of the user who resolved the question */
    private String resolvedBy;
    /** Username of the question author */
    private String authorUsername;
    
    private String linkedquestionid;


    /**
     * Creates a new Question with the specified details.
     * 
     * @param title The title of the question
     * @param description The detailed description of the question
     * @param tags List of tags associated with the question
     * @param authorUsername Username of the question author
     */
    public Question(String title, String description, List<String> tags, String authorUsername) {
        this.questionId = UUID.randomUUID().toString();
        this.setTitle(title);
        this.setDescription(description);
        this.tags = new HashSet<>();
        tags.forEach(tag -> this.tags.add(tag.toLowerCase()));
        this.answers = new ArrayList<>();
        this.resolvedBy = null;
        this.authorUsername = authorUsername;
        this.linkedquestionid = questionId;
    }

    /**
     * Gets the username of the question author.
     * @return The author's username
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * Sets the username of the question author.
     * @param authorUsername The author's username to set
     */
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    /**
     * Gets the unique identifier of the question.
     * @return The question ID
     */
    public String getQuestionId() {
        return questionId;
    }

    /**
     * Sets the question's unique identifier.
     * @param questionId The question ID to set
     */
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getlinkedQuestionId() {
        return linkedquestionid;
    }

    /**
     * Sets the question's unique identifier.
     * @param questionId The question ID to set
     */
    public void setlinkedQuestionId(String linkedquestionid) {
        this.linkedquestionid = linkedquestionid;
    }


    /**
     * Gets the title of the question.
     * @return The question title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the question.
     * @param title The title to set
     * @throws IllegalArgumentException if title is null or empty
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title;
    }

    /**
     * Gets the description of the question.
     * @return The question description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the question.
     * @param description The description to set
     * @throws IllegalArgumentException if description is null or empty
     */
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    /**
     * Gets the set of tags associated with the question.
     * @return Set of tags
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Sets the tags for the question.
     * @param tags List of tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = new HashSet<>();
        tags.forEach(tag -> this.tags.add(tag.toLowerCase()));
    }

    /**
     * Adds a single tag to the question.
     * @param tag The tag to add
     */
    public void addTag(String tag) {
        this.tags.add(tag.toLowerCase());
    }

    /**
     * Gets the list of answers for this question.
     * @return List of answers
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    /**
     * Adds an answer to the question.
     * @param answer The answer to add
     */
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    /**
     * Gets the username of who resolved the question.
     * @return Username of resolver
     */
    public String getResolvedBy() {
        return resolvedBy;
    }

    /**
     * Sets the resolver of the question and updates the database.
     * @param resolvedBy Username of the resolver
     * @throws SQLException if database update fails
     */
    public void setResolvedBy(String resolvedBy) throws SQLException {
        this.resolvedBy = resolvedBy;
        DatabaseHelper.getInstance().updateQuestionResolved(this.questionId, resolvedBy);
    }
    
    public List<PrivateMessage> getPrivateMessages(String receiverUsername) {
        return DatabaseHelper.getPrivateMessages(this.questionId, receiverUsername);
    }
    
}
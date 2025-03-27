package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of Question objects and provides operations for manipulating questions.
 */
public class QuestionsList {
    /** List to store Question objects */
    private List<Question> questions;

    /**
     * Constructs a new QuestionsList with an empty ArrayList.
     */
    public QuestionsList() {
        this.questions = new ArrayList<>();
    }

    /**
     * Adds a new question to the list.
     * @param question The Question object to be added
     */
    public void addQuestion(Question question) {
        questions.add(question);
    }

    /**
     * Removes a question from the list based on its ID.
     * @param questionId The unique identifier of the question to remove
     */
    public void removeQuestion(String questionId) {
        questions.removeIf(q -> q.getQuestionId().equals(questionId));
    }

    /**
     * Retrieves a specific question by its ID.
     * @param questionId The unique identifier of the question to retrieve
     * @return The Question object if found, null otherwise
     */
    public Question getQuestion(String questionId) {
        for (Question q : questions) {
            if (q.getQuestionId().equals(questionId)) {
                return q;
            }
        }
        return null;
    }

    /**
     * Searches for questions matching a query string in title, description, or tags.
     * @param query The search string to match against questions
     * @return List of Question objects matching the search criteria. Returns all questions if query is empty
     */
    public List<Question> searchQuestions(String query) {
        if (query.isEmpty()) {
            return this.questions;
        }
        List<Question> results = new ArrayList<>();
        for (Question q : questions) {
            if (q.getTitle().contains(query) || q.getDescription().contains(query) || q.getTags().contains(query)) {
                results.add(q);
            }
        }
        return results;
    }
}
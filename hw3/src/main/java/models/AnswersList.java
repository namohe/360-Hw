package models;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that manages a collection of Answer objects.
 * This class provides functionality to add, remove, retrieve and search answers.
 */
public class AnswersList {
    /** List to store Answer objects */
    private List<Answer> answers;

    /**
     * Constructor that initializes an empty list of answers
     */
    public AnswersList() {
        this.answers = new ArrayList<>();
    }

    /**
     * Adds a new answer to the list
     * @param answer The Answer object to be added
     */
    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    /**
     * Removes an answer from the list based on its ID
     * @param answerId The unique identifier of the answer to remove
     */
    public void removeAnswer(String answerId) {
        answers.removeIf(a -> a.getAnswerId().equals(answerId));
    }

    /**
     * Retrieves a specific answer by its ID
     * @param answerId The unique identifier of the answer to retrieve
     * @return The Answer object if found, null otherwise
     */
    public Answer getAnswer(String answerId) {
        for (Answer a : answers) {
            if (a.getAnswerId().equals(answerId)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Searches for answers based on content or author
     * @param query The search term to match against answer content or author
     * @return List of Answer objects that match the search criteria. Returns all answers if query is empty
     */
    public List<Answer> searchAnswers(String query) {
        if (query.isEmpty()) {
            return this.answers;
        }
        List<Answer> results = new ArrayList<>();
        for (Answer a : answers) {
            if (a.getContent().contains(query) || a.getAuthor().contains(query)) {
                results.add(a);
            }
        }
        return results;
    }
}
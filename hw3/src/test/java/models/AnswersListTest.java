package models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * A class that manages a collection of Question objects.
 */
public class AnswersListTest {
    private AnswersList answersList;
    

    /**
     * Sets up a fresh answer list instance before each test
     */
    @Before
    public void setUp() {
        answersList = new AnswersList();
    }
    /**
     * Tests retrieving all answers when questions exist in the list
     * Verifies that answers can be added and retrieved correctly
     */
    @Test
    public void testViewAllAnswersPositive() {
        // Setup test data
        Answer answer1 = new Answer("First answer", "John");
        Answer answer2 = new Answer("Second answer", "Jane");
        answersList.addAnswer(answer1);
        answersList.addAnswer(answer2);

        // Test searching with empty query returns all answers
        List<Answer> results = answersList.searchAnswers("");
        
        assertEquals(2, results.size());
        assertTrue(results.contains(answer1));
        assertTrue(results.contains(answer2));
    }
    /**
     * Tests retrieving all answers when the list is empty
     * Verifies empty list handling
     */
    @Test
    public void testViewAllAnswersNegative() {
        // Test empty answers list
        List<Answer> results = answersList.searchAnswers("");
        
        assertEquals(0, results.size());
    }

    /**
     * Tests deleting and adding a new answer
     * Verifies that its added or deleted
     */
    @Test
    public void testAddAndRemoveAnswer() {
        Answer answer = new Answer("Test answer", "TestUser");
        answersList.addAnswer(answer);
        
        // Verify answer was added
        List<Answer> results = answersList.searchAnswers("");
        assertEquals(1, results.size());
        
        // Remove answer
        answersList.removeAnswer(answer.getAnswerId());
        
        // Verify answer was removed
        results = answersList.searchAnswers("");
        assertEquals(0, results.size());
    }
   
}

package models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for QuestionsList functionality
 * Tests the management and searching of questions in the Q and A system
 */
public class QuestionsListTest {
    private QuestionsList questionsList;

    /**
     * Sets up a fresh QuestionsList instance before each test
     */
    @Before
    public void setUp() {
        questionsList = new QuestionsList();
    }

    /**
     * Tests retrieving all questions when questions exist in the list
     * Verifies that multiple questions can be added and retrieved correctly
     */
    @Test
    public void testViewAllQuestionsPositive() {
        // Create and add sample questions
        Question q1 = new Question("Java Basics", "How to declare variables?", Arrays.asList("java", "basics"), "user1");
        Question q2 = new Question("Python Lists", "List comprehension help", Arrays.asList("python", "lists"), "user2");
        
        questionsList.addQuestion(q1);
        questionsList.addQuestion(q2);

        // Test viewing all questions with empty search query
        List<Question> allQuestions = questionsList.searchQuestions("");
        
        assertEquals(2, allQuestions.size());
        assertTrue(allQuestions.contains(q1));
        assertTrue(allQuestions.contains(q2));
    }

    /**
     * Tests retrieving all questions when the list is empty
     * Verifies empty list handling
     */
    @Test
    public void testViewAllQuestionsNegative() {
        List<Question> allQuestions = questionsList.searchQuestions("");
        assertTrue(allQuestions.isEmpty());
    }

}
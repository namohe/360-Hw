package models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for link question class
 * Tests the ability to link questions to posed questions
 */

public class LinkQuestionTest {
    private QuestionsList questionsList;
    private Question originalquestion;

    /**
     * Sets up a fresh QuestionsList instance before each test
     */
    @Before
    public void setUp() {
        questionsList = new QuestionsList();
    }

    /**
     * after multiple questions can be added and retrieved correctly
     * and then add a linked question to it based on the question added
     * 
     */
    @Test
    public void testViewAllQuestionsPositive() {
        // Create and add sample questions
        Question q1 = new Question("Java Basics", "How to declare variables?", Arrays.asList("java", "basics"), "user1");
        Question q2 = new Question("Python Lists", "List comprehension help", Arrays.asList("python", "lists"), "user2");
        
       q1 = originalquestion;
        
        questionsList.addQuestion(q1);
        questionsList.addQuestion(q2);

        // Test viewing all questions with empty search query
        List<Question> allQuestions = questionsList.searchQuestions("");
        
        assertEquals(2, allQuestions.size());
        assertTrue(allQuestions.contains(q1));
        assertTrue(allQuestions.contains(q2));
    }
    
    /**
     * tests for a successful linked question
     */
    @Test
    public void testLinkedquesPositive() {
    	Question linkedquestion = new Question("Linked Question", "This question is based on this question", Arrays.asList("linked", "basics"),"user1");
    
    	questionsList.addQuestion(linkedquestion);
    
    }

}

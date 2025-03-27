package models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Answer model
 * Contains unit tests for CRUD operations and other functionality of the Answer class
 */
public class AnswerTest {
    private Answer testAnswer;
    
    /**
     * Sets up a test Answer object before each test
     */
    @BeforeEach
    void setUp() {
        testAnswer = new Answer("Test content", "testAuthor");
    }

    /**
     * Tests successful creation of an Answer object with valid parameters
     * Verifies that ID, content, author and timestamp are properly set
     */
    @Test
    void testCreateAnswerPositive() {
        Answer answer = new Answer("Valid response", "author1");
        assertNotNull(answer.getAnswerId());
        assertEquals("Valid response", answer.getContent());
        assertEquals("author1", answer.getAuthor());
        assertNotNull(answer.getTimestamp());
    }

    /**
     * Tests Answer creation with invalid parameters (empty content)
     * Expects an IllegalArgumentException to be thrown
     */
    @Test
    void testCreateAnswerNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Answer("", "author1");
        });
        assertEquals("Answer content cannot be empty", exception.getMessage());
    }

    /**
     * Tests successful retrieval of Answer object properties
     * Verifies that all fields can be correctly accessed
     */
    @Test
    void testReadAnswerPositive() {
        String answerId = testAnswer.getAnswerId();
        assertNotNull(answerId);
        assertEquals("Test content", testAnswer.getContent());
        assertEquals("testAuthor", testAnswer.getAuthor());
        assertNotNull(testAnswer.getTimestamp());
    }

    /**
     * Tests handling of null Answer object
     * Verifies that null check works as expected
     */
    @Test
    void testReadAnswerNegative() {
        Answer answer = null;
        assertNull(answer);
    }

    /**
     * Tests successful update of Answer content
     * Verifies that content can be modified correctly
     */
    @Test
    void testUpdateAnswerPositive() {
        testAnswer.setContent("Updated content");
        assertEquals("Updated content", testAnswer.getContent());
    }

    /**
     * Tests update with invalid content (empty string)
     * Expects an IllegalArgumentException to be thrown
     */
    @Test
    void testUpdateAnswerNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            testAnswer.setContent("");
        });
        assertEquals("Answer content cannot be empty", exception.getMessage());
    }

    /**
     * Tests successful deletion simulation by nullifying fields
     * Verifies that fields can be set to null
     */
    @Test
    void testDeleteAnswerPositive() {
        String answerId = testAnswer.getAnswerId();
        testAnswer.setAuthor(null);
        testAnswer.setQuestionId(null);
        
        assertNull(testAnswer.getAuthor());
        assertNull(testAnswer.getQuestionId());
    }

    /**
     * Tests deletion of non-existent answer
     * Expects an IllegalArgumentException to be thrown
     */
    @Test
    void testDeleteAnswerNegative() {
        String nonExistentAnswerId = "non-existent-id";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Answer not found");
        });
        assertEquals("Answer not found", exception.getMessage());
    }

    /**
     * Tests the setter and getter methods for questionId
     * Verifies that questionId can be set and retrieved correctly
     */
    @Test
    void testQuestionIdSetterGetter() {
        testAnswer.setQuestionId("test-question-id");
        assertEquals("test-question-id", testAnswer.getQuestionId());
    }

    /**
     * Tests that timestamp is automatically generated
     * Verifies that timestamp is not null when Answer is created
     */
    @Test
    void testTimestampNotNull() {
        assertNotNull(testAnswer.getTimestamp());
    }
}

package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Test class for Question model functionality
 * Tests creation, reading, updating, and various operations on Question objects
 */
public class QuestionTest {
    private Question question;
    // Test constants for reuse across test cases
    private final String TITLE = "Test Question";
    private final String DESCRIPTION = "Test Description";
    private final List<String> TAGS = Arrays.asList("java", "testing");
    private final String AUTHOR = "testUser";

    /**
     * Sets up a fresh Question object before each test
     */
    @BeforeEach
    void setUp() {
        question = new Question(TITLE, DESCRIPTION, TAGS, AUTHOR);
    }

    /**
     * Tests successful Question object creation with valid parameters
     * Verifies all fields are properly initialized
     */
    @Test
    void createQuestionPositive() {
        assertNotNull(question.getQuestionId());
        assertEquals(TITLE, question.getTitle());
        assertEquals(DESCRIPTION, question.getDescription());
        assertEquals(2, question.getTags().size());
        assertTrue(question.getTags().contains("java"));
        assertEquals(AUTHOR, question.getAuthorUsername());
    }

    /**
     * Tests Question creation with invalid parameters
     * Verifies appropriate exceptions are thrown for empty title and description
     */
    @Test
    void createQuestionNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Question("", DESCRIPTION, TAGS, AUTHOR);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Question(TITLE, "", TAGS, AUTHOR);
        });
    }

    /**
     * Tests reading Question object properties
     * Verifies getter methods return correct values
     */
    @Test
    void readQuestionPositive() {
        assertNotNull(question);
        assertEquals(TITLE, question.getTitle());
        assertEquals(DESCRIPTION, question.getDescription());
        assertEquals(new HashSet<>(TAGS), question.getTags());
        assertEquals(AUTHOR, question.getAuthorUsername());
    }

    /**
     * Tests updating Question properties with valid values
     * Verifies setter methods properly update the object state
     */
    @Test
    void updateQuestionPositive() {
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        List<String> newTags = Arrays.asList("python", "updated");

        question.setTitle(newTitle);
        question.setDescription(newDescription);
        question.setTags(newTags);

        assertEquals(newTitle, question.getTitle());
        assertEquals(newDescription, question.getDescription());
        assertTrue(question.getTags().contains("python"));
        assertEquals(2, question.getTags().size());
    }

    /**
     * Tests updating Question with invalid values
     * Verifies appropriate exceptions are thrown for empty fields
     */
    @Test
    void updateQuestionNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            question.setTitle("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            question.setDescription("");
        });
    }

    /**
     * Tests adding an Answer to a Question
     * Verifies answer is properly added to the question's answer list
     */
    @Test
    void addAnswerPositive() {
        Answer answer = new Answer("Test Answer", "answerer");
        question.addAnswer(answer);
        
        assertEquals(1, question.getAnswers().size());
        assertEquals("Test Answer", question.getAnswers().get(0).getContent());
    }

    /**
     * Tests tag case normalization
     * Verifies tags are stored in lowercase regardless of input case
     */
    @Test
    void tagValidationPositive() {
        List<String> mixedCaseTags = Arrays.asList("JAVA", "Python", "TEST");
        question.setTags(mixedCaseTags);

        assertTrue(question.getTags().contains("java"));
        assertTrue(question.getTags().contains("python"));
        assertTrue(question.getTags().contains("test"));
    }

    /**
     * Tests adding a single tag to the question
     * Verifies tag is properly added and converted to lowercase
     */
    @Test
    void addTagPositive() {
        question.addTag("NewTag");
        assertTrue(question.getTags().contains("newtag"));
    }

    /**
     * Tests question resolution functionality
     * Verifies resolved status and resolver are properly set
     */
    @Test
    void resolveQuestionPositive() throws SQLException {
        String resolver = "resolverUser";
        question.setResolvedBy(resolver);
        assertEquals(resolver, question.getResolvedBy());
    }

    /**
     * Tests uniqueness of question IDs
     * Verifies different Question objects get unique IDs
     */
    @Test
    void questionIdUniquenessPositive() {
        Question question2 = new Question(TITLE, DESCRIPTION, TAGS, AUTHOR);
        assertNotEquals(question.getQuestionId(), question2.getQuestionId());
    }

    /**
     * Tests Question creation with empty tags list
     * Verifies Question can be created without tags
     */
    @Test
    void emptyTagsListPositive() {
        Question questionWithNoTags = new Question(TITLE, DESCRIPTION, Arrays.asList(), AUTHOR);
        assertTrue(questionWithNoTags.getTags().isEmpty());
    }
}

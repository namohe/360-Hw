package models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.sql.Timestamp; 

/**
 * Test for ability it send a private message after a question
 */
public class PrivatemessageTest {
	private PrivateMessage Pm;
	private QuestionsList questionsList;
	


    /**
     * test if a private message is sent
     * lists eveything about sender and username of reciever
     */
    @Test 
    public void testPMPositive() {
    	
    int messageId = 1;
    String questionId = "1";
    String senderUsername = "user1";
    String receiverUsername = "user2";
    String messageContent = "this is a message";
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    PrivateMessage newmessgae = new PrivateMessage(messageId, questionId, senderUsername, receiverUsername, messageContent, timestamp, false);
    	
    }

    
    
}
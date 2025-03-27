package models;

import java.util.Date; 
import java.sql.Timestamp; 


/**
 * Represents a messaging system form a posed question.
 */
public class PrivateMessage {
    private int messageId;
    private String questionId;
    private String senderUsername;
    private String receiverUsername;
    private String messageContent;
    private Timestamp timestamp;
    private boolean isRead;

    // Correct Constructor
    public PrivateMessage(int messageId, String questionId, String senderUsername, String receiverUsername,
                          String messageContent, Timestamp timestamp, boolean isRead) {
        this.messageId = messageId;
        this.questionId = questionId;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getter Methods
    public int getMessageId() {
        return messageId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }
}


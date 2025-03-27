package helpers;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import models.Answer;
import models.AnswersList;
import models.Question;
import models.QuestionsList;
import models.User;
import models.UserRole;
import models.Review;
import models.ReviewFeedback;
import models.ReviewerRating;
import models.ReviewerRequestStatus;
import models.PrivateMessage;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {
	private static DatabaseHelper instance;

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt
	
	private DatabaseHelper() {}

	public static DatabaseHelper getInstance() {
			if (instance == null) {
					instance = new DatabaseHelper();
					try {
						instance.connectToDatabase();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return instance;
	}

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "roles VARCHAR(255), "
				+ "oneTimePassword VARCHAR(255) DEFAULT NULL, "
				+ "lastActiveDate DATE DEFAULT CURRENT_DATE,"
				+ "isReviewer BOOLEAN DEFAULT FALSE)";
		statement.execute(userTable);
		
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE, "
				+ "roles VARCHAR(255), "
				+ "expirationDate DATE, "
				+ "createdBy VARCHAR(255))";
		statement.execute(invitationCodesTable);

    String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
            + "questionId VARCHAR(255) PRIMARY KEY, "
            + "title VARCHAR(255), "
            + "description TEXT, "
            + "tags VARCHAR(255), "
            + "resolvedBy VARCHAR(255), "
            + "authorUsername VARCHAR(255), "
            + "FOREIGN KEY (authorUsername) REFERENCES cse360users(userName))";
    statement.execute(questionsTable);

    String answersTable = "CREATE TABLE IF NOT EXISTS answers ("
            + "answerId VARCHAR(255) PRIMARY KEY, "
            + "content TEXT, "
            + "author VARCHAR(255), "
            + "timestamp TIMESTAMP, "
            + "questionId VARCHAR(255), "
            + "FOREIGN KEY (questionId) REFERENCES questions(questionId))";
    statement.execute(answersTable);
    
    String reviews = "CREATE TABLE IF NOT EXISTS reviews (" +
    	    "reviewId VARCHAR(255) PRIMARY KEY, " +
    	    "answerId VARCHAR(255), " +
    	    "reviewerUsername VARCHAR(255), " +
    	    "reviewText TEXT, " +
    	    "timestamp TIMESTAMP, " +
    	    "parentReviewId VARCHAR(255), " +  
    	    "FOREIGN KEY (answerId) REFERENCES answers(answerId), " +  
    	    "FOREIGN KEY (parentReviewId) REFERENCES reviews(reviewId) ON DELETE CASCADE)";  
    	statement.execute(reviews);
    
    String reviewFeedbackTable = "CREATE TABLE IF NOT EXISTS review_feedback (" +
    	    "feedbackId VARCHAR(255) PRIMARY KEY, " +
    	    "reviewId VARCHAR(255), " +
    	    "studentUsername VARCHAR(255), " +
    	    "feedbackText TEXT, " +
    	    "timestamp TIMESTAMP, " +
    	    "parentReviewId VARCHAR(255), " +
    	    "FOREIGN KEY (reviewId) REFERENCES reviews(reviewId) ON DELETE CASCADE)";
    	statement.execute(reviewFeedbackTable);


    
    	String reviewerRatingsTable = "CREATE TABLE IF NOT EXISTS reviewer_ratings ("
    		    + "ratingId VARCHAR(255) PRIMARY KEY, "
    		    + "reviewerUsername VARCHAR(255), "
    		    + "studentUsername VARCHAR(255), "
    		    + "totalRatings INT DEFAULT 0, " // Sum of all ratings
    		    + "numRatings INT DEFAULT 0, " // Number of ratings
    		    + "averageRating DOUBLE DEFAULT 0.0, " // Average rating
    		    + "weight DOUBLE DEFAULT 0.0, " // Calculated weight
    		    + "reviewId VARCHAR(255), " // Add reviewId column
    		    + "FOREIGN KEY (reviewerUsername) REFERENCES cse360users(userName), "
    		    + "FOREIGN KEY (studentUsername) REFERENCES cse360users(userName), "
    		    + "FOREIGN KEY (reviewId) REFERENCES reviews(reviewId))"; // Add foreign key for reviewId
    		statement.execute(reviewerRatingsTable);
    

    	String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS trusted_reviewers ("
    			+ "id INT AUTO_INCREMENT PRIMARY KEY, "
    			+ "reviewerUsername VARCHAR(255), "
    			+ "studentUsername VARCHAR(255), "
    			+ "FOREIGN KEY (reviewerUsername) REFERENCES cse360users(userName), "
    			+ "FOREIGN KEY (studentUsername) REFERENCES cse360users(userName))";
    		statement.execute(trustedReviewersTable);
    
    String reviewerRequestsTable = "CREATE TABLE IF NOT EXISTS reviewer_requests ("
    	    + "requestId VARCHAR(255) PRIMARY KEY, "
    	    + "studentUsername VARCHAR(255), "
    	    + "status VARCHAR(20), " // "PENDING", "ACCEPTED", "DENIED"
    	    + "FOREIGN KEY (studentUsername) REFERENCES cse360users(userName))";
    	statement.execute(reviewerRequestsTable);
    	
    String privateMessagesTable = "CREATE TABLE IF NOT EXISTS private_messages ("
    		+ "messageId INT AUTO_INCREMENT PRIMARY KEY, "
    		+ "questionId VARCHAR(255), "
    		+ "senderUsername VARCHAR(255), "
    		+ "receiverUsername VARCHAR(255), "
    		+ "messageContent TEXT, "
    		+ "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
    		+ "isRead BOOLEAN DEFAULT FALSE, "
    		+ "FOREIGN KEY (questionId) REFERENCES questions(questionId), "
    		+ "FOREIGN KEY (senderUsername) REFERENCES cse360users(userName), "
    		+ "FOREIGN KEY (receiverUsername) REFERENCES cse360users(userName)"
    		+ ");";
    	statement.execute(privateMessagesTable);



    // Add the resolvedBy foreign key after both tables exist
    String alterQuestionsTable = "ALTER TABLE questions ADD FOREIGN KEY (resolvedBy) REFERENCES answers(answerId)";
    statement.execute(alterQuestionsTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, roles) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRolesAsString());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT roles FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String dbRoles = rs.getString("roles");
					// Check if any of the user's roles match the stored roles
					for (UserRole role : user.getRoles()) {
						if (dbRoles.contains(role.toString())) {
							return true;
						}
					}
				}
				return false;
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the roles of a user from the database using their UserName.
	public Set<UserRole> getUserRoles(String userName) {
		String query = "SELECT roles FROM cse360users WHERE userName = ?";
		Set<UserRole> roles = new HashSet<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				String rolesStr = rs.getString("roles");
				Set<UserRole> roleArray = User.getRolesFromString(rolesStr);
				for (UserRole role : roleArray) {
					roles.add(role);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roles;
	}
	
	public List<User> getUserList() throws SQLException {
		List<User> users = new ArrayList<>();
		String query = "SELECT userName, roles, lastActiveDate FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		
		while (resultSet.next()) {
			String userName = resultSet.getString("userName");
			String rolesStr = resultSet.getString("roles");
			User user = new User(userName, "");
			
			// Add all roles from the database
			Set<UserRole> roleArray = User.getRolesFromString(rolesStr);
			for (UserRole role : roleArray) {
				user.addRole(role);
			}
			
			java.sql.Date lastActiveDate = resultSet.getDate("lastActiveDate");
			if (lastActiveDate != null) {
				user.setLastActiveDate(lastActiveDate.toLocalDate());
			}
			users.add(user);
		}
		return users;
	}

	public String generateInvitationCode(Set<UserRole> roles, LocalDate expirationDate, String adminUsername) {
		String code = UUID.randomUUID().toString().substring(0, 4);
		String query = "INSERT INTO InvitationCodes (code, roles, expirationDate, createdBy) VALUES (?, ?, ?, ?)";
	
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
						pstmt.setString(1, code);
						pstmt.setString(2, roles.stream()
										.map(UserRole::getValue)
										.collect(java.util.stream.Collectors.joining(",")));
						pstmt.setDate(3, java.sql.Date.valueOf(expirationDate));
						pstmt.setString(4, adminUsername);
						pstmt.executeUpdate();
		} catch (SQLException e) {
						e.printStackTrace();
		}
		return code;
		}
			
		public User validateInvitationCode(String code, String username, String password) {
			String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND expirationDate >= CURRENT_DATE";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
							pstmt.setString(1, code);
							ResultSet rs = pstmt.executeQuery();
							if (rs.next()) {
								String dbRoles = rs.getString("roles");
								Set<UserRole> roleArray = User.getRolesFromString(dbRoles);
								User user = new User(username, password);
								for (UserRole role : roleArray) {
									user.addRole(role);
								}
								register(user);
								markInvitationCodeAsUsed(code);
								return user;
							}
			} catch (SQLException e) {
							e.printStackTrace();
			}
			return null;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	        System.out.println("Marking invite code as used: " + code);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	public void setOneTimePassword(String username, String oneTimePassword) throws SQLException {
	    String query = "UPDATE cse360users SET oneTimePassword = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, oneTimePassword);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    }
	}
	public boolean validateOneTimePassword(String username, String oneTimePassword) throws SQLException {
					String query = "SELECT oneTimePassword FROM cse360users WHERE userName = ?";
					try (PreparedStatement pstmt = connection.prepareStatement(query)) {
									pstmt.setString(1, username);
									ResultSet rs = pstmt.executeQuery();
						
									if (rs.next()) {
													return oneTimePassword.equals(rs.getString("oneTimePassword"));
									}
					}
					return false;
	}
		
	public void clearOneTimePassword(String username) throws SQLException {
					setOneTimePassword(username, null);
	}
	public void updatePassword(String username, String newPassword) throws SQLException {
	    String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    }
	}
	
	public void deleteUser(String userName) throws SQLException {
	    String query = "DELETE FROM cse360users WHERE userName = ?";
	    PreparedStatement preparedStatement = connection.prepareStatement(query);
	    preparedStatement.setString(1, userName);
	    preparedStatement.executeUpdate();
	}

	public void updateUserRoles(String userName, Set<UserRole> newRoles) throws SQLException {
    // First check if this would remove the last admin
    if (!newRoles.contains(UserRole.ADMIN)) {
        // Count remaining admins
        String countQuery = "SELECT COUNT(*) FROM cse360users WHERE roles LIKE '%ADMIN%' AND userName != ?";
        try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
            countStmt.setString(1, userName);
            ResultSet rs = countStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("Cannot remove last admin role");
            }
        }
    }

    String query = "UPDATE cse360users SET roles = ? WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        String rolesStr = newRoles.stream()
                .map(UserRole::getValue)
                .collect(java.util.stream.Collectors.joining(","));
        pstmt.setString(1, rolesStr);
        pstmt.setString(2, userName);
        pstmt.executeUpdate();
    }
	}

	public void addQuestion(Question question) throws SQLException {
			String insertQuestion = "INSERT INTO questions (questionId, title, description, tags, resolvedBy, authorUsername) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
					pstmt.setString(1, question.getQuestionId());
					pstmt.setString(2, question.getTitle());
					pstmt.setString(3, question.getDescription());
					pstmt.setString(4, String.join(",", question.getTags()));
					pstmt.setString(5, question.getResolvedBy());
					pstmt.setString(6, question.getAuthorUsername());
					pstmt.executeUpdate();
			}
	}

	public void addAnswer(Answer answer, String questionId) throws SQLException {
			String insertAnswer = "INSERT INTO answers (answerId, content, author, timestamp, questionId) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer)) {
					pstmt.setString(1, answer.getAnswerId());
					pstmt.setString(2, answer.getContent());
					pstmt.setString(3, answer.getAuthor());
					pstmt.setTimestamp(4, new Timestamp(answer.getTimestamp().getTime()));
					pstmt.setString(5, questionId);
					pstmt.executeUpdate();
			}
	}

	public QuestionsList getQuestions() throws SQLException {
			QuestionsList questions = new QuestionsList();
			String query = "SELECT * FROM questions";
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
					String questionId = resultSet.getString("questionId");
					String title = resultSet.getString("title");
					String description = resultSet.getString("description");
					String authorUsername = resultSet.getString("authorUsername");
					String resolvedBy = resultSet.getString("resolvedBy");
					List<String> tags = List.of(resultSet.getString("tags").split(","));
					Question question = new Question(title, description, tags, authorUsername);
					question.setQuestionId(questionId);
					question.setResolvedBy(resolvedBy);
					questions.addQuestion(question);
			}
			return questions;
	}

	public AnswersList getAnswersForQuestion(String questionId) throws SQLException {
			AnswersList answers = new AnswersList();
			String query = "SELECT * FROM answers WHERE questionId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, questionId);
					ResultSet resultSet = pstmt.executeQuery();
					while (resultSet.next()) {
							String answerId = resultSet.getString("answerId");
							String content = resultSet.getString("content");
							String author = resultSet.getString("author");
							Timestamp timestamp = resultSet.getTimestamp("timestamp");
							Answer answer = new Answer(content, author);
							answer.setAnswerId(answerId);
							answer.setQuestionId(questionId);
							answers.addAnswer(answer);
					}
			}
			return answers;
	}
	
	public AnswersList getAnswersPendingReview() throws SQLException {
	    AnswersList answers = new AnswersList();
	    String query = "SELECT a.answerId, a.content, a.author, a.timestamp, q.title, q.questionId " +
	               "FROM answers a " +
	               "JOIN questions q ON a.questionId = q.questionId " +
	               "LEFT JOIN reviews r ON a.answerId = r.answerId " +
	               "WHERE r.answerId IS NULL";


	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet resultSet = pstmt.executeQuery()) {
	        while (resultSet.next()) {
	            String answerId = resultSet.getString("answerId");
	            String content = resultSet.getString("content");
	            String author = resultSet.getString("author");
	            Timestamp timestamp = resultSet.getTimestamp("timestamp");
	            String questionTitle = resultSet.getString("title");
	            String questionId = resultSet.getString("questionId");
	            System.out.println("Pending Answer: " + resultSet.getString("content"));

	            Answer answer = new Answer(content, author);
	            answer.setAnswerId(answerId);
	            answer.setQuestionId(questionId);
	            answer.setQuestionTitle(questionTitle);  // Store the question title in the Answer model
	            
	            answers.addAnswer(answer);
	        }
	    }
	    return answers;
	}



	public void updateQuestionResolved(String questionId, String resolvedBy) throws SQLException {
			String sql = "UPDATE questions SET resolvedBy = ? WHERE questionId = ?";
			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.setString(1, resolvedBy);
					stmt.setString(2, questionId);
					stmt.executeUpdate();
			} catch (SQLException e) {
					e.printStackTrace();
			}
	}
	
    public void addReview(Review review) throws SQLException {
        String insertReview = "INSERT INTO reviews (reviewId, answerId, reviewerUsername, reviewText, timestamp, parentReviewId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseHelper.getInstance().connection.prepareStatement(insertReview)) {
            pstmt.setString(1, review.getReviewId());
            pstmt.setString(2, review.getAnswerId());
            pstmt.setString(3, review.getReviewerUsername());
            pstmt.setString(4, review.getReviewText());
            pstmt.setTimestamp(5, new Timestamp(review.getTimestamp().getTime()));
            pstmt.setString(6, review.getParentReviewId());
            pstmt.executeUpdate();
        }
    }

    public List<Review> getReviewsForAnswer(String answerId, String currentUser) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE answerId = ? OR reviewerUsername = ?";
        try (PreparedStatement pstmt = DatabaseHelper.getInstance().connection.prepareStatement(query)) {
            pstmt.setString(1, answerId);
           pstmt.setString(2, currentUser); 
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reviews.add(new Review(
                        rs.getString("reviewId"),
                        rs.getString("answerId"),
                        rs.getString("reviewerUsername"),
                        rs.getString("reviewText"),
                        rs.getTimestamp("timestamp")
                ));
            }
        }
        return reviews;
    }
    
    public List<Review> getReviewHistory(String parentReviewId) throws SQLException {
        List<Review> reviewHistory = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE parentReviewId = ? ORDER BY timestamp ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, parentReviewId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                    rs.getString("reviewId"),
                    rs.getString("answerId"),
                    rs.getString("reviewerUsername"),
                    rs.getString("reviewText"),
                    rs.getTimestamp("timestamp"),
                    rs.getString("parentReviewId") 
                );
                reviewHistory.add(review);
            }
        }
        return reviewHistory;
    }



    public void updateReview(String reviewId, String newText) throws SQLException {
        String query = "UPDATE reviews SET reviewText = ? WHERE reviewId = ?";
        try (PreparedStatement pstmt = DatabaseHelper.getInstance().connection.prepareStatement(query)) {
            pstmt.setString(1, newText);
            pstmt.setString(2, reviewId);
            pstmt.executeUpdate();
        }
    }

    public void deleteReview(String reviewId) throws SQLException {
        String query = "DELETE FROM reviews WHERE reviewId = ?";
        try (PreparedStatement pstmt = DatabaseHelper.getInstance().connection.prepareStatement(query)) {
            pstmt.setString(1, reviewId);
            pstmt.executeUpdate();
        }
    }
    
    public void addReviewerRequest(String requestId, String studentUsername, String status) throws SQLException {
        String insertRequest = "INSERT INTO reviewer_requests (requestId, studentUsername, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertRequest)) {
            pstmt.setString(1, requestId);
            pstmt.setString(2, studentUsername);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        }
    }
    
    public boolean doesReviewerRequestExist(String studentUsername) throws SQLException {
        String query = "SELECT COUNT(*) FROM reviewer_requests WHERE studentUsername = ? AND status = 'PENDING'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUsername);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    public List<String> getPendingReviewerRequests() throws SQLException {
        List<String> requests = new ArrayList<>();
        String query = "SELECT studentUsername FROM reviewer_requests WHERE status = 'PENDING'";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                requests.add(rs.getString("studentUsername"));
            }
        }
        return requests;
    }
    
    public void updateReviewerRequestStatus(String studentUsername, String status) throws SQLException {
        String query = "UPDATE reviewer_requests SET status = ? WHERE studentUsername = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setString(2, studentUsername);
            pstmt.executeUpdate();
        }
    }
    
    public void addReviewerRole(String username) throws SQLException {
        String query = "UPDATE cse360users SET roles = CONCAT(roles, ',REVIEWER') WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }
    
    public ReviewerRequestStatus wasReviewerRequestAccepted(String username) throws SQLException {
        String query = "SELECT status FROM reviewer_requests WHERE studentUsername = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String status = rs.getString("status");
                if ("ACCEPTED".equals(status)) {
                    return ReviewerRequestStatus.ACCEPTED;
                } else if ("DENIED".equals(status)) {
                    return ReviewerRequestStatus.DENIED;
                }
            }
        }
        return ReviewerRequestStatus.NO_REQUEST; // Default value
    }
    
    public void resetReviewerRequestStatus(String username) throws SQLException {
        String query = "UPDATE reviewer_requests SET status = 'NO_REQUEST' WHERE studentUsername = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

public List<Review> getAllReviews() throws SQLException {
    List<Review> reviews = new ArrayList<>();
    String query = "SELECT r.reviewId, r.answerId, r.reviewerUsername, r.reviewText, r.timestamp, " +
                   "a.content AS answerContent, q.title AS questionTitle " +
                   "FROM reviews r " +
                   "JOIN answers a ON r.answerId = a.answerId " +
                   "JOIN questions q ON a.questionId = q.questionId";

    try (PreparedStatement pstmt = connection.prepareStatement(query);
         ResultSet resultSet = pstmt.executeQuery()) {
        while (resultSet.next()) {
            Review review = new Review(
                resultSet.getString("reviewId"),
                resultSet.getString("answerId"),
                resultSet.getString("reviewerUsername"),
                resultSet.getString("reviewText"),
                resultSet.getTimestamp("timestamp")
            );
            review.setQuestionTitle(resultSet.getString("questionTitle"));
            review.setAnswerContent(resultSet.getString("answerContent"));

            reviews.add(review);
        }
    }
    
    return reviews;
    
    
    
}
public void addReviewFeedback(ReviewFeedback feedback) throws SQLException {
    String query = "INSERT INTO review_feedback (feedbackId, reviewId, studentUsername, feedbackText, timestamp) VALUES (?, ?, ?, ?, ?)";
    
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, feedback.getFeedbackId());
        pstmt.setString(2, feedback.getReviewId());
        pstmt.setString(3, feedback.getStudentUsername());
        pstmt.setString(4, feedback.getFeedbackText());
        pstmt.setTimestamp(5, feedback.getTimestamp());

        // ✅ Debugging: Print before inserting
        System.out.println("Inserting Feedback: " + feedback.getFeedbackText());

        pstmt.executeUpdate();
    }
}


public List<ReviewFeedback> getFeedbackForReview(String reviewId) throws SQLException {
    List<ReviewFeedback> feedbackList = new ArrayList<>();
    String query = "SELECT * FROM review_feedback WHERE reviewId = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, reviewId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            ReviewFeedback feedback = new ReviewFeedback(
                rs.getString("feedbackId"),
                rs.getString("reviewId"),
                rs.getString("studentUsername"),
                rs.getString("feedbackText"),
                rs.getTimestamp("timestamp")
            );
            feedbackList.add(feedback);

            // ✅ Debugging: Print feedback retrieval
            System.out.println("Retrieved Feedback: " + feedback.getFeedbackText());
        }
    }
    return feedbackList;
}


/*public void addReviewerRating(String reviewerUsername, String studentUsername, int rating, String reviewId) throws SQLException {
    // Check if the reviewer already has a rating from this student for this review
    String checkQuery = "SELECT * FROM reviewer_ratings WHERE reviewerUsername = ? AND studentUsername = ? AND reviewId = ?";
    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
        checkStmt.setString(1, reviewerUsername);
        checkStmt.setString(2, studentUsername);
        checkStmt.setString(3, reviewId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            // Update existing rating
            int totalRatings = rs.getInt("totalRatings") + rating;
            int numRatings = rs.getInt("numRatings") + 1;
            double averageRating = (double) totalRatings / numRatings;
            double weight = calculateWeight(averageRating);

            String updateQuery = "UPDATE reviewer_ratings SET totalRatings = ?, numRatings = ?, averageRating = ?, weight = ? WHERE ratingId = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, totalRatings);
                updateStmt.setInt(2, numRatings);
                updateStmt.setDouble(3, averageRating);
                updateStmt.setDouble(4, weight);
                updateStmt.setString(5, rs.getString("ratingId"));
                updateStmt.executeUpdate();
            }
        } else {
            // Insert new rating
            String insertQuery = "INSERT INTO reviewer_ratings (ratingId, reviewerUsername, studentUsername, totalRatings, numRatings, averageRating, weight, reviewId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, UUID.randomUUID().toString());
                insertStmt.setString(2, reviewerUsername);
                insertStmt.setString(3, studentUsername);
                insertStmt.setInt(4, rating);
                insertStmt.setInt(5, 1); // numRatings starts at 1
                insertStmt.setDouble(6, rating); // averageRating starts at the first rating
                insertStmt.setDouble(7, calculateWeight(rating));
                insertStmt.setString(8, reviewId);
                insertStmt.executeUpdate();
            }
        }
    }
}*/

public void addReviewerRating(String reviewerUsername, String studentUsername, int rating, String reviewId) throws SQLException {
    // Check if the reviewer already has a rating from this student
    String checkQuery = "SELECT totalRatings, numRatings FROM reviewer_ratings WHERE reviewerUsername = ? AND studentUsername = ?";
    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
        checkStmt.setString(1, reviewerUsername);
        checkStmt.setString(2, studentUsername);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            // Update existing rating instead of inserting a new one
            int totalRatings = rs.getInt("totalRatings") + rating;
            int numRatings = rs.getInt("numRatings") + 1;
            double averageRating = (double) totalRatings / numRatings;
            double weight = calculateWeight(averageRating);

            String updateQuery = "UPDATE reviewer_ratings SET totalRatings = ?, numRatings = ?, averageRating = ?, weight = ? WHERE reviewerUsername = ? AND studentUsername = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, totalRatings);
                updateStmt.setInt(2, numRatings);
                updateStmt.setDouble(3, averageRating);
                updateStmt.setDouble(4, weight);
                updateStmt.setString(5, reviewerUsername);
                updateStmt.setString(6, studentUsername);
                updateStmt.executeUpdate();
            }
        } else {
            // If no existing rating, insert a new one (only happens if the previous logic fails)
            String insertQuery = "INSERT INTO reviewer_ratings (ratingId, reviewerUsername, studentUsername, totalRatings, numRatings, averageRating, weight, reviewId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, UUID.randomUUID().toString());
                insertStmt.setString(2, reviewerUsername);
                insertStmt.setString(3, studentUsername);
                insertStmt.setInt(4, rating);
                insertStmt.setInt(5, 1); 
                insertStmt.setDouble(6, rating); 
                insertStmt.setDouble(7, calculateWeight(rating));
                insertStmt.setString(8, reviewId);
                insertStmt.executeUpdate();
            }
        }
    }
}

public void updateReviewerRating(String ratingId, int newRating) throws SQLException {
 String query = "UPDATE reviewer_ratings SET rating = ?, weight = ? WHERE ratingId = ?";
 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
     pstmt.setInt(1, newRating);
     pstmt.setDouble(2, calculateWeight(newRating)); // Recalculate weight
     pstmt.setString(3, ratingId);
     pstmt.executeUpdate();
 }
}




private double calculateWeight(double averageRating) {
    return averageRating / 5.0; // Normalize weight to 0-1
}

public void addToTrustedReviewers(String reviewerUsername, String studentUsername) throws SQLException {
    // Check if the reviewer already has a rating from this student
    String checkQuery = "SELECT * FROM reviewer_ratings WHERE reviewerUsername = ? AND studentUsername = ?";
    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
        checkStmt.setString(1, reviewerUsername);
        checkStmt.setString(2, studentUsername);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) { // If no rating exists, insert a default one
            String insertRatingQuery = "INSERT INTO reviewer_ratings (ratingId, reviewerUsername, studentUsername, totalRatings, numRatings, averageRating, weight, reviewId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertRatingStmt = connection.prepareStatement(insertRatingQuery)) {
                insertRatingStmt.setString(1, UUID.randomUUID().toString()); // Generate a unique rating ID
                insertRatingStmt.setString(2, reviewerUsername);
                insertRatingStmt.setString(3, studentUsername);
                insertRatingStmt.setInt(4, 0); 
                insertRatingStmt.setInt(5, 0); 
                insertRatingStmt.setDouble(6, 0.0); // Default averageRating = 0.0
                insertRatingStmt.setDouble(7, 0.0); // Default weight = 0.0
                insertRatingStmt.setString(8, null); 
                insertRatingStmt.executeUpdate();
            }
        }
    }

    // Now, insert into trusted_reviewers table
    String query = "INSERT INTO trusted_reviewers (reviewerUsername, studentUsername) VALUES (?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, reviewerUsername);
        pstmt.setString(2, studentUsername);
        pstmt.executeUpdate();
    }
}


public List<ReviewerRating> getTrustedReviewers(String studentUsername) throws SQLException {
    List<ReviewerRating> trustedReviewers = new ArrayList<>();
    String query = "SELECT * FROM reviewer_ratings WHERE studentUsername = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, studentUsername);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            String ratingId = rs.getString("ratingId");
            String reviewerUsername = rs.getString("reviewerUsername");
            double averageRating = rs.getDouble("averageRating");
            double weight = rs.getDouble("weight");
            
            ReviewerRating rating = new ReviewerRating(ratingId, reviewerUsername, studentUsername, averageRating, weight, reviewerUsername);
            trustedReviewers.add(rating);
        }
    }
    
    return trustedReviewers;
}

public String getReviewIdForReviewer(String reviewerUsername) throws SQLException {
    String query = "SELECT reviewId FROM reviews WHERE reviewerUsername = ? LIMIT 1";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, reviewerUsername);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("reviewId");
        }
    }
    return null; // Return null if no review is found
}

public List<Review> getAllTrustedReviews(String studentUsername) throws SQLException {
    List<Review> reviews = new ArrayList<>();
    String query = "SELECT r.reviewId, r.answerId, r.reviewerUsername, r.reviewText, r.timestamp " +
                   "FROM reviews r " +
                   "JOIN trusted_reviewers tr ON r.reviewerUsername = tr.reviewerUsername " +
                   "WHERE tr.studentUsername = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, studentUsername);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            Review review = new Review(
                resultSet.getString("reviewId"),
                resultSet.getString("answerId"),
                resultSet.getString("reviewerUsername"),
                resultSet.getString("reviewText"),
                resultSet.getTimestamp("timestamp")
            );
            reviews.add(review);
        }
    }
    return reviews;
}



// Method to send a private message
public static void sendPrivateMessage(String questionId, String senderUsername, String receiverUsername, String messageContent) {
    String query = "INSERT INTO private_messages (questionId, senderUsername, receiverUsername, messageContent, timestamp, isRead) VALUES (?, ?, ?, ?, NOW(), FALSE)";

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, questionId);
        stmt.setString(2, senderUsername);
        stmt.setString(3, receiverUsername);
        stmt.setString(4, messageContent);

        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


// Method to fetch private messages for a question
public static List<PrivateMessage> getPrivateMessages(String questionId, String receiverUsername) {
    List<PrivateMessage> messages = new ArrayList<>();
    String query = "SELECT * FROM private_messages WHERE questionId = ? AND receiverUsername = ? ORDER BY timestamp DESC";

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, questionId);
        stmt.setString(2, receiverUsername);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            PrivateMessage msg = new PrivateMessage(
                rs.getInt("messageId"),
                rs.getString("questionId"),
                rs.getString("senderUsername"),
                rs.getString("receiverUsername"),
                rs.getString("messageContent"),
                rs.getTimestamp("timestamp"),
                rs.getBoolean("isRead")
            );
            messages.add(msg);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return messages;
}


// Method to mark messages as read
public static void markMessageAsRead(int messageId) {
    String query = "UPDATE private_messages SET is_read = 1 WHERE message_id = ?";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setInt(1, messageId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static int getUserIdByUsername(String username) {
    String query = "SELECT userId FROM users WHERE username = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("userId");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1; // Return -1 if user not found
}


 
}

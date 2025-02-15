package application;

import java.sql.SQLException;
import java.util.ArrayList;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class questions {
	
	 private final DatabaseHelper databaseHelper;
	 public questions(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	 }
	 private final List<questionsub> questions = new ArrayList<>();
	 int questionnum = 1;

	 private void loadQuestions(ListView<String> questionpage) {
     	List<String> allQuestions = databaseHelper.getQuestions();
     	questionpage.getItems().setAll(allQuestions);
     	
     	
     }
	 
	    public void show(Stage primaryStage) {
	    	// Input fields for userName and password
	        TextField questionField = new TextField();
	        questionField.setPromptText("Enter your question:");
	        questionField.setMaxWidth(250);
	        
	        
	        Button newQuestion = new Button("Add a new Question!");
            Button relatedquestions = new Button("Read related questions!");
	      

	        // Label to display error messages
	        Label errorLabel = new Label();
	        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	      
            ListView<String> questionpage = new ListView<>();
            loadQuestions(questionpage);
    
	       
	        newQuestion.setOnAction(a -> {  // allows users to ask question and clears when question is sent
	            String quest = questionField.getText();
	            if (quest.isEmpty()) {
	            	errorLabel.setText("question can't be empty");
	            	return;
	            }
	            
	            errorLabel.setText("");
	            
	            databaseHelper.insertQuestion(quest);
	            questionsub newquestion = new questionsub(questionnum++, quest);
	            questions.add(newquestion);

	            
	            questionpage.getItems().setAll(
	                questions.stream()
	                    .map(questionsub::getquestion)
	                    .collect(Collectors.toList())
	            );
	            loadQuestions(questionpage);
	            
	            questionField.clear();
	        });
	        
	         
	        relatedquestions.setOnAction(a -> {  // allows users to see question that conatin simlar words as theirs /
	            String word = questionField.getText();
	            if(word.isEmpty()) {
	            	errorLabel.setText("no word to compare!");
	            	return;
	            }
	            List<String> allQuestions = databaseHelper.getQuestions();
	            
	            List<String> relatedquestion = allQuestions.stream() //shows questions with related words
	            		.filter(q -> q.toLowerCase().contains(word.toLowerCase()))
	            	    .collect(Collectors.toList());
	            
	            if (relatedquestion.isEmpty()) {
	            	errorLabel.setText("no questions found!");
	            } else {
	            errorLabel.setText("");	
	            questionpage.getItems().setAll(relatedquestion);
	            }
	        });
	        
	        questionpage.setOnMouseClicked(e -> {
	        	String selected = questionpage.getSelectionModel().getSelectedItem();
	        	
	        	if(selected != null) {
	        		int qnum = questions.stream()
	        				.filter(q-> q.getquestion().equals(selected))
	        				.findFirst()
	        				.map(questionsub::qnum)
	        				.orElse(-1);
	        		if(qnum != -1) {
	        			new answer(databaseHelper).show(primaryStage, qnum);
	        		}
	        	}
	        });
	       
	        
	        
	        Button editq = new Button("Edit");
		    Button deleteq = new Button("delete");
		    
		    editq.setOnAction(e ->{
		    	String updatingq = questionpage.getSelectionModel().getSelectedItem();
		    	if(updatingq == null) {
		    		errorLabel.setText("question not selected!");
		    		return;
		    	}
		    	TextInputDialog dialog = new TextInputDialog(updatingq);
		    	dialog.setTitle("editing");
		    	dialog.showAndWait().ifPresent(newquest -> {
		    		if(newquest.isEmpty()) {
		    			errorLabel.setText("updating nothing!");
		    			return;
		    		}
		    		
		    		databaseHelper.updatequestion(updatingq, newquest);
		    			loadQuestions(questionpage);
		    		});
		    });
		    
		    
		    deleteq.setOnAction(e ->{
		    	String deletingq = questionpage.getSelectionModel().getSelectedItem();
		    	 if(deletingq == null) {
		    		 errorLabel.setText("nothing can't be deleted");
		    		 return;
		    }
	    	databaseHelper.deletequestion(deletingq); 
	        loadQuestions(questionpage);
		    	
		    } );
		    
		    
		    Button answers = new Button("answer!");
		    answers.setOnAction(e -> {
		    	String ansering = questionpage.getSelectionModel().getSelectedItem();
		    	if( ansering == null) {
		    		errorLabel.setText("question not selected!");
		    		return;
		    	}
		    	int qnum = -1;
	            for (questionsub q : questions) {
	                if (q.getquestion().equals(ansering)) {
	                    qnum = q.getqnum();
	                    break;
	                }
	            }
	            new answer(databaseHelper).show(new Stage(), qnum);
		    });
	        
	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> new UserHomePage(databaseHelper).show(primaryStage));
	        
	       
	        VBox layout = new VBox(10);
	      
	        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
	        layout.getChildren().addAll(questionField, newQuestion, relatedquestions, questionpage, errorLabel, backButton, editq, deleteq,answers);
	        

	        primaryStage.setScene(new Scene(layout, 800, 400));
	        primaryStage.setTitle("Question page!");
	        primaryStage.show();

	}
	}

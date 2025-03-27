package application;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class answer {

	 private final DatabaseHelper databaseHelper;
	 public answer(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	 }
	 private final List<answersub> answers = new ArrayList<>();
	 

	 private void loadAnswers(ListView<String> answerpage, int qnum) {
     	List<String> allanswer = databaseHelper.getAnswers(qnum);
     	answerpage.getItems().setAll(allanswer);
     }
	    public void show(Stage primaryStage, int qnum) {
	    	// Input fields for userName and password
	        TextField answerField = new TextField();
	        answerField.setPromptText("Enter an answer:");
	        answerField.setMaxWidth(250);
	        
	        
	        Button newAnswer = new Button("Add a new answer!");
           Button relatedanswers = new Button("Read related answers!");
	      

	        // Label to display error messages
	        Label errorLabel = new Label();
	        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
           ListView<String> answerpage = new ListView<>();
	       
           newAnswer.setOnAction(e -> {  // allows users to ask question and clears when question is sent
	            String ans = answerField.getText();
	            if (ans.isEmpty()) {
	            	errorLabel.setText("answer can't be empty");
	            	return;
	            }
	            
	            errorLabel.setText("");
	            databaseHelper.insertAnswer(ans, qnum);
	            loadAnswers(answerpage, qnum);
	            
	        
	            answerField.clear();
	        });
           
	         
	        relatedanswers.setOnAction(e -> {  // allows users to see question that conatin simlar words as theirs /
	            
	            List<String> star = answers.stream() //shows questions with related words
	            		.filter(answersub::isstarred)
	            		.map(answersub::getanswer)
	            		.collect(Collectors.toList());
	            answerpage.getItems().setAll(star);
	        });
	        
	        
	        Button edita = new Button("Edit");
		    Button deletea = new Button("delete");
		    
		    
		    
		    edita.setOnAction(e ->{
		    	String updatinga = answerpage.getSelectionModel().getSelectedItem();
		    	if(updatinga == null) {
		    		errorLabel.setText("answer not selected!");
		    		return;
		    	}
		    	TextInputDialog dialog = new TextInputDialog(updatinga);
		    	dialog.setTitle("editing");
		    	dialog.showAndWait().ifPresent(newansw -> {
		    		if(newansw.isEmpty()) {
		    			errorLabel.setText("updating nothing!");
		    			return;
		    		}
		    		
		    		databaseHelper.updateanswer(updatinga, newansw);
			    	loadAnswers(answerpage, qnum);
		    	});
		    });
		    
		    
		    deletea.setOnAction(e ->{
		    	String deletinga = answerpage.getSelectionModel().getSelectedItem();
		    	if(deletinga == null) {
		    		 errorLabel.setText("nothing can't be deleted");
		    		 return;
		    	}
		    		databaseHelper.deleteanswer(deletinga);
		    		loadAnswers(answerpage, qnum);
		    	
		    } );
	        
	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> new questions(databaseHelper).show(primaryStage));
	       
	        
	       
	        
	        VBox layout = new VBox(10);
	        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
	        layout.getChildren().addAll(answerField, newAnswer, relatedanswers, answerpage, errorLabel, backButton,edita, deletea);

	        primaryStage.setScene(new Scene(layout, 800, 400));
	        primaryStage.setTitle("Answer page!");
	        primaryStage.show();

	}
}

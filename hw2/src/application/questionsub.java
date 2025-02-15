package application;
import java.util.ArrayList;
import java.util.List;

public class questionsub {
	private  List<questionsub> questions;
	
	private int qnum;
	private String q;
	private boolean Answered;
	private List<Integer> linkedquestions;
	
	public questionsub(int qnum, String q) {
		this.qnum = qnum;
		this.q = q;
		this.Answered = false;
		this.linkedquestions = new ArrayList<>();	
	}
	public int qnum() {
		return qnum;
	}
	public int getqnum() {
		return qnum;
	}
	public void addquestion(questionsub questions) {
		questions.addquestion(questions);
	}
	public String getquestion() {
		return q;
	}
	public void deletequestion(int num) {
		questions.removeIf(question -> question.qnum() == num);
	}
	public List<questionsub> questions(){
		return questions;
	}
	
	public boolean answered() {
		return Answered;
	}
	public void setquestion(String newq) {
		this.q = newq;
	}
	public void linkedquestions(int numquest) {
		linkedquestions.add(numquest);
	}
	

}

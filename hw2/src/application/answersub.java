package application;


public class answersub {
	private int num;
	private String a;
	private boolean isstarred;
	private int qnum;
	
	
	public answersub(int num, String a, boolean starred, int qnum) {
		this.num = num;
		this.a = a;
		this.isstarred = starred;
		this.qnum= qnum;
	
	}
	public int answernumber() {
		return num;
	}
	public void addanswer(answersub answers) {
		answers.addanswer(answers);
	}
	public String getanswer() {
		return a;
	}
	public void setanswer(String newa) {
		this.a = newa;
	}
	
	public boolean isstarred() {
		return isstarred;
	}
	public void markedstarred() {
		isstarred=true;
	}
	public int getqnum() {
		
		return qnum;
	}

}

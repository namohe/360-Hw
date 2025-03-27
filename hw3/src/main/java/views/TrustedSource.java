package views;

import helpers.DatabaseHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;



public class TrustedSource{
	private Set<String> trusted;
	
	
	public TrustedSource() {
		this.trusted = new HashSet<>()	;
		}
	
	public void Addtrusted(String username) {
		this.trusted.add(username);
	}
	
	public void removetrusted(String username) {
		this.trusted.remove(username);
	}
	
	public Set<String> gettrustedsources(){
	return trusted;
}
}
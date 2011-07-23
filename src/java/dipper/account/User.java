package dipper.account;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class User {
	private final String username;
	private HashSet<String> groups;

	public User(String username, Collection<String> groups) {
		this.username = username;
		this.groups = new HashSet<String>(groups);
	}
	
	public String getUserName() {
		return username;
	}
	
	public Set<String> getGroups() {
		return groups;
	}
}
package dipper.webapp.session;

import dipper.account.User;

public class DipperSession {
	public final User user;
	
	// This id can change, so it should not be finalized.
	public String sessionId;
	public long lastSessionIdChange;
	
	public long lastAccessTime;
	
	/*package*/ DipperSession(User user) {
		this.user = user;
	}
	
	public String getID() {
		return sessionId;
	}
	
	public void setID(String id) {
		sessionId = id;
		lastSessionIdChange = System.currentTimeMillis();
	}
	
	public long getSessionIdAge() {
		return System.currentTimeMillis() - lastSessionIdChange;
	}
	
	public void updateTime() {
		lastAccessTime = System.currentTimeMillis();
	}
}
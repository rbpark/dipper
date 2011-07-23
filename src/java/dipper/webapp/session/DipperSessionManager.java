package dipper.webapp.session;

import java.util.HashMap;
import java.util.UUID;

import dipper.account.User;

public class DipperSessionManager {
	private HashMap<String, DipperSession> dipperSessions = new HashMap<String, DipperSession>();
	private static final long SESSION_ID_SWITCH = 60000;
	
	public DipperSessionManager() {
	}
	
	public boolean doesSessionExist(String id) {
		return dipperSessions.containsKey(id);
	}
	
	public DipperSession getDipperSession(String id) {
		DipperSession session = dipperSessions.get(id);
		
		// We switch the session id just to give that extra bit of ? just to prevent session hijacking
		if (session.getSessionIdAge() > SESSION_ID_SWITCH) {
			assignNewUid(session);
		}
		
		return session;
	}
	
	public DipperSession createSession(User user) {
		DipperSession session = new DipperSession(user);
		assignNewUid(session);
		return session;
	}
	
	private void assignNewUid(DipperSession session) {
		String uuid = UUID.randomUUID().toString();
		session.setID(uuid);
		dipperSessions.remove(uuid);
		dipperSessions.put(uuid, session);
	}
}
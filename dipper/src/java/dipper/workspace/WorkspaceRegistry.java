package dipper.workspace;

import java.io.File;
import java.util.HashMap;

public class WorkspaceRegistry {
	public static WorkspaceRegistry instance = new WorkspaceRegistry();
	private HashMap<String, WorkspaceEntry> registry = new HashMap<String, WorkspaceEntry>();
	
	private WorkspaceRegistry() {		
	}
	
	public void registerWorkspace(File directory) {
		
	}
}
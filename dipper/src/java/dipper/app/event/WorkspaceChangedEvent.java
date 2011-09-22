package dipper.app.event;

import dipper.app.DipperProject;

public class WorkspaceChangedEvent {
	public static final int CHANGE_UNKNOWN = 0;
	public static final int PROJECT_ADDED_CHANGE = 1;
	public static final int PROJECT_CLOSED_CHANGE = 2;
	
	private DipperProject project;
	private int changeType = CHANGE_UNKNOWN;
	
	public WorkspaceChangedEvent() {
	}

	public void setProject(DipperProject project) {
		this.project = project;
	}

	public DipperProject getProject() {
		return project;
	}

	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}

	public int getChangeType() {
		return changeType;
	}	
}
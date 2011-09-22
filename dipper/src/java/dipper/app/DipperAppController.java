package dipper.app;

import java.util.Collection;
import java.util.LinkedHashSet;

import dipper.app.event.WorkspaceChangedEvent;
import dipper.app.event.WorkspaceChangedListener;

public class DipperAppController {
	private DipperProject attachedListeners = null;
	private Collection<WorkspaceChangedListener> workspaceChangedListeners = new LinkedHashSet<WorkspaceChangedListener>();
	private WorkspaceChangedEvent event = new WorkspaceChangedEvent();
	
	public DipperAppController() {
		
	}
	
	public DipperProject getActiveProject() {
		return attachedListeners;
	}
	
	public void newProject(String name) {
		attachedListeners = new DipperProject(name);
		fireWorkspaceChangedEvent(attachedListeners, WorkspaceChangedEvent.PROJECT_ADDED_CHANGE);
	}
	
	public void addWorkspaceChangeListener(WorkspaceChangedListener listener) {
		workspaceChangedListeners.add(listener);
	}
	
	public void removeWorkspaceChangeListener(WorkspaceChangedListener listener) {
		workspaceChangedListeners.remove(listener);
	}
	
	protected void fireWorkspaceChangedEvent(DipperProject project, int reason) {
		event.setProject(project);
		event.setChangeType(reason);
		for (WorkspaceChangedListener l: workspaceChangedListeners) {
			l.workspaceChanged(event);
		}
	}
}
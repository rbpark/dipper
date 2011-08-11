package dipper.desktop.ui;

import java.awt.Component;

import javax.swing.JSplitPane;

public class ProjectSplitPane extends JSplitPane {
	private static final long serialVersionUID = 3576490010924786027L;
	public ProjectCanvas canvas;
	
	public ProjectSplitPane() {
		this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
	}
	
	public void setCanvas(ProjectCanvas canvas) {
		this.setLeftComponent(canvas);
		this.canvas = canvas;
	}
	
	public void reposition() {
		Component parent = getParent();
		if (parent != null) {
			this.setSize(getParent().getWidth(), getParent().getHeight());
		}
	}
}
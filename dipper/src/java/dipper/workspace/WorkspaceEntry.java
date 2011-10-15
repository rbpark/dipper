package dipper.workspace;

import java.io.File;
import java.util.HashSet;

public class WorkspaceEntry {
	private static final String EMPTY_DESCRIPTION = "no description";
	private static final String INITIAL_VERSION = "n/a";
	
	private HashSet<File> librariesToLoad = new HashSet<File>();
	private final String name;
	
	private String version = INITIAL_VERSION;
	private String description = EMPTY_DESCRIPTION;
	private String author = INITIAL_VERSION;
	
	public WorkspaceEntry(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addClasspathResource(File resource) {
		librariesToLoad.add(resource);
	}

}
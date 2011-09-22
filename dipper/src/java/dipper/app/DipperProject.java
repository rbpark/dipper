package dipper.app;

public class DipperProject {
	private String name;
	
	public DipperProject(String name) {
		this.setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
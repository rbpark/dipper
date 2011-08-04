package dipper.nodes;

import java.awt.Image;
import java.awt.Point;

public abstract class Node {
	private String name;
	private Point position;
	
	public Node() {
	}
	
	public Node(String name) {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setPosition(double x, double y) {
		position.setLocation(x, y);
	}
	
	public void setPosition(Point p) {
		position.setLocation(p);
	}
	
	public double getX() {
		return position.getX();
	}
	
	public double getY() {
		return position.getY();
	}
	
	public abstract Image getIcon();
}
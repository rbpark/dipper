package dipper.desktop.ui;

import java.awt.Color;

import javax.swing.JPanel;

import dipper.desktop.ui.interpolate.PositionInterpolator;

public class AnimatablePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public static final int INSTANT = -1;
	public static final int FAST = 2000;
	public static final int MEDIUM = 1000;
	public static final int SLOW = 100;
	
	private int moveSpeed = INSTANT;
	private PositionInterpolator positionInterpolator;
	
	public AnimatablePanel() {
		super();
		positionInterpolator = new PositionInterpolator(this);
		this.setOpaque(false);
		setDoubleBuffered(true);
	}
	
	public void setMoveSpeed(int speed) {
		moveSpeed = speed;
	}

	public void animateLocation(int x, int y) {
		animateLocation(x, y, moveSpeed);
	}
	
	public void animateLocation(int x, int y, int speed) {
		if (speed <= 0) {
			positionInterpolator.setPosition(x, y, false);
		}
		else {
			positionInterpolator.setStepsPerSecond(speed);			
			positionInterpolator.setPosition(x, y, true);
		}
	}

}
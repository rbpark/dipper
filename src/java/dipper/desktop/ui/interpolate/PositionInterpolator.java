package dipper.desktop.ui.interpolate;

import java.awt.Component;

public class PositionInterpolator implements ValueInterpolator{
	private Component component;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private long startTimeMS = -1;
	
	private int stepValuePerSec = 1000;
		
	public PositionInterpolator(Component component) {
		this.component = component;
		this.endX = component.getX();
		this.endY = component.getY();
	}
	
	public void setStepsPerSecond(int steps) {
		stepValuePerSec = steps;
	}
	
	public void setPosition(int x, int y) {
		setPosition(x, y, true);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public synchronized void setPosition(int x, int y, boolean animate) {
		if (!animate) {
			component.setLocation(x, y);
			return;
		}
		
		startX = component.getX();
		startY = component.getY();
		endX = x;
		endY = y;
		
		if (x != startX || y != startY) {
			startTimeMS = System.currentTimeMillis();
			UIAnimator.addAnimation(this);
		}
	}
	
	/**
	 * The only caller of this should be the interpolator
	 */
	public boolean interpolate() {
		int x = component.getX();
		int y = component.getY();
		if (x == endX && y == endY) {
			return false;
		}
		
		long deltaTime = System.currentTimeMillis() - startTimeMS;
		
		int steps = (int)((deltaTime * stepValuePerSec) / 1000);
		if (x != endX) {
			x = resolveValue(startX, endX, steps);
		}
		if (y != endY) {
			y = resolveValue(startY, endY, steps);
		}
		component.setLocation(x, y);
		return true;
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param steps
	 * @return
	 */
	private int resolveValue(int start, int end, int steps) {
		if (start == end) {
			return end;
		}
		
		if (start < end ){
			// need to increment the steps.
			int val = start + steps;
			if (val > end) {
				return end;
			}
			return val;
		}
		else {
			int val = start - steps;
			if (val < end) {
				return end;
			}
			return val;
		}
	}
}
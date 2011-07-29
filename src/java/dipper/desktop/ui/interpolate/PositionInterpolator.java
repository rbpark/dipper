package dipper.desktop.ui.interpolate;

import java.awt.Component;

public class PositionInterpolator implements ValueInterpolator{
	private Component component;
	private int startX;
	private int startY;
	
	private int endX;
	private int endY;
	private float dx;
	private float dy;
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
		
		int deltaX = endX - startX;
		int deltaY = endY - startY;
		double inverseMagnitude = 1.0d/Math.sqrt(deltaX*deltaX + deltaY*deltaY); 
		
		dx = (float)inverseMagnitude * deltaX;
		dy = (float)inverseMagnitude * deltaY;
		
		if (x != startX || y != startY) {
			startTimeMS = System.currentTimeMillis();
			UIAnimator.addAnimation(this);
		}
	}
	
	/**
	 * The only caller of this should be the interpolator
	 */
	public boolean interpolate() {
		int x1 = component.getX();
		int y1 = component.getY();
		if ((x1 == endX && y1 == endY)) {
			return false;
		}
		
		long deltaTime = System.currentTimeMillis() - startTimeMS;
		int x2 = x1;
		int y2 = y1;
		
		float steps = ((float)(deltaTime * stepValuePerSec)/ 1000f);
		if (x1 != endX) {
			x2 = resolveValue(startX, endX, (int)(dx*steps));
		}
		if (y1 != endY) {
			y2 = resolveValue(startY, endY, (int)(dy*steps));
		}
		
		component.setLocation(x2, y2);

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
		
		int val = start + steps;
		if (start < end ){
			// need to increment the steps.
			if (val > end) {
				return end;
			}
		}
		else {
			if (val < end) {
				return end;
			}
		}
		return val;
	}
}
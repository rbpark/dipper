package dipper.desktop.ui.interpolate;

import dipper.desktop.ui.OpacityComponent;

public class OpacityInterpolator implements ValueInterpolator{
	private float step = 0.01f;
	private float target;
	private float start;
	private float dy;
	private long startTimeMs;
	private OpacityComponent comp;
	
	public OpacityInterpolator(OpacityComponent comp) {
		this.comp = comp;
		target = comp.getOpacity();
	}
	
	public void setStepsPerSecond(float steps) {
		this.step = steps;
	}
	
	public synchronized void setOpacity(float o, boolean animate) {
		start = comp.getOpacity();
		target = o;
		
		if (start == target) {
			return;
		}
		if (animate || step <= 0) {
			comp.setOpacity(o);
		}
		
		startTimeMs = System.currentTimeMillis();
		dy = target - start > 0 ? step : -step;
		UIAnimator.addAnimation(this);
	}
	
	@Override
	public boolean interpolate() {
		long deltaTime = System.currentTimeMillis() - startTimeMs;

		float thisOpacity = ((float)(deltaTime * dy)) + start;
		
		if (dy >= 0) {
			if (thisOpacity > target) {
				comp.setOpacity(target);
				return false;
			}
		}
		else {
			if (thisOpacity < target) {
				comp.setOpacity(target);
				return false;	
			}
		}
		
		comp.setOpacity(thisOpacity);
		return true;
	}
}
package dipper.desktop.ui.interpolate;

public class DragVelocityInterpolator implements ValueInterpolator {
	private long currentTimeMS = -1;
	private double maxVelocity = 100;

	private double velocityThreshold = 0.01;
	private double decelerationPerMS = 1.0/15.0;
	
	private double vx;
	private double vy;
	private double dx;
	private double dy;
	
	private TranslateComponent comp;
	
	public DragVelocityInterpolator(TranslateComponent comp) {
		this.comp=comp;
	}
	
	public void setMaxVelocity(double v) {
		maxVelocity = v;
	}
	
	@Override
	public synchronized boolean interpolate() {
		if ( vx*vx + vy*vy < velocityThreshold*velocityThreshold) {
			return false;
		}
		long stepTime = (System.currentTimeMillis() - currentTimeMS + 1);
		vx -= dx * stepTime;
		vy -= dy * stepTime;
		
		
		if ((dx > 0 && vx < 0) || (dx < 0 && vx > 0) || (dy > 0 && vy < 0) || (dy < 0 && vy > 0) ) {
			return false;
		}
			
		double tx = comp.getTranslateX() + vx * stepTime;
		double ty = comp.getTranslateY() + vy * stepTime;
		
		comp.setTranslate(tx, ty);
		currentTimeMS = System.currentTimeMillis();
		
		return true;
	}
	
	public synchronized void setVelocity(double vx, double vy) {	
		this.vx = vx;
		this.vy = vy;
		
		double velocity = Math.sqrt( vx*vx + vy*vy);
		if (velocity < velocityThreshold) {
			return;
		}
		else if (velocity > maxVelocity) {
			velocity = maxVelocity;
		}
		
		double factor = decelerationPerMS/velocity;
		this.dx = vx * factor;
		this.dy = vy * factor;
		
		currentTimeMS = System.currentTimeMillis();
		
		UIAnimator.addAnimation(this);
	}
}
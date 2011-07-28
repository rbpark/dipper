package dipper.desktop.ui.interpolate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dipper.desktop.ui.interpolate.ValueInterpolator;

public class UIAnimator {
	private static final int idleWaitTime = 10000;
	// Should give us about 60fps
	private static final long waitBetweenFrames = 15;
	private static UIAnimator instance = new UIAnimator();
	private Set<ValueInterpolator> interpolators;
	private List<ValueInterpolator> interpolatorQueue;
	
	private AnimationLoop looper; 
	
	private UIAnimator() {
		interpolators = new HashSet<ValueInterpolator>();
		interpolatorQueue = new ArrayList<ValueInterpolator>();
		looper = new AnimationLoop();
		looper.start();
	}
	
	public static void addAnimation(ValueInterpolator value) {
		instance.internalAddAnimation(value);
	}
	
	private synchronized void internalAddAnimation(ValueInterpolator value) {
		synchronized(interpolatorQueue) {
			interpolatorQueue.add(value);
		}
		looper.interrupt();
	}
	
	private class AnimationLoop extends Thread {
		boolean isRunning = true;
		
		public void run() {
			while (isRunning) {
				// We only need to synchronize on the queue, since adding to the set
				// should be protected.
				synchronized (interpolatorQueue) {
					// Periodically Add items from the queue into the interpolator set.
					if (!interpolatorQueue.isEmpty()) {
						interpolators.addAll(interpolatorQueue);
						interpolatorQueue.clear();
					}
				}
				
				//Iterate through the map list and then move on.
				if (!interpolators.isEmpty()) {
					for (ValueInterpolator interpolator: interpolators) {
						if(!interpolator.interpolate()) {
							interpolators.remove(interpolator);
						}
					}
//					
//					// Try to keep animation at 60 fps.
//					synchronized (this) {
//						try {
//							this.wait(waitBetweenFrames);
//						} catch (InterruptedException e) {
//						}
//					}
				}
				else {
					synchronized (this) {
						try {
							this.wait(idleWaitTime);
						} catch (InterruptedException e) {
						}
					}
				}
				
			}
		}
	}
}
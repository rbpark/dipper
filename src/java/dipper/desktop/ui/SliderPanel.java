package dipper.desktop.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

public class SliderPanel extends AnimatablePanel {
	private static final long serialVersionUID = 1L;
	private static final String SPRITE_BORDER_IMG_PATH = "images/slideMenu.png";
	
	public static final int BIND_LEFT = 0;
	public static final int BIND_RIGHT = 1;
	public static final int BIND_TOP = 2;
	public static final int BIND_BOTTOM = 3;
	
	private boolean isExpanded = true;
	private int bindSide = BIND_LEFT;
	
	private int dimension = 0;
	private int collapseMargin = 25;
	private int gestureOverpull = 15;
	private int marginLeft = 0;
	private int marginRight = 0;
	private int marginTop = 0;
	private int marginBottom = 0;
	
	private int expanded = 0;
	private int collapsed = 0;
	
	private float gestureExpandThreshold = 0.3f;
	private float gestureCollapseThreshold = 0.7f;
	
	private static BorderSprites defaultSprite;
	static {
		try {
			defaultSprite = new BorderSprites();
			defaultSprite.createSpritesFromResource(SPRITE_BORDER_IMG_PATH, 32, 32, 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Border sprites
	private BorderSprites sprites = defaultSprite;
	
	public SliderPanel(int bindSide) {
		super();
		this.bindSide = bindSide;
		
		GestureMouse listener = new GestureMouse(this);
		this.addMouseMotionListener(listener);
		this.addMouseListener(listener);
		this.setMoveSpeed(AnimatablePanel.FAST);
	}
	
	public void setBorderSprites(BorderSprites sprites) {
		this.sprites = sprites;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (sprites != null) {
			sprites.drawBorderPanel(g, this.getWidth(), this.getHeight());
		}
	}
	
	public void setMargins(int left, int right, int top, int bottom) {
		marginLeft = left;
		marginRight = right;
		marginTop = top;
		marginBottom = bottom;
		reposition();
	}
	
	public void setExpanded(boolean expanded) {
		isExpanded = expanded;
		animate();
	}
	
	public void setGestureOverpull(int gestureOverpull) {
		this.gestureOverpull = gestureOverpull;
	}
	
	public void setGestureCollapseExpandThresholds(float collapse, float expand) {
		gestureCollapseThreshold = collapse;
		gestureExpandThreshold = expand;
	}
	
	public void toggleExpanded() {
		isExpanded = !isExpanded;
		animate();
	}
	
	public boolean isExpanded() {
		return isExpanded;
	}
	
	public void setDimension(int dimension) {
		this.dimension = dimension;
		reposition();
	}
	
	public void setCollapseMargin(int margin) {
		this.collapseMargin = margin;
		reposition();
	}
	
	private synchronized void animate() {
		switch (bindSide) {
		case BIND_LEFT:
		case BIND_RIGHT:
			this.animateLocation(isExpanded ? expanded : collapsed, marginTop);
			break;
		case BIND_TOP:
		case BIND_BOTTOM:
			this.animateLocation(marginLeft, isExpanded ? expanded : collapsed);
			break;
		}
	}
	
	public synchronized void reposition() {
		Component parent = this.getParent();
		if (parent == null) {
			return;
		}
		
		int x = this.getX();
		int y = this.getY();
		int width = dimension;
		int height = dimension;
		switch (bindSide) {
		case BIND_LEFT:
			y = marginTop;
			height = parent.getHeight() - marginTop - marginBottom;

			expanded = marginLeft;
			collapsed = -width + collapseMargin;
			this.setLocation(isExpanded ? expanded : collapsed, y);
			break;
		case BIND_RIGHT:
			y = marginTop;
			height = parent.getHeight() - marginTop - marginBottom;

			expanded = parent.getWidth() - width - marginRight;
			collapsed = parent.getWidth() - collapseMargin;
			this.setLocation(isExpanded ? expanded : collapsed, y);
			break;
		case BIND_TOP:
			x = marginLeft;
			width = parent.getWidth() - marginLeft - marginRight;
			
			expanded = marginTop;
			collapsed = -height + collapseMargin;
			this.setLocation(x, isExpanded ? expanded : collapsed);
			break;
		case BIND_BOTTOM:
			x = marginLeft;
			width = parent.getWidth() - marginLeft - marginRight;
			
			expanded = parent.getHeight() - height - marginTop;
			collapsed = parent.getHeight() - collapseMargin;
			this.setLocation(x, isExpanded ? expanded : collapsed);
			break;
		}
		
		this.setSize(width, height);
	}
	
	private void commitGesture(double vx, double vy) {
		int x = this.getX();
		int y = this.getY();
		
		double inverse = 1.0d / (expanded - collapsed);
		double r = 0.05; 
		
		double threshold = 0;
		switch (bindSide) {
		  case BIND_LEFT:
		  case BIND_RIGHT: {
			  
			  double velocity = (vx*vx) / (2*r);
			  if (vx < 0) {
				  x -= velocity;
			  }
			  else {
				  x += velocity;
			  }
			  threshold = (x - collapsed) * inverse; 
		  }  break;
		  case BIND_TOP:
		  case BIND_BOTTOM: {
			  double velocity = (vy*vy) / (2*r);
			  
			  if (vx < 0) {
				  x -= velocity;
			  }
			  else {
				  x += velocity;
			  }
			  threshold = (y - collapsed) * inverse;
		  } break;
		}
		
		if (isExpanded) {
			this.setExpanded(threshold > gestureCollapseThreshold);
		}
		else {
			this.setExpanded(threshold > gestureExpandThreshold);
		}
	}
	
	private void setGestureLocation(int x, int y) {
		switch (bindSide) {
		  case BIND_LEFT:
			  y = getY();
			  if (x > expanded + gestureOverpull) {
				  x = expanded + gestureOverpull;
			  }
			  else if (x < collapsed - gestureOverpull) {
				  x = collapsed - gestureOverpull;
			  }
			  break;
		  case BIND_RIGHT:
			  y = getY();
			  if (x < expanded - gestureOverpull) {
				  x = expanded - gestureOverpull;
			  }
			  else if (x > collapsed + gestureOverpull) {
				  x = collapsed + gestureOverpull;
			  }
			  break;
		  case BIND_TOP:
			  x = getX();
			  if (y > expanded + gestureOverpull) {
				  y = expanded + gestureOverpull;
			  }
			  else if (y < collapsed - gestureOverpull) {
				  y = collapsed - gestureOverpull;
			  }
			  break;
		  case BIND_BOTTOM:
			  x = getX();
			  if (y < expanded - gestureOverpull) {
				  y = expanded - gestureOverpull;
			  }
			  else if (y > collapsed + gestureOverpull) {
				  y = collapsed + gestureOverpull;
			  }
			  break;
		}
		
		super.setLocation(x, y);
	}
	
	private static class GestureMouse implements MouseMotionListener, MouseListener {
		private int cx;
		private int cy;
		private int sx;
		private int sy;
		private int dx;
		private int dy;
		private SliderPanel slider;
		private long currentTime = -1;
		private long doubleClickTime = -1;
		private static final long DOUBLE_CLICK_THRESHOLD = 200;
		
		private GestureMouse(SliderPanel slider) {
			this.slider = slider;
		}
		
		
		@Override
		public void mouseDragged(MouseEvent e) {
			
			if (currentTime == -1) {
				currentTime = System.currentTimeMillis();
				sx = e.getXOnScreen();
				sy = e.getYOnScreen();
				cx = slider.getX();
				cy = slider.getY();
				
				return;
			}
			currentTime = System.currentTimeMillis();
			
			dx = e.getXOnScreen() - sx;
			dy = e.getYOnScreen() - sy;
			slider.setGestureLocation(cx + dx, cy + dy);
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - doubleClickTime < DOUBLE_CLICK_THRESHOLD) {
				slider.toggleExpanded();
			}
			
			doubleClickTime = System.currentTimeMillis();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			currentTime = System.currentTimeMillis();
			sx = e.getXOnScreen();
			sy = e.getYOnScreen();
			cx = slider.getX();
			cy = slider.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (currentTime != -1) {
				long deltaTime = System.currentTimeMillis() - currentTime;
				if (deltaTime == 0) {
					deltaTime = 1;
				}

				slider.commitGesture(dx/deltaTime, dy/deltaTime );
			}
			currentTime = -1;
		}
		
	}
}
package dipper.desktop.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;
import java.io.IOException;

import javax.swing.JPanel;

import dipper.app.DipperProject;
import dipper.desktop.ui.interpolate.TranslateComponent;

public class ProjectCanvas extends JPanel implements TranslateComponent {

	private static final long serialVersionUID = -9156651220918451578L;
	private static final String BACKGROUND_IMG_PATH = "images/mask.png";
	private static final String DOCUMENT_IMG_PATH = "images/documentBackground.png";
	
	private int topMargin = 15;
	private int bottomMargin = 20;
	private int leftMargin = 10;
	private int rightMargin = 10;
	
	private double translateX;
	private double translateY;
	private double scale;
	private static final double MAX_SCALE = 5;
	private static final double MIN_SCALE = 0.1;
	private static final double[] SCALE_LEVELS = { 0.2, 0.25, 0.3, 0.4, 0.5, 0.7, 1.0, 1.3, 1.6, 2.0, 2.4, 3.0, 3.5, 4.0, 4.5, 5.0};
	private int level = 5;
	
	private double x1;
	private double x2;
	private double y1;
	private double y2;
	
	private Color gridColor = new Color(.5f,.5f,.5f,0.3f);
	private Color blank = new Color(1f,1f,1f,0.0f);
	
	private VolatileImage image;
	private AffineTransform transform;
	private static BorderSprites maskSprites;
	
	private double gridDistance = 32;
	private boolean drawGrid = true;
	
	private DipperProject project = null;
	
	private static BorderSprites buttonSprites;

	private boolean imageDirty = true;
	
	private Insets inset;
	
	static {
		try {
			maskSprites = new BorderSprites();
			maskSprites.createSpritesFromResource(BACKGROUND_IMG_PATH, 64, 64, 64, 64);
			
			buttonSprites = new BorderSprites();
			buttonSprites.createSpritesFromResource(DOCUMENT_IMG_PATH, 32, 32, 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private UpdateSize sizeThread = new UpdateSize();
	
	public ProjectCanvas() {
		this.setOpaque(false);
		CanvasMouseListener listener = new CanvasMouseListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.addMouseWheelListener(listener);
		
		this.setBackground(new Color(1f,1f,1f,0.5f));
		this.setLayout(null);
		this.inset = new Insets(topMargin, leftMargin, bottomMargin, rightMargin);
		
		this.translateX = 0.0;
		this.translateY = 0.0;
		
		this.scale = 1.5;
		transform = new AffineTransform();
		recalcTransform();
		
		sizeThread.start();
	}
	
	@Override
	public Insets getInsets() {
		return inset;
	}
	
	private void scaleDelta(int click, double x, double y) {
		level += click;
		if (level >= SCALE_LEVELS.length) {
			level = SCALE_LEVELS.length - 1;
		}
		else if (level < 0) {
			level = 0;
		}
		
		scale(SCALE_LEVELS[level], x, y);
	}
	
	private void scale(double newScale, double x, double y) {
		if (newScale > MAX_SCALE) {
			newScale = MAX_SCALE;
		} 
		else if (newScale < MIN_SCALE) {
			newScale = MIN_SCALE;
		}
		
		double scaleFactor = newScale / scale;
		if (scaleFactor == 1) return;
		
		translateX = scaleFactor*(translateX - x) + x;
		translateY = scaleFactor*(translateY - y) + y;
		scale = newScale;
		
		recalcTransform();
		imageDirty();
	}
	
	private void recalcTransform() {
		transform.setToIdentity();
		transform.translate(translateX, translateY);
		transform.scale(scale, scale);
		
		x1 = -translateX/scale;
		y1 = -translateY/scale;
		x2 = getWidth()/scale + x1;
		y2 = getHeight()/scale + y1;
	}
	
	@Override
	public void doLayout() {
		recalcTransform();
		//this.setSize(getParent().getWidth() - leftMargin - rightMargin, getParent().getHeight() - topMargin - bottomMargin);
		
		//System.out.println("Do layout " + getParent());
		sizeThread.tick();
		repaint(15);
	}
	
	@Override
	public synchronized void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		if (image != null) {
			GraphicsConfiguration gc = this.getGraphicsConfiguration();
			if (imageDirty || image.validate(gc) == VolatileImage.IMAGE_RESTORED) {
				Graphics2D imageG2d = (Graphics2D)image.getGraphics();
				internalRedraw(imageG2d, false);
				imageDirty = false;
			}
			g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
	}
	
	private synchronized void internalRedraw(Graphics2D g2d, boolean framebuffer) {
		Composite oldComp = g2d.getComposite();
		int width = getWidth();
		int height = getHeight();
		if (!framebuffer){
			g2d.setComposite(AlphaComposite.Src);
			g2d.setColor(blank);
			g2d.fillRect(0, 0, width, height);
		}

		g2d.setComposite(AlphaComposite.SrcOver);
		AffineTransform old = g2d.getTransform();
		g2d.transform(transform);

		if (drawGrid) {
			g2d.setColor(gridColor);
			
			// Horizontal Lines
			double startX = Math.floor(y1/gridDistance)*gridDistance;
			double endX = Math.ceil(y2/gridDistance)*gridDistance;
			while (startX <= endX) {
				g2d.drawLine((int)x1, (int)startX, (int)x2, (int)startX);
				//g2d.fillRect((int)x1, (int)startX, (int)(x2-x1), 2);
				startX += gridDistance;
			}
			
			// Vertical Lines
			double startY = Math.floor(x1/gridDistance)*gridDistance;
			double endY = Math.ceil(x2/gridDistance)*gridDistance;
			while (startY <= endY) {
				//g2d.fillRect((int)startY, (int)y1, 2, (int)(y2-y1));
				g2d.drawLine((int)startY, (int)y1, (int)startY, (int)y2);
				startY += gridDistance;
			}
		}
		
		buttonSprites.drawBorderPanel(g2d, 128, 128);
		
		g2d.setTransform(old);
		
		if (!framebuffer){
			g2d.setComposite(AlphaComposite.DstIn);
			maskSprites.drawBorderPanel(g2d, width, height);
			g2d.setComposite(oldComp);
		}

	}
	
	private void imageDirty() {
		imageDirty = true;
		repaint(15);
	}
		
	void translateGraph(double deltaX, double deltaY) {
		if (Double.isNaN(deltaX) || Double.isNaN(deltaY) || Double.isInfinite(deltaX) || Double.isInfinite(deltaY)) {
			return;
		}
		translateX += deltaX;
		translateY += deltaY;

		recalcTransform();
		imageDirty();
	}
	
	public class CanvasMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {
		long currentTime = -1; 
		int px;
		int py;
		double dx;
		double dy;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mouse clicked");
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			currentTime = System.currentTimeMillis();
			px = e.getX();
			py = e.getY();
			//interp.setVelocity(0, 0);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
//			long time = (System.currentTimeMillis() - currentTime);
//			if (time > 100) {
//				return;
//			}
//			
//			//dx = e.getX() - px;
//			//dy = e.getY() - py;
//			//	System.out.println("D:" + dx+ "," + dy + " time:" + time);
//			double vx = dx / (time + 1);
//			double vy = dy / (time + 1);
//
//			interp.setVelocity(vx, vy);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			currentTime = System.currentTimeMillis();
			dx = e.getX() - px;
			dy = e.getY() - py;
			px = e.getX();
			py = e.getY();
			
			translateGraph(dx, dy);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int rotation = e.getWheelRotation();
			if (e.isMetaDown()) {
				scaleDelta(-rotation, e.getX(), e.getY());
			}
			else if (e.isShiftDown()) {
				translateGraph(-rotation*5, 0);
			}
			else {
				translateGraph(0, -rotation*5);
			}
		}
		
		
	}
	
	@Override
	public double getTranslateX() {
		// TODO Auto-generated method stub
		return translateX;
	}

	@Override
	public double getTranslateY() {
		// TODO Auto-generated method stub
		return translateY;
	}

	@Override
	public void setTranslate(double tx, double ty) {
		if (Double.isNaN(tx) || Double.isNaN(ty) || Double.isInfinite(tx) || Double.isInfinite(ty)) {
			return;
		}
		this.translateX = tx;
		this.translateY = ty;

		recalcTransform();
		imageDirty();
	}
	
	private class UpdateSize extends Thread {
		private boolean isRunning = true;
		private long time = 0;
		public void tick() {
			time = 100;
			this.interrupt();
		}
		
		public void run() {
			while(isRunning) {
				synchronized(this) {
					while (time > 0) {
						try {
							this.wait(time);
						} catch (InterruptedException e) {
							continue;
						}
						
						resizeIfNecessary();
					}
					
					try {
						wait();
					}
					catch (InterruptedException e) {	
					}
	
				}
			}
		}
	}
	
	private void resizeIfNecessary() {
		if (this.getWidth() <= 0 || this.getHeight() <= 0) {
			return;
		}
		if (image == null || image.getWidth() != this.getWidth() || image.getHeight() != this.getHeight()) {
			GraphicsConfiguration gc = this.getGraphicsConfiguration();
			image = gc.createCompatibleVolatileImage(this.getWidth(), this.getHeight(), VolatileImage.TRANSLUCENT);
			image.setAccelerationPriority(1.0f);
			image.validate(gc);
			imageDirty();
		}
	}

	public void setProject(DipperProject project) {
		this.project = project;
	}

	public DipperProject getProject() {
		return project;
	}
}
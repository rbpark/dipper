package dipper.desktop.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import dipper.desktop.ui.interpolate.DragVelocityInterpolator;
import dipper.desktop.ui.interpolate.TranslateComponent;

public class ProjectCanvas extends JPanel implements TranslateComponent {

	private static final long serialVersionUID = -9156651220918451578L;
	private static final String BACKGROUND_IMG_PATH = "images/mask.png";
	
	private int topMargin = 50;
	private int bottomMargin = 50;
	private int leftMargin = 50;
	private int rightMargin = 50;
	
	private double tx;
	private double ty;
	private double scale;
	
	private double x1;
	private double x2;
	private double y1;
	private double y2;
	
	private Color gridColor = new Color(0.0f,0.0f,0.0f,0.2f);
	private Color blank = new Color(1f,1f,1f,0.0f);
	
	private BufferedImage image;
	private static Image gridMask;
	private AffineTransform transform;
	private static BorderSprites maskSprites;
	private DragVelocityInterpolator interp;
	
	private BufferedImage[] borders = new BufferedImage[4];
	private double gridDistance = 100;
	private boolean drawGrid = true;
	private Rectangle bounds = new Rectangle();
	private Rectangle clip = null;
	
	static {
		try {
			InputStream backgroundStream = new BufferedInputStream(
					DipperMainPanel.class.getClassLoader().getResourceAsStream(BACKGROUND_IMG_PATH));
			Image gridUnsized = ImageIO.read(backgroundStream);
			
			int resizedWidth = 400;
			int resizedHeight = 400;
			
			gridMask = gridUnsized.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_AREA_AVERAGING);
			maskSprites = new BorderSprites();
			maskSprites.createSpritesFromResource(BACKGROUND_IMG_PATH, 64, 64, 64, 64);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ProjectCanvas() {
		this.setOpaque(false);
		CanvasMouseListener listener = new CanvasMouseListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.setBackground(new Color(1f,1f,1f,0.5f));
		this.setLayout(null);
		
		this.setLocation(leftMargin, topMargin);
		this.reposition();
		
		interp = new DragVelocityInterpolator(this);
		
		this.tx = 0.0;
		this.ty = 0.0;
		
		this.scale = 1.0;
		transform = new AffineTransform();
		recalcTransform();
	}
	
	private void recalcTransform() {
		transform.setToIdentity();
		transform.scale(scale, scale);
		transform.translate(tx, ty);
		
		x1 = -tx;
		y1 = -ty;
		x2 = getWidth() + x1;
		y2 = getHeight() + y2;
		
		clip = new Rectangle(64, 64, this.getWidth() - 128, this.getHeight() - 128);
	}
	
	@Override
	public void doLayout() {
		recalcTransform();
		image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		redraw();
	}
	
	@Override
	public synchronized void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Graphics2D imageG2d = (Graphics2D)image.getGraphics();
		internalRedraw(imageG2d, false);
		g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
//	
//		int width = this.getWidth();
//		int height = this.getHeight();
//
//		bounds = g2d.getClipBounds(bounds);
//		//bounds.intersection(r);
//		g2d.setClip(bounds.intersection(clip));
//		internalRedraw(g2d, true);
//		g2d.setClip(bounds);
//			
//		
//		// Upperside
//		g2d.drawImage(image, 0, 0, width, 64, 0, 0, width, 64, null);
//		// Leftside
//		g2d.drawImage(image, 0, 64, 64, height - 64,0, 64, 64, height - 64, null);
//		// Leftside
//		g2d.drawImage(image, width - 64, 64, width, height - 64,width - 64, 64, width, height - 64, null);
//		// bottom side
//		g2d.drawImage(image, 0, height - 64, width, height, 0, height - 64, width, height, null);
//		
	}
	
	private void redraw()  {
//		Graphics2D g2d = (Graphics2D)image.getGraphics();
//		internalRedraw(g2d, false);
		
		//Draw only the borders
//		g2d.setClip(0, 0, this.getWidth() - 128, 64);
//		internalRedraw(g2d, false);
//		
		repaint(15);
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
		
		g2d.setTransform(old);
		
		if (!framebuffer){
			g2d.setComposite(AlphaComposite.SrcIn);
			maskSprites.drawBorderPanel(g2d, width, height);
			g2d.setComposite(oldComp);
		}

	}

	public void reposition() {
		Component parent = getParent();
		if (parent != null) {
			this.setSize(getParent().getWidth() - leftMargin - rightMargin, getParent().getHeight() - topMargin - bottomMargin);
		}
	}
	
	public class CanvasMouseListener implements MouseListener, MouseMotionListener {
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
			interp.setVelocity(0, 0);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			long time = (System.currentTimeMillis() - currentTime);
			if (time > 100) {
				return;
			}
			
			//dx = e.getX() - px;
			//dy = e.getY() - py;
			//	System.out.println("D:" + dx+ "," + dy + " time:" + time);
			double vx = dx / (time + 1);
			double vy = dy / (time + 1);

			interp.setVelocity(vx, vy);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			currentTime = System.currentTimeMillis();
			dx = e.getX() - px;
			dy = e.getY() - py;
			px = e.getX();
			py = e.getY();
			
			setTranslate(tx + dx, ty + dy);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			
		}
		
	}

	@Override
	public double getTranslateX() {
		// TODO Auto-generated method stub
		return tx;
	}

	@Override
	public double getTranslateY() {
		// TODO Auto-generated method stub
		return ty;
	}

	@Override
	public void setTranslate(double tx, double ty) {
		if (Double.isNaN(tx) || Double.isNaN(ty) || Double.isInfinite(tx) || Double.isInfinite(ty)) {
			return;
		}
		this.tx = tx;
		this.ty = ty;

		recalcTransform();
		redraw();
		repaint(15);
	}
}
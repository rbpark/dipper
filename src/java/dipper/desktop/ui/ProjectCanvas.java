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
	
	private Color gridColor = new Color(0f,0f,0f,0.5f);
	private Color blank = new Color(1f,1f,1f,0.0f);
	
	private BufferedImage image;
	private static Image gridMask;
	private AffineTransform transform;
	private static BorderSprites maskSprites;
	private DragVelocityInterpolator interp;
	
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
	}
	
	@Override
	public void doLayout() {
		//image = createVolatileImage(this.getWidth(), this.getHeight() );
		image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//images[1] = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
	
		redraw();
	}
	
	@Override
	public synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		//internalRedraw(g2d);
	}
	
	private void redraw()  {
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		internalRedraw(g2d);
		//repaint(15);
	}
	
	private synchronized void internalRedraw(Graphics2D g2d) {
		Composite oldComp = g2d.getComposite();
		int width = getWidth();
		int height = getHeight();
		g2d.setComposite(AlphaComposite.Src);
		g2d.setColor(blank);
		g2d.fillRect(0, 0, width, height);

		AffineTransform old = g2d.getTransform();
		g2d.setTransform(transform);
		g2d.setColor(new Color(0f,0f,0f,1.0f));
		
		g2d.drawLine(0, 10, width, 10);
		g2d.fillRect(110, 110, 10, 10);

		g2d.setTransform(old);
		
		g2d.setComposite(AlphaComposite.SrcIn);
		maskSprites.drawBorderPanel(g2d, width, height);
		g2d.setComposite(oldComp);
	}

	public void reposition() {
		Component parent = getParent();
		if (parent != null) {
			this.setSize(getParent().getWidth() - leftMargin - rightMargin, getParent().getWidth() - topMargin - bottomMargin);
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
			if (System.currentTimeMillis() - currentTime > 100) {
				return;
			}
			double vx = dx / (System.currentTimeMillis() - currentTime);
			double vy = dy / (System.currentTimeMillis() - currentTime);
			
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
		System.out.println(tx +"," + ty);

		recalcTransform();
		redraw();
		repaint(15);
	}
}
package dipper.desktop.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import dipper.desktop.ui.interpolate.OpacityInterpolator;

public class DipperMenu extends JPanel implements OpacityComponent {
	private static final long serialVersionUID = 252808975643487376L;
	private static final String DIPPER_IMG_PATH = "images/dipper.png";
	private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
	private float radius = 200;
	private Point center = new Point(20, -110);
	private float startDegree = 35f*(float)(Math.PI/180f);
	private float endDegree = 85f*(float)(Math.PI/180f);
	
	private final Font font = new Font("SansSerif", Font.PLAIN, 16);
	private Color hoverColor = new Color(23,127,175);
	private Color fontColor = new Color(10,10,10);
	private Color disabledColor = new Color(0,0,0,64);
	
	private float menuOpacity = 1f;
	private boolean menuVisible = true;
	
	private static BufferedImage dipperImage;
	static {
		InputStream dipperStream = new BufferedInputStream(
				DipperMainPanel.class.getClassLoader().getResourceAsStream(DIPPER_IMG_PATH));
		try {
			dipperImage = ImageIO.read(dipperStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private OpacityInterpolator interpolator;
	private MenuMouseMotionListener menuListener;
	private DipperLogo logo;
	
	public DipperMenu() {
		this.setOpaque(false);
		this.setLayout(null);
		this.setSize(210,180);
		
		logo = new DipperLogo();
		logo.setLocation(10, 10);
		this.add(logo);
		
		interpolator = new OpacityInterpolator(this);
		menuListener = new MenuMouseMotionListener();
		this.setMenuVisibility(true);
	}
	
	public void toggleMenuVisiblity() {
		setMenuVisibility( !menuVisible);
	}
	
	public void setMenuVisibility(boolean menuVisiblity) {
		if (menuVisiblity == menuVisible) {
			return;
		}

		menuVisible = menuVisiblity;
		if (menuVisiblity) {
			this.addMouseListener(menuListener);
			this.addMouseMotionListener(menuListener);
			interpolator.setOpacity(1, true);
		}
		else {
			this.removeMouseListener(menuListener);
			this.removeMouseMotionListener(menuListener);
			interpolator.setOpacity(0, true);
		}
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public void addMenuItem(MenuItem item) {
		menuItems.add(item);
		layoutPositions();
	}
	
	public void layoutPositions() {
		int numSteps = menuItems.size() > 1 ? menuItems.size() - 1 : 1;
		float degreeStep = (endDegree - startDegree)/numSteps;
		float currentDegree = startDegree;
		
		for (MenuItem menuItem: menuItems) {
			AffineTransform transform = new AffineTransform();

			transform.translate(center.x, center.y);
			transform.rotate(currentDegree);
			transform.translate(radius, 0);
			menuItem.setTransform(transform);
			
			currentDegree += degreeStep;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
	
		if (menuOpacity == 0) {
			return;
		}
		
		Composite old = null;
		if (menuOpacity != 1) {
			old = g2d.getComposite();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, menuOpacity));
		}
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.setFont(font);
		AffineTransform transform = g2d.getTransform();
		
		for (MenuItem menuItem: menuItems) {
			if (!menuItem.isDimensionSet()) {
				menuItem.calculatePossibleDimension(g2d, font);
			}
			
			g2d.setTransform(menuItem.getTransform());
	
			if (menuItem.isHover()) {		
				int offsetX = -1;
				int offsetY = -1;
				if (menuItem.isPressed()) {
					offsetX = 0;
					offsetY = 0;
				}
				g2d.setColor(hoverColor);
				g2d.drawString(menuItem.getName(), offsetX, menuItem.getHeight() + offsetY);
			}
			else {
				if (menuItem.isEnabled) {
					g2d.setColor(fontColor);
				}
				else {
					g2d.setColor(disabledColor);
				}
				g2d.drawString(menuItem.getName(), 0, menuItem.getHeight());
			}
		}
		
		if (old != null) {
			g2d.setComposite(old);
		}
		g2d.setTransform(transform);
	}
	
	@SuppressWarnings("serial")
	private class DipperLogo extends JPanel implements MouseListener {
		private DipperLogo() {
			super();
			this.setOpaque(false);
			this.setSize(dipperImage.getWidth(), dipperImage.getHeight());
			this.addMouseListener(this);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			g2d.drawImage(dipperImage, 0, 0, null);
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//toggleMenuVisiblity();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			//this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setMenuVisibility(true);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			//this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
		
	}
	
	public static class MenuItem {
		private String name;
		private boolean isEnabled = true;
		private AffineTransform transform;
		private int width = -1;
		private int height = -1;
		private boolean isHover = false;
		private boolean isPressed = false;
		private static final int threshold = 4;
		
		public MenuItem(String name) {	
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public void calculatePossibleDimension(Graphics g, Font font) {			
			FontMetrics metrics = g.getFontMetrics(font);
			this.height = metrics.getHeight();
			this.width = metrics.stringWidth(name);
		}
		
		public boolean isDimensionSet() {
			return this.height > 0;
		}
		
		public void setTransform(AffineTransform transform) {
			this.transform = transform;
		}
		
		public AffineTransform getTransform() {
			return transform;
		}
		
		public void setEnabled(boolean enabled) {
			isEnabled = enabled;
		}
		
		public boolean isEnabled() {
			return isEnabled;
		}
	
		public boolean isHover() {
			return isHover;
		}
		
		public void setHover(boolean hover) {
			isHover = hover;
			if (!isHover) {
				isPressed = false;
			}
		}
		
		public void setPressed(boolean pressed) {
			isPressed = pressed;
		}
		
		public boolean isPressed() {
			return isPressed;
		}
		
		public boolean isPointWithin(double x, double y) {
			return x > -threshold && y > -threshold && x < width + threshold && y < height + threshold;
		}
	}
	
	private class MenuMouseMotionListener implements MouseMotionListener, MouseListener {
		private Point dest = new Point();
		private MenuItem hovered = null;
		
		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (hovered != null) {
				try {
					hovered.getTransform().inverseTransform(e.getPoint(), dest);
				} catch (NoninvertibleTransformException e1) {
				}
				
				if (hovered.isPointWithin(dest.getX(), dest.getY())) {
					return;
				}
				else {
					hovered.setHover(false);
					hovered = null;
					repaint(15);
				}
			}
			
			for (MenuItem menuItem : menuItems) {
				if (menuItem.isEnabled()) {
					try {
						menuItem.getTransform().inverseTransform(e.getPoint(), dest);
						if (menuItem.isPointWithin(dest.getX(), dest.getY())) {
							menuItem.setHover(true);
							hovered = menuItem;
							repaint(15);
						}
					} catch (NoninvertibleTransformException e1) {
						throw new RuntimeException(e1);
					}
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (hovered != null) {
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			if (!contains(e.getPoint())) {
				setMenuVisibility(false);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (hovered != null) {
				hovered.setPressed(true);
				repaint(15);
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (hovered != null) {
				hovered.setPressed(false);
				repaint(15);
				
				try {
					hovered.getTransform().inverseTransform(e.getPoint(), dest);
				} catch (NoninvertibleTransformException e1) {
				}
				
				if (!hovered.isPointWithin(dest.getX(), dest.getY())) {
					hovered.setHover(false);
					hovered = null;
				}
				else {
					System.out.println("Hover " + hovered.getName() + " released.");
				}
			}
		}
	}


	@Override
	public void setOpacity(float f) {
		// TODO Auto-generated method stub
		this.menuOpacity = f;
		repaint(15);
	}

	@Override
	public float getOpacity() {
		return menuOpacity;
	}
}
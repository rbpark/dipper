package dipper.desktop.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class DipperMainPanel extends JPanel {
	private static final long serialVersionUID = 7143502855240909560L;
	private static final String BACKGROUND_IMG_PATH = "images/background.png";
	private Image backgroundImage;
	private BufferedImage resizedImageBackground;
	
	private static final int RIGHT_MENU_WIDTH = 300;
	
	private SliderPanel rightPanel;
	private BottomPanel bottomPanel;
	private DocumentPanel documentPanel;
	private DipperMenu dipperMenu;
	
	public DipperMainPanel() {
		this.setLayout(null);
		this.setDoubleBuffered(true);
		try {
			InputStream backgroundStream = new BufferedInputStream(
					DipperMainPanel.class.getClassLoader().getResourceAsStream(BACKGROUND_IMG_PATH));
			backgroundImage = ImageIO.read(backgroundStream);
			
			resizedImageBackground = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = resizedImageBackground.createGraphics();
			g.drawImage(backgroundImage, 0, 0, 256, 256, null);
			g.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dipperMenu = new DipperMenu();
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("new"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("open"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("action"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("preferences"));	
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("save as"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("save"));
		this.add(dipperMenu);
		dipperMenu.setMenuVisibility(false);
		
		rightPanel = new SliderPanel(SliderPanel.BIND_RIGHT);
		rightPanel.setDimension(RIGHT_MENU_WIDTH);
		rightPanel.setMargins(10, 0, 40, 310);
		this.add(rightPanel);
		
		bottomPanel = new BottomPanel();
		this.add(bottomPanel);
		
//		documentPanel = new DocumentPanel();
//		documentPanel.setLocation(100, 100);
//		documentPanel.setSize(800, 600);
//		this.add(documentPanel);
	}
	
	public void doLayout() {
		super.doLayout();
		rightPanel.reposition();
		bottomPanel.reposition();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;
		int width = this.getWidth();
		int height = this.getHeight();
		
		g2d.drawImage(resizedImageBackground, 0, -4, width, height+4, Color.WHITE, null);
		//g2d.drawImage(dipperImage, 10, 10, null);
	}
	
}
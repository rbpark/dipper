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
	private static final String DIPPER_IMG_PATH = "images/dipper.png";
	private static final String SPRITE_BORDER_IMG_PATH = "images/slideMenu.png";
	private Image backgroundImage;
	private Image dipperImage;
	private BufferedImage resizedImageBackground;
	private BorderSprites sprites;
	
	private static final int RIGHT_MENU_WIDTH = 300;
	private static final int BOTTOM_MENU_HEIGHT = 300;
	
	private SliderPanel rightPanel;
	private SliderPanel bottomPanel;
	
	public DipperMainPanel() {
		this.setLayout(null);
		this.setDoubleBuffered(true);
		try {
			InputStream backgroundStream = new BufferedInputStream(
					DipperMainPanel.class.getClassLoader().getResourceAsStream(BACKGROUND_IMG_PATH));
			backgroundImage = ImageIO.read(backgroundStream);
			
			InputStream dipperStream = new BufferedInputStream(
					DipperMainPanel.class.getClassLoader().getResourceAsStream(DIPPER_IMG_PATH));
			dipperImage = ImageIO.read(dipperStream);
			
			resizedImageBackground = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = resizedImageBackground.createGraphics();
			g.drawImage(backgroundImage, 0, 0, 256, 256, null);
			g.dispose();

			sprites = new BorderSprites();
			sprites.createSpritesFromResource(SPRITE_BORDER_IMG_PATH, 32, 32, 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}


		rightPanel = new SliderPanel(SliderPanel.BIND_RIGHT);
		rightPanel.setDimension(RIGHT_MENU_WIDTH);
		rightPanel.setMargins(10, 0, 40, 310);
		rightPanel.setBorderSprites(sprites);
		this.add(rightPanel);
		
		bottomPanel = new SliderPanel(SliderPanel.BIND_BOTTOM);
		bottomPanel.setDimension(BOTTOM_MENU_HEIGHT);
		bottomPanel.setMargins(0, 0, 0, 0);
		bottomPanel.setBorderSprites(sprites);
		this.add(bottomPanel);
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
		g2d.drawImage(dipperImage, 10, 10, null);
	}
	
}
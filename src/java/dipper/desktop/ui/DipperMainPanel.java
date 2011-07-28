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
	private BufferedImage resizedImage;
	
	public DipperMainPanel() {
		this.setLayout(null);
		this.setDoubleBuffered(true);
		try {
			InputStream stream = new BufferedInputStream(
					DipperMainPanel.class.getClassLoader().getResourceAsStream(BACKGROUND_IMG_PATH));

			backgroundImage = ImageIO.read(stream);
			
			resizedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(backgroundImage, 0, 0, 256, 256, null);
			g.dispose();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;
		int width = this.getWidth();
		int height = this.getHeight();
		
		g2d.drawImage(resizedImage, 0, 0, width, height, Color.DARK_GRAY, null);
	}
	
}
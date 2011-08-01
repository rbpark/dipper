package dipper.desktop.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class BorderSprites {
	private BufferedImage upperLeftCorner;
	private BufferedImage upperRightCorner;
	private BufferedImage upperMiddleSide;
	
	private BufferedImage lowerLeftCorner;
	private BufferedImage lowerRightCorner;
	private BufferedImage lowerMiddleSide;
	
	private BufferedImage middleLeftSide;
	private BufferedImage middleRightSide;
	private BufferedImage middle;
	
	private BufferedImage mainImage;
	
	public BorderSprites() {
	}

	public void createSpritesFromResource(String resource, int left, int right, int top, int bottom, int resizeW, int resizeH) throws IOException {		
		InputStream backgroundStream = new BufferedInputStream(
				DipperMainPanel.class.getClassLoader().getResourceAsStream(resource));
		
		BufferedImage mainImage = ImageIO.read(backgroundStream);
		BufferedImage scaledImage = (BufferedImage)mainImage.getScaledInstance(resizeW, resizeH, Image.SCALE_AREA_AVERAGING);
		if (mainImage == null) {
			throw new IOException("Image " + resource + " not found.");
		}
		createSpritesFromImage(scaledImage, left, right, top, bottom);	
	}

	
	/**
	 * Cuts up the image from the resource
	 * 
	 * @param resource
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @throws IOException 
	 */
	public void createSpritesFromResource(String resource, int left, int right, int top, int bottom) throws IOException {
		InputStream backgroundStream = new BufferedInputStream(
				DipperMainPanel.class.getClassLoader().getResourceAsStream(resource));
		
		BufferedImage mainImage = ImageIO.read(backgroundStream);
	
		if (mainImage == null) {
			throw new IOException("Image " + resource + " not found.");
		}
		createSpritesFromImage(mainImage, left, right, top, bottom);	
	}
	
	public void createSpritesFromImage(BufferedImage image, int left, int right, int top, int bottom) {
		mainImage = image;
		int imageWidth = mainImage.getWidth(null);
		int imageHeight = mainImage.getHeight(null);

		int x1 = left;
		int x2 = imageWidth - right;
		int y1 = top;
		int y2 = imageHeight - bottom;
		
		int leftWidth = left;
		int middleWidth = x2 - x1;
		int rightWidth = right;
		
		int topHeight = top;
		int middleHeight = y2 - y1;
		int lowerHeight = bottom;
		
		
		upperLeftCorner = mainImage.getSubimage(0, 0, leftWidth, topHeight);
		upperMiddleSide = mainImage.getSubimage(x1, 0, middleWidth, topHeight);
		upperRightCorner = mainImage.getSubimage(x2, 0, rightWidth, topHeight);

		middleLeftSide = mainImage.getSubimage(0, y1, leftWidth, middleHeight);
		middle = mainImage.getSubimage(x1, y1, middleWidth, middleHeight);
		middleRightSide = mainImage.getSubimage(x2, y1, rightWidth, middleHeight);
		
		lowerLeftCorner = mainImage.getSubimage(0, y2, leftWidth, lowerHeight);
		lowerMiddleSide = mainImage.getSubimage(x1, y2, middleWidth, lowerHeight);
		lowerRightCorner = mainImage.getSubimage(x2, y2, rightWidth, lowerHeight);

	}
	
	public void drawBorderPanel(Graphics g, int width, int height) {
		int x1 = upperLeftCorner.getWidth();
		int x2 = width - upperRightCorner.getWidth();
		int y1 = upperLeftCorner.getHeight();
		int y2 = height - lowerLeftCorner.getHeight();
		
		int middleWidth = x2 - x1;
		int middleHeight = y2 - y1;
		
		// Draw top left corner
		g.drawImage(upperLeftCorner, 0, 0, null);
		g.drawImage(upperMiddleSide, x1, 0, middleWidth, y1, null);
		g.drawImage(upperRightCorner, x2, 0, null);
		
		g.drawImage(middleLeftSide, 0, y1, x1, middleHeight, null);
		g.drawImage(middle, x1, y1, middleWidth, middleHeight, null);
		g.drawImage(middleRightSide, x2, y1, middleRightSide.getWidth(), middleHeight, null);
		
		g.drawImage(lowerLeftCorner, 0, y2, null);
		g.drawImage(lowerMiddleSide, x1, y2, middleWidth, lowerMiddleSide.getHeight(), null);
		g.drawImage(lowerRightCorner, x2, y2, null);
	}
}
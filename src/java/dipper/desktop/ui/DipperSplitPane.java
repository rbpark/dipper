package dipper.desktop.ui;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class DipperSplitPane extends JPanel {
	private static final String DIVIDER_IMG_PATH = "images/divider.png";
	private JComponent left;
	private JComponent right;
	private int position = -1;
	private int dividerWidth = 10;
	
	private static BufferedImage dividerImage;
	static {
		InputStream dipperStream = new BufferedInputStream(
				DipperMainPanel.class.getClassLoader().getResourceAsStream(DIVIDER_IMG_PATH));
		try {
			dividerImage = ImageIO.read(dipperStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DipperSplitPane(JComponent left, JComponent right) {
		this(left, right, -1);
	}
	
	public DipperSplitPane(JComponent left, JComponent right, int position) {
		this.setLayout(null);
		this.setOpaque(false);
		this.left = left;
		this.right = right;
		this.position = position;
		this.add(left);
		this.add(right);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(dividerImage, position, getHeight()/2, null);
	}
	
	@Override
	public void doLayout() {
		if (position == -1) {
			position = getWidth() / 2;
		}
		else {
			if (position > getWidth()) {
				layoutPanel(left, 0, 0, getWidth() - dividerWidth, getHeight());
				layoutPanel(right, getWidth(), 0, 1, getHeight());
			}
			else {
				layoutPanel(left, 0, 0, position - dividerWidth, getHeight());
				layoutPanel(right, position, 0, getWidth() - position, getHeight());
			}
		}
	}
	
	private void layoutPanel(JComponent comp, int x, int y, int width, int height) {
		if (comp == null) {
			return;
		}
		Insets inset = comp.getInsets();
		if (inset != null) {
			x += inset.left;
			y += inset.top;
			width -= (inset.right + inset.left);
			height -= (inset.bottom + inset.top);
		}
		
		comp.setLocation(x, y);
		comp.setSize(width, height);
	}
}
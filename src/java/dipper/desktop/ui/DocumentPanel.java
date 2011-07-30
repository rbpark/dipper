package dipper.desktop.ui;

import java.awt.Graphics;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.io.IOException;

import javax.swing.JPanel;

public class DocumentPanel extends JPanel {

	private static final long serialVersionUID = -9156651220918451578L;
	private static final String DOCUMENT_BORDER_IMG_PATH = "images/documentBackground.png";
	private static BorderSprites defaultSprites = null;
	private BorderSprites sprites = defaultSprites;
	static {
		defaultSprites = new BorderSprites();
		try {
			defaultSprites.createSpritesFromResource(DOCUMENT_BORDER_IMG_PATH, 32, 32, 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DocumentPanel() {
		this.setOpaque(false);
	}
	
	public void setBorderSprites(BorderSprites sprites) {
		this.sprites = sprites;
		this.setOpaque(false);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (sprites != null) {
			sprites.drawBorderPanel(g, this.getWidth(), this.getHeight());
		}
	}
	
	public class MyListener implements DragGestureListener {



		@Override
		public void dragGestureRecognized(DragGestureEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println(arg0);
		}
		
	}
}
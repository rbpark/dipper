package dipper.desktop.ui;

import java.awt.Graphics;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;

import javax.swing.JPanel;

public class DocumentPanel extends JPanel {
	private BorderSprites sprites = null;
	public DocumentPanel() {
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
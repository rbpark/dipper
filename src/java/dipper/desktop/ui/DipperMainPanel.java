package dipper.desktop.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import dipper.app.DipperAppController;
import dipper.app.DipperProject;
import dipper.app.event.WorkspaceChangedEvent;
import dipper.app.event.WorkspaceChangedListener;
import dipper.desktop.ui.DipperMenu.MenuItem;
import dipper.desktop.ui.event.DipperMenuListener;

public class DipperMainPanel extends JPanel implements WorkspaceChangedListener, DipperMenuListener {
	private static final long serialVersionUID = 7143502855240909560L;
	private static final String BACKGROUND_IMG_PATH = "images/background.png";
	private BufferedImage backgroundImage;
	private BufferedImage resizedImageBackground;
	
	private static final int RIGHT_MENU_WIDTH = 300;
	
	private SliderPanel rightPanel;
	private BottomPanel bottomPanel;
	private ProjectCanvas documentPanel;
	private DipperMenu dipperMenu;
	
	private DipperAppController appController;
	private DipperMenu.MenuItem saveAs;
	private DipperMenu.MenuItem save;
	
	public DipperMainPanel(DipperAppController controller) {
		this.setLayout(null);
		this.setDoubleBuffered(true);
		
		appController = controller;
		appController.addWorkspaceChangeListener(this);
		
		try {
			InputStream backgroundStream = new BufferedInputStream(
					DipperMainPanel.class.getClassLoader().getResourceAsStream(BACKGROUND_IMG_PATH));
			backgroundImage = ImageIO.read(backgroundStream);
			resizedImageBackground = backgroundImage;
//			resizedImageBackground = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g = resizedImageBackground.createGraphics();
//			g.drawImage(backgroundImage, 0, 0, 256, 256, null);
//			g.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dipperMenu = new DipperMenu();
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("new"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("open"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("action"));
		dipperMenu.addMenuItem(new DipperMenu.MenuItem("preferences"));	
		saveAs = new DipperMenu.MenuItem("save as");
		save = new DipperMenu.MenuItem("save");
		dipperMenu.addMenuItem(saveAs);
		dipperMenu.addMenuItem(save);
		dipperMenu.addMenuListener(this);
		
		this.add(dipperMenu);
		dipperMenu.setMenuVisibility(false);
		
		rightPanel = new SliderPanel(SliderPanel.BIND_RIGHT);
		rightPanel.setDimension(RIGHT_MENU_WIDTH);
		rightPanel.setMargins(10, 0, 40, 250);
		rightPanel.setExpanded(false);
		this.add(rightPanel);
		

		bottomPanel = new BottomPanel();
		bottomPanel.setExpanded(false);
		this.add(bottomPanel);
		
		documentPanel = new ProjectCanvas();
		this.add(documentPanel);
		
		resetPanels();
	}

	public void resetPanels() {
		DipperProject proj = appController.getActiveProject();
		if (proj == null) {
			bottomPanel.setVisible(false);
			documentPanel.setVisible(false);
			rightPanel.setVisible(false);
			saveAs.setEnabled(false);
			save.setEnabled(false);
		}
		else {
			bottomPanel.setVisible(true);
			documentPanel.setVisible(true);
			rightPanel.setVisible(true);
			saveAs.setEnabled(true);
			save.setEnabled(true);
		}
	}
	
	public void doLayout() {
		super.doLayout();
		rightPanel.reposition();
		bottomPanel.reposition();
		if (documentPanel != null) {
			documentPanel.reposition();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;
		int width = this.getWidth();
		int height = this.getHeight();
		
		g2d.drawImage(resizedImageBackground, 0, -4, width, height+4, Color.WHITE, null);
	}

	@Override
	public void workspaceChanged(WorkspaceChangedEvent e) {
		resetPanels();
	}

	@Override
	public void menuItemSelected(MenuItem item) {
		if (item.getName().equals("new")) {
			appController.newProject("New Project");
		}
	}

}
package dipper.desktop;

import java.awt.Color;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dipper.desktop.ui.DipperMainPanel;
import dipper.desktop.ui.interpolate.PositionInterpolator;
import dipper.utils.PropertyUtils;

@SuppressWarnings("serial")
public class DipperFrame extends JFrame {
	private static final String WIDTH_PROPERTY = "width";
	private static final String HEIGHT_PROPERTY = "height";
	
	public DipperFrame(Properties props) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		extractProperties(props);
		setupPanels();
		
		this.setVisible(true);
	}
	
	private void setupPanels() {
		DipperMainPanel mainPain = new DipperMainPanel();
		this.setContentPane(mainPain);
		
		JPanel panel = new JPanel();
		panel.setSize(200, 500);
		panel.setLocation(50, 100);
		panel.setBackground(Color.black);
		panel.setDoubleBuffered(true);
		mainPain.add(panel);
		
		PositionInterpolator interp = new PositionInterpolator(panel);
		interp.setStepsPerSecond(1200);
		interp.setPosition(900, 400, true);
	}
	
	private void extractProperties(Properties props) {
		int width = PropertyUtils.getInt(props, WIDTH_PROPERTY, 800);
		int height = PropertyUtils.getInt(props, HEIGHT_PROPERTY, 600);
		
		this.setSize(width, height);
	}
	
	public void fillProperties(Properties props) {
		props.put(WIDTH_PROPERTY, Integer.toString(this.getWidth()));
		props.put(HEIGHT_PROPERTY,Integer.toString(this.getHeight()));
	}
}
package dipper.desktop;

import java.awt.Color;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
		JPanel mainPain = new JPanel();
		mainPain.setDoubleBuffered(true);
		mainPain.setLayout(null);
		this.setContentPane(mainPain);
		
		JPanel panel = new JPanel();
		panel.setSize(100, 100);
		panel.setLocation(100, 100);
		panel.setBackground(Color.black);
		panel.setDoubleBuffered(true);
		mainPain.add(panel);
		
		PositionInterpolator interp = new PositionInterpolator(panel);
		interp.setStepsPerSecond(750);
		interp.setPosition(700, 500, true);
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
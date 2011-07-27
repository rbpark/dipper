package dipper.desktop;

import java.util.Properties;

import javax.swing.JFrame;

import dipper.utils.PropertyUtils;

@SuppressWarnings("serial")
public class DipperFrame extends JFrame {
	private static final String WIDTH_PROPERTY = "width";
	private static final String HEIGHT_PROPERTY = "height";
	
	public DipperFrame(Properties props) {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		extractProperties(props);

		this.setVisible(true);
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
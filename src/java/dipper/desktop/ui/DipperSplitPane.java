package dipper.desktop.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class DipperSplitPane extends JPanel {
	private JComponent left;
	private JComponent right;
	private int position = -1;
	private int dividerWidth = 10;
	
	public DipperSplitPane(JComponent left, JComponent right) {
		this(left, right, -1);
	}
	
	public DipperSplitPane(JComponent left, JComponent right, int position) {
		this.left = left;
		this.right = right;
		this.position = position;
	}
	
	@Override
	public void doLayout() {
		if (position == -1) {
			position = getWidth() / 2;
		}
		else {
		}
	}
}
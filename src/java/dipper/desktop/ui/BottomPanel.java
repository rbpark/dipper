package dipper.desktop.ui;

public class BottomPanel extends SliderPanel {
	private static final long serialVersionUID = -5447661869146685488L;
	private static final int BOTTOM_MENU_HEIGHT = 300;
	
	public BottomPanel() {
		super(BIND_BOTTOM);
		this.setDimension(BOTTOM_MENU_HEIGHT);
		this.setMargins(0, 0, 0, 0);
	}
	
}
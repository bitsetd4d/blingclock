package blingclock.visualiser;

import javax.swing.JPanel;


public abstract class VisualiserPanel extends JPanel implements TimeVisualiser {

	protected TimeSelectionListener listener;
	protected boolean ticking = false;
	protected boolean updating = true;
	protected boolean showRegistered = false;
	
	public void setListener(TimeSelectionListener listener) {
		this.listener = listener;
	}
	
	public void setTicking(boolean ticking) {
		this.ticking = ticking;
	}

	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	public void setShowNeedsRegistered(boolean showRegistered) {
		this.showRegistered = showRegistered;
	}

}

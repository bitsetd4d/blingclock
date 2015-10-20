package countdowntimer.tweaks;

import javax.swing.JFrame;

import countdowntimer.visualiser.SingleTimeBarVisualiserPanel;

import blingclock.visualiser.TimeBarVisualiserPanel;


public class TweakPanelControl {
	
	private TweakPanel tweakPanel;
	
	public void open(JFrame jframe,final int x,final int y,final int w,final int h) {
		if (tweakPanel == null) {
			tweakPanel = new TweakPanel();			
			tweakPanel.setTweakPanelControl(this);
	    	jframe.getLayeredPane().add(tweakPanel,new Integer(998));
	    	tweakPanel.setLocation(x,y);
	    	tweakPanel.setSize(w,h);
	    	tweakPanel.setVisible(true);
	    	jframe.getLayeredPane().validate();
	    	TimeBarVisualiserPanel.globalDisableMouseMonitoring();  
	    	SingleTimeBarVisualiserPanel.globalDisableMouseMonitoring();
	    	jframe.repaint();
		} else {
			closeExistingPanel();
		}
	}
	
	private void closeExistingPanel() { 
		tweakPanel.closePanel();
		tweakPanel = null;
    	TimeBarVisualiserPanel.globalEnableMouseMonitoring();  
    	SingleTimeBarVisualiserPanel.globalEnableMouseMonitoring();
	}

	public void panelClosed(TweakPanel tweakPanel) {
		if (this.tweakPanel == tweakPanel) {
			this.tweakPanel = null;
	    	TimeBarVisualiserPanel.globalEnableMouseMonitoring();  
	    	SingleTimeBarVisualiserPanel.globalEnableMouseMonitoring();
		}
	}
	
}

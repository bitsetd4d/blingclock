package countdowntimer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.Group;

import blingclock.controls.GlowButton;
import blingclock.digits.DigitDecorationPanel;


public class LayoutUtil {
	
	// Options panel buttons
	private GlowButton muteButton;
	//private GlowButton loopButton;
	private GlowButton fracButton;
	private GlowButton subModeButton;
	private GlowButton tweakButton;
	private GlowButton timeModeButton;
	private GlowButton onTopButton;
	
	private GlowButton prefsButton; 
	private GlowButton visualiseButton; 
	private GlowButton miniButton; 
	private GlowButton fullButton; 
	private GlowButton compactButton; 
	private GlowButton removeTitlebarButton; 
	private GlowButton buyButton; 
	private GlowButton debugButton1; 
	
	private GlowButton pauseButton;
	private GlowButton playButton;
	private GlowButton resetButton; 
	
	private GlowButton filler = new GlowButton(""); //$NON-NLS-1$
	private GlowButton filler2 = new GlowButton(""); //$NON-NLS-1$

	public void setControlButtons(GlowButton pauseButton,GlowButton playButton,GlowButton resetButton) { 
		this.pauseButton = pauseButton;
		this.playButton = playButton;
		this.resetButton = resetButton;
	}

	public void setOptionsButtons(GlowButton muteButton, /*GlowButton loopButton,*/ GlowButton fracButton, GlowButton subModeButton, GlowButton timeModeButton, GlowButton onTopButton) {
		this.muteButton = muteButton;
		//this.loopButton = loopButton;
		this.fracButton = fracButton;
		this.subModeButton = subModeButton;
		this.timeModeButton = timeModeButton;
		this.onTopButton = onTopButton;
	}
	
	public void setModeButtons(GlowButton prefsButton,GlowButton visualiseButton,GlowButton miniButton,GlowButton fullButton,GlowButton compactButton,GlowButton removeTitlebarButton, GlowButton buyButton,GlowButton debugButton1) {
		this.prefsButton = prefsButton; 
		this.visualiseButton = visualiseButton; 
		this.miniButton = miniButton; 
		this.fullButton = fullButton; 
		this.compactButton = compactButton; 
		this.removeTitlebarButton = removeTitlebarButton; 
		this.buyButton = buyButton; 
		this.debugButton1 = debugButton1; 
	}

	public void layoutOptionsPanel(JPanel optionsButtonPanel,boolean fullScreenMode) {
	    GroupLayout optionLayout = new GroupLayout(optionsButtonPanel);
		optionsButtonPanel.setLayout(optionLayout);
		optionsButtonPanel.setBackground(new Color(0, 0, 0));

		final int RHW = 37;
		final int BH =  15;
		
		optionsButtonPanel.removeAll();

		if (fullScreenMode) {
			Group g1 = optionLayout.createParallelGroup()
				.add(muteButton, RHW, RHW, RHW)
				.add(fracButton, RHW, RHW, RHW)
				.add(filler, RHW, RHW, RHW)
				.add(timeModeButton, RHW, RHW, RHW)
				.add(subModeButton, RHW, RHW, RHW);
				
			Group g2 = optionLayout.createSequentialGroup()
				.add(muteButton, BH, BH, BH)
				.add(fracButton, BH, BH, BH)
				.add(filler, BH, BH, BH)
				.add(timeModeButton, BH, BH, BH)
				.add(subModeButton, BH, BH, BH);
			
			optionLayout.setHorizontalGroup(g1);
			optionLayout.setVerticalGroup(g2);

		} else {
_MARKER.mark();			
			Group g1 = optionLayout.createParallelGroup()
				.add(muteButton, RHW, RHW, RHW)
				.add(fracButton, RHW, RHW, RHW)
				.add(onTopButton, RHW, RHW, RHW)
				.add(filler, RHW, RHW, RHW)
				.add(timeModeButton, RHW,RHW, RHW)
				.add(subModeButton, RHW,RHW, RHW)
				.add(tweakButton, RHW, RHW, RHW);
			Group g2 = optionLayout.createSequentialGroup()
				.add(muteButton, BH, BH, BH)
				.add(onTopButton, BH, BH, BH)
				.add(fracButton, BH, BH, BH)
				.add(filler, BH, BH, BH)
				.add(timeModeButton, BH, BH, BH)
				.add(subModeButton, BH, BH, BH)
				.add(tweakButton, BH, BH, BH);
			
			optionLayout.setHorizontalGroup(g1);
			optionLayout.setVerticalGroup(g2);
		}
	}
	

	public void layoutBelowDigitsPanel(DigitDecorationPanel belowDigitsPanel,boolean fullScreenMode) {
		List<GlowButton> list = new ArrayList<GlowButton>();
		list.add(muteButton.makeLinkedCopy());
		if (!fullScreenMode) list.add(onTopButton.makeLinkedCopy());
		list.add(fracButton.makeLinkedCopy());
		list.add(filler.makeLinkedCopy());
		list.add(timeModeButton.makeLinkedCopy());
		list.add(subModeButton.makeLinkedCopy());
_MARKER.mark();		
		list.add(tweakButton.makeLinkedCopy());

		for (GlowButton b : list) {
			b.setHorizontalAlignment(SwingConstants.CENTER);
		}
		belowDigitsPanel.setupButtons(list);
		
		list = new ArrayList<GlowButton>();
		list.add(playButton.makeLinkedCopy());
		list.add(pauseButton.makeLinkedCopy());
		list.add(resetButton.makeLinkedCopy());
		belowDigitsPanel.setupControls(list);
	}

	public void layoutModePanel(JPanel modeButtonPanel, boolean fullScreenMode) {
        GroupLayout modeLayout = new GroupLayout(modeButtonPanel);
        modeButtonPanel.setLayout(modeLayout);
        modeButtonPanel.setBackground(new Color(0,0,0));
        
        final int BH = 15;
_MARKER.mark();		
        if (fullScreenMode) {
	        modeLayout.setHorizontalGroup(modeLayout.createParallelGroup()
	            		.add(prefsButton,40,40,40)
	            		.add(fullButton,40,40,40)
	            		.add(compactButton,40,40,40)
	            		.add(visualiseButton,40,40,40)
//	            		.add(debugButton1,40,40,40)
	            		);
	
		    modeLayout.setVerticalGroup(modeLayout.createSequentialGroup()
	            			.add(prefsButton,BH,BH,BH)
	            			.add(fullButton,BH,BH,BH)
	            			.add(compactButton,40,40,40)
	            			.add(visualiseButton,BH,BH,BH)
//	            			.add(debugButton1,BH,BH,BH)
	            			);
        } else {
        	modeLayout.setHorizontalGroup(modeLayout.createParallelGroup()
            		.add(prefsButton,40,40,40)
            		.add(miniButton,40,40,40)
            		.add(fullButton,40,40,40)
            		.add(visualiseButton,40,40,40)
            		.add(compactButton,40,40,40)
            		.add(removeTitlebarButton,40,40,40)
            		.add(buyButton,40,40,40)
  //          		.add(debugButton1,40,40,40)
            		);

        	modeLayout.setVerticalGroup(modeLayout.createSequentialGroup()
            			.add(prefsButton,BH,BH,BH)
            			.add(miniButton,BH,BH,BH)
            			.add(fullButton,BH,BH,BH)	
            			.add(visualiseButton,BH,BH,BH)
            			.add(compactButton,BH,BH,BH)	
            			.add(removeTitlebarButton,BH,BH,BH)	
            			.add(buyButton,BH,BH,BH)	
   //         			.add(debugButton1,BH,BH,BH)
            			);	
        }
        
	}

	public void layoutAboveDigitsPanel(DigitDecorationPanel aboveDigitsPanel, boolean fullScreenMode) {
		List<GlowButton> list = new ArrayList<GlowButton>();
		list.add(prefsButton.makeLinkedCopy());
		if (!fullScreenMode) list.add(miniButton.makeLinkedCopy());
		list.add(fullButton.makeLinkedCopy());
		list.add(filler2.makeLinkedCopy());
		list.add(visualiseButton.makeLinkedCopy());
		list.add(compactButton.makeLinkedCopy());
		if (!fullScreenMode) list.add(removeTitlebarButton.makeLinkedCopy());
		if (!fullScreenMode) list.add(buyButton.makeLinkedCopy());
_MARKER.mark();		
		//list.add(debugButton1.makeLinkedCopy());
		
		for (GlowButton b : list) {
			b.setHorizontalAlignment(SwingConstants.CENTER);
		}
		aboveDigitsPanel.setupButtons(list);
		
	}

	public void setTweakButton(GlowButton tweakButton) {
		this.tweakButton = tweakButton;
_MARKER.mark();
	}

}

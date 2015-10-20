package blingclock.digits;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import blingclock.layout.MetricsSharing;

public class DigitsPanel extends JPanel {

	private GridBagLayout layout;
	private int lastDays = 0;
	private boolean lastHourGreaterThanZero = false;
	private String daysAsString = ""; //$NON-NLS-1$
	private LEDDigit[] dayDigits = new LEDDigit[0];
	private LEDDigit s1,s2,m1,m2,h1,h2;
	private DecimalDigitPanel decimalPanel;
	private boolean showDecimals = false;
	private LEDSeparator sep1,sep2,daySep;
	private int digitCols;
	private DigitsPanelLabelPanel topLabel;
	
	private Timer separatorTimer;
	private Timer blinkTimer;
	private boolean slowMode;
	
	private boolean preserveDigitsRatio = false;
	private boolean titleVisible;
	private boolean paintjamMode;
	private boolean freeratio;
	
	private JPanel topFiller;
	private JPanel bottomFiller;
	private JPanel leftFiller;
	private JPanel rightFiller;
	
	private DigitDecorationPanel aboveDigitsPanel;
	private DigitDecorationPanel belowDigitsPanel;
	
	//private static final double MIN_RATIO = 2.6; 
	private static double MIN_RATIO = 2.5; 
	//private static final double MAX_RATIO = 4.2;
	private static double MAX_RATIO = 2.7;
	private int trimx;
	private int trimy;
	
	private boolean currentSeparatorBlink = false;
	private boolean currentBlink = false;
	
	private DigitsPanelListener delegate;
	
	private boolean isAtLeastAnHour;
	
	private boolean showRegistered;
	
	public DigitsPanel(DigitsPanelListener delegate) {
		super();
		this.delegate = delegate;
		init();
	}

	public boolean isPreserveDigitsRatio() {
		return preserveDigitsRatio;
	}
	
	public void setShowDecimals(boolean b) {
		showDecimals = b;
		relayout();
	}
	
	public void setAllowPlusMinus(boolean b) {
		topLabel.setAllowPlusMinus(b);
	}

	public void setPreserveDigitsRatio(boolean preserveDigitsRatio) {
		this.preserveDigitsRatio = preserveDigitsRatio;
		if (preserveDigitsRatio) {
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					recalculateLayout();
				}
			});
		}
	}
	
	public DigitDecorationPanel getAboveDigitsPanel() { return aboveDigitsPanel; } 
	public DigitDecorationPanel getBelowDigitsPanel() { return belowDigitsPanel; }
	
	@Override
	public void invalidate() {
		super.invalidate();
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			updateTitleLabelAndMetrics(); // Update label with positions of digits
		}});
	}

	private void init() {
		topFiller = new JPanel();
		bottomFiller = new JPanel();		
		leftFiller = new JPanel();		
		rightFiller = new JPanel();		
		aboveDigitsPanel = new DigitDecorationPanel(false);
		belowDigitsPanel = new DigitDecorationPanel(true);
		s1 = new LEDDigit();
		s2 = new LEDDigit();
		m1 = new LEDDigit();
		m2 = new LEDDigit();
		h1 = new LEDDigit();
		h2 = new LEDDigit();
		sep1 = new LEDSeparator(false);
		sep2 = new LEDSeparator(false);
		daySep = new LEDSeparator(true);
		
		decimalPanel = new DecimalDigitPanel();
		
		topLabel = new DigitsPanelLabelPanel(delegate);		
		topLabel.setForeground(Color.WHITE);
		topLabel.setFont(new Font(DigitStyling.FONT, Font.PLAIN, 10));
		
		setupLayout();
		setBackground(new Color(0,0,0));
		// Comment out 4 lines below to see filler in action
		topFiller.setBackground(new Color(0,0,0));
		bottomFiller.setBackground(new Color(0,0,0));
		leftFiller.setBackground(new Color(0,0,0));
		rightFiller.setBackground(new Color(0,0,0));
		createBlinkers();
	}
	
	protected void recalculateLayout() {
		int hf = getHeight() / 40;
		int wf = getWidth() / 40;
		int font = Math.max(hf,wf);
		
		topLabel.setFont(new Font(DigitStyling.FONT, Font.PLAIN, font));
		
		double aspectRatio = (double)getWidth() / (double)getHeight();

		if (aspectRatio < MIN_RATIO) {
			double idealHeight = getWidth() / MIN_RATIO;
			trimy = (int)((getHeight() - idealHeight) / 2);
			if (trimy < 0) trimy = 0;
		} else {
			trimy = 0;
		}
		if (aspectRatio > MAX_RATIO) {
			double idealWidth = getHeight() * MAX_RATIO;
			trimx = (int)((getWidth() - idealWidth) / 2);
			if (trimx < 0) trimx = 0;			
		} else {
			trimx = 0;
		}
		layout.setConstraints(topFiller, getTopFillerGBC());
		layout.setConstraints(belowDigitsPanel, getBelowDigitsGBC(digitCols));
		layout.setConstraints(aboveDigitsPanel, getAboveDigitsGBC(digitCols));
		layout.setConstraints(bottomFiller, getBottomFillerGBC());
		layout.setConstraints(leftFiller, getVerticalFillerGBC());
		layout.setConstraints(rightFiller, getVerticalFillerGBC());
    	layout.layoutContainer(this);
    	
    	setVisible(true);
//System.out.println(aspectRatio);
    	updateTitleLabelAndMetrics();
	}

	public void setShowRegistered(boolean showRegistered) {
		if (this.showRegistered != showRegistered) {
			this.showRegistered = showRegistered;
			s1.setShowRegistered(showRegistered);
			s2.setShowRegistered(showRegistered);
			m1.setShowRegistered(showRegistered);
			m2.setShowRegistered(showRegistered);
			h1.setShowRegistered(showRegistered);
			h2.setShowRegistered(showRegistered);
			sep1.setShowRegistered(showRegistered);
			sep2.setShowRegistered(showRegistered);
			daySep.setShowRegistered(showRegistered);
			decimalPanel.setShowRegistered(showRegistered);
			repaint();
		}
	}

	private void updateTitleLabelAndMetrics() {
		int firstX = 0;
    	if (dayDigits.length > 0) {
    		topLabel.setDayStart(dayDigits[0].getX());
    		LEDDigit lastDayDigit = dayDigits[dayDigits.length-1];
    		topLabel.setDayEnd(lastDayDigit.getX()+lastDayDigit.getWidth());
    		firstX = lastDayDigit.getX();
    	} else {
    		topLabel.setDayStart(0);
    		topLabel.setDayEnd(0);
    		firstX = h2.getX();
    	}

   		double digitsH = s1.getHeight();
		double digitsW = s1.getWidth() + s1.getX() - firstX;

    	MetricsSharing.getInstance().setMetric("digits-w", digitsW); //$NON-NLS-1$
    	MetricsSharing.getInstance().setMetric("digits-h", digitsH); //$NON-NLS-1$
    	MetricsSharing.getInstance().setMetric("digits-leftfill", leftFiller.getWidth()); //$NON-NLS-1$
    	MetricsSharing.getInstance().setMetric("digits-rightfill", rightFiller.getWidth()); //$NON-NLS-1$
    	    	
    	if (isAtLeastAnHour || !DigitPrefs.getInstance().getHideHourDigitsIfPossible()) {
    		topLabel.setHourStart(h2.getX() - leftFiller.getWidth());
    		topLabel.setHourEnd(h1.getX()+h1.getWidth() - leftFiller.getWidth());
    	} else {
    		topLabel.setHourStart(0);
    		topLabel.setHourEnd(0);
    	}
    	topLabel.setMinuteStart(m2.getX() - leftFiller.getWidth());
    	topLabel.setMinuteEnd(m1.getX()+m1.getWidth() - leftFiller.getWidth());
    	topLabel.setSecondStart(s2.getX() - leftFiller.getWidth());
    	topLabel.setSecondEnd(s1.getX()+s1.getWidth() - leftFiller.getWidth());
    	topLabel.repaint();
	}
	
	public void setTitleVisible(boolean titleVisible) {
		topLabel.setTitleVisible(titleVisible);
		this.titleVisible = titleVisible;
		removeAll();
		setupLayout();
		validate();
	}
	
	public boolean isTitleVisible() {
		return titleVisible;
	}
	

	public void setPaintjamMode(boolean paintjamMode) {
		//if (this.paintjamMode = paintjamMode) return;
		this.paintjamMode = paintjamMode;
		MIN_RATIO = paintjamMode ? 1.0 : 2.6;
		removeAll();
		setupLayout();
		validate();
	}
	
	public void setFreeRatio(boolean freeratio) {
		//if (this.freeratio != freeratio) {
			this.freeratio = freeratio;
			MIN_RATIO = freeratio ? 1.0 : 2.6;
			MAX_RATIO = freeratio ? 4.0 : 2.7;
			removeAll();
			setupLayout();
			validate();
		//}
	}


	private void setupLayout() {
		layout = new GridBagLayout();
		setLayout(layout);
		digitCols = 1;
		// DISPLAY
		layout.setConstraints(topFiller, getTopFillerGBC());
		add(topFiller,getTopFillerGBC());
		add(leftFiller,getVerticalFillerGBC());
		if (dayDigits.length > 0) {
			for (LEDDigit d : dayDigits) {
				add(d,getDigitGBC(digitCols++));
			}
			add(daySep,getDaySeparatorGBC(digitCols++));
		}
		
		if (!paintjamMode) {
			if (isAtLeastAnHour || !DigitPrefs.getInstance().getHideHourDigitsIfPossible()) {		
				add(h2,getDigitGBC(digitCols++));
				add(h1,getDigitGBC(digitCols++));
				add(sep1,getSeparatorGBC(digitCols++));
			}
			add(m2,getDigitGBC(digitCols++));	
		}
		
		add(m1,getDigitGBC(digitCols++));
		add(sep2,getSeparatorGBC(digitCols++));
		add(s2,getDigitGBC(digitCols++));
		add(s1,getDigitGBC(digitCols++));
		if (showDecimals) {
			add(decimalPanel,getDecimalGBC(digitCols++));
		}
		add(rightFiller,getVerticalFillerGBC());
		add(aboveDigitsPanel,getAboveDigitsGBC(digitCols));
		add(belowDigitsPanel,getBelowDigitsGBC(digitCols));
		add(bottomFiller,getBottomFillerGBC());
		
		add(topLabel,getTopGBC());
		
	}

	public void relayout() {
		relayoutDigits(lastDays);
	}

	private void relayoutDigits(int days) {
		if (days == 0) {
			dayDigits = new LEDDigit[0];
		} else {
			int digits = String.valueOf(days).length();
			dayDigits = new LEDDigit[digits];
			for (int i=0; i<digits; i++) {
				dayDigits[i] = new LEDDigit();
			}
		}
		removeAll();
		setupLayout();
		validate();
		//updateTitleLabel();
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			updateTitleLabelAndMetrics();
		}});
	}

	public void startSeparatorBlink() {
		separatorTimer.start();
	}
	
	public void stopSeparatorBlink() {
		separatorTimer.stop();
		currentSeparatorBlink = false;
		onSeparatorTimer();
	}
	
	public void startDigitBlink() {
		slowMode = false;
		blinkTimer.start();
	}
	
	public void startSlowDigitBlink() {
		slowMode = true;
		blinkTimer.start();
	}
	
	public void stopDigitBlink() {
		blinkTimer.stop();
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			currentBlink = false;
			onBlinkTimer();
		}});
	}
	
	private void createBlinkers() {
		separatorTimer = new Timer(500,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onSeparatorTimer();
		}});
		blinkTimer = new Timer(500,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onBlinkTimer();
		}});
	}


	protected void onSeparatorTimer() {
		currentSeparatorBlink = !currentSeparatorBlink;
		sep1.setBlink(currentSeparatorBlink);
		sep2.setBlink(currentSeparatorBlink);
	}
	
	private int deferBlinkOff;
	protected void onBlinkTimer() {
		if (slowMode && currentBlink & deferBlinkOff < 2) {
			deferBlinkOff++;
			return;
		}
		deferBlinkOff = 0;
		currentBlink = !currentBlink;
		s1.setBlink(currentBlink);
		s2.setBlink(currentBlink);
		m1.setBlink(currentBlink);
		m2.setBlink(currentBlink);
		h1.setBlink(currentBlink);
		h2.setBlink(currentBlink);
		decimalPanel.setBlink(currentBlink);
	}
	
	private GridBagConstraints getTopGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = titleVisible ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.gridx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = titleVisible ? 1 : 0;
		gbc.weightx = 1;
		gbc.weighty = 0.1;
		gbc.ipady = 8;
		gbc.insets = new Insets(0,0,1,0);
		return gbc;
	}

	private GridBagConstraints getSeparatorGBC(int grid) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 3;
		gbc.gridx = grid;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		return gbc;
	}

	private GridBagConstraints getDaySeparatorGBC(int grid) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 3;
		gbc.gridx = grid;
		gbc.weightx = 1;
		gbc.weighty = 1;
		return gbc;
	}

	private GridBagConstraints getDigitGBC(int grid) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 3;
		gbc.gridx = grid;
		gbc.weightx = 1;
		gbc.weighty = 1;
		return gbc;	
	}
	
	private GridBagConstraints getDecimalGBC(int grid) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 3;
		gbc.gridx = grid;
		gbc.weightx = 1;
		gbc.weighty = 0.5;
		return gbc;	
	}

	
	private GridBagConstraints getVerticalFillerGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 3;
		//gbc.gridx = 0;
		gbc.ipadx = trimx;
		return gbc;	
	} 
	
	private GridBagConstraints getTopFillerGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.ipady = trimy;
		return gbc;	
	}
	
	private GridBagConstraints getBelowDigitsGBC(int cols) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 4;
		gbc.gridx = 1;
		gbc.gridwidth = cols;
		gbc.insets = new Insets(4,0,0,0);
		gbc.ipady = 16;
		return gbc;	
	}
	
	
	private GridBagConstraints getAboveDigitsGBC(int cols) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 2;
		gbc.gridx = 1;
		gbc.gridwidth = cols;
		gbc.insets = new Insets(4,0,0,0);
		gbc.ipady = 12;
		return gbc;	
	}
	
	private GridBagConstraints getBottomFillerGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 4;
		gbc.ipady = trimy;
		return gbc;	
	}
	
	public void setCounter(int days,long countDownSeconds,long countDownHundredths,boolean clockMode) { 
		int h = (int) countDownSeconds / (60 * 60);
		isAtLeastAnHour = (h > 0);
		if (days != lastDays || lastHourGreaterThanZero != isAtLeastAnHour) {
			daysAsString = String.valueOf(days);
			relayoutDigits(days);
			lastDays = days;
			lastHourGreaterThanZero = isAtLeastAnHour;
		}
    	
    	int left = (int) countDownSeconds - (h * 60 * 60);
    	int m = left / 60;
    	left -= (m * 60);
    	int s = left;
    	
    	//
    	// Seconds
    	//
    	String sx = String.valueOf(s);
    	if (sx.length() < 2) {
    		s2.setCharacter("0"); //$NON-NLS-1$
    		s1.setCharacter(sx);
    	} else {
    		s2.setCharacter(sx.substring(0,1));
    		s1.setCharacter(sx.substring(1,2));    		
    	}
    	
    	//
    	// Minutes
    	//
    	if (h == 0 && m == 0 && !clockMode) {
    		m2.setCharacter(""); //$NON-NLS-1$
    		m1.setCharacter(""); //$NON-NLS-1$
    		sep2.setOn(false);
    	} else {
    		String mx = String.valueOf(m);
	    	if (mx.length() < 2) {
	    		m2.setCharacter((h == 0 && !clockMode) ? "" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
	    		m1.setCharacter(mx);
	    	} else {
	    		m2.setCharacter(mx.substring(0,1));
	    		m1.setCharacter(mx.substring(1,2));    		
	    	}
	    	sep2.setOn(true);
    	}
    	
    	// Hours
    	if (h == 0 && !clockMode) {
    		h2.setCharacter(""); //$NON-NLS-1$
    		h1.setCharacter(""); //$NON-NLS-1$
    		sep1.setOn(false);
    		
    	} else {
    		isAtLeastAnHour = true;
    		if (clockMode && !DigitPrefs.getInstance().getShow24Clock()) {
    			boolean pm = (h >= 12);
    			h2.showPM(pm);
    			h2.showAM(!pm);
    			if (h >= 13) {
    				h -= 12;
    			}
    		} else {
    			h2.showAM(false);
    			h2.showPM(false);
    		}
    		String hx = String.valueOf(h);
	    	if (hx.length() < 2) {
	    		h2.setCharacter(""); //$NON-NLS-1$
	    		h1.setCharacter(hx);
	    	} else {
	    		h2.setCharacter(hx.substring(0,1));
	    		h1.setCharacter(hx.substring(1,2));    		
	    	}
	    	sep1.setOn(true);
    	}
    	
    	// Days
    	if (days > 0) {
    		for (int i=0; i<daysAsString.length(); i++) {
    			dayDigits[i].setCharacter(daysAsString.substring(i,i+1));
    		}
    	}
    	
    	if (showDecimals) {
    		decimalPanel.setDecimal(countDownHundredths % 100);
    	}
    }

}

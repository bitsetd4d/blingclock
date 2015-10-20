package countdowntimer.mini;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MiniDigitPanel extends JPanel {
	
	private Icon[] digitIcons = new Icon[10];
	private JLabel[] digits = new JLabel[6];
	private int countDownSeconds;

	public void setShowDigits(boolean show) {
		if (show) {
			displayDigits();
		} else {
			for (JLabel label : digits) {
				label.setIcon(null);
			}
		}
	}

	public MiniDigitPanel() {
		super();
		setBackground(new Color(0,0,0));
		layoutDigits();
	}

	public MiniDigitPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		layoutDigits();
	}

	public MiniDigitPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		layoutDigits();
	}

	public MiniDigitPanel(LayoutManager layout) {
		super(layout);
		layoutDigits();
	}
	
	private void layoutDigits() {
		setLayout(null);
		for (int i=0; i<10; i++) {
			digitIcons[i] = makeDigitIcon(i);
		}
		int x = 2;
		for (int i=0; i<6; i++) {
			digits[i] = new JLabel();
			digits[i].setIcon(digitIcons[0]);
			add(digits[i]);
			digits[i].setLocation(x, 3);
			digits[i].setSize(5,12);
			x += 6;
			if (i == 1) x += 2;
			if (i == 3) x += 2;
		}
		
	}

	public void setTimeRemaining(int seconds) { 
		countDownSeconds = seconds;
		displayDigits();
	}
	
	private void setDigit(int index,int value) {
		Icon icon = null;
		if (value >= 0 && value <= 9) {
			icon = digitIcons[value];
		}
		digits[index].setIcon(icon);
	}
	
	private void displayDigits() { 
    	int h = countDownSeconds / (60 * 60);
    	int left = countDownSeconds - (h * 60 * 60);
    	int m = left / 60;
    	left -= (m * 60);
    	int s = left;
    	
    	// 01 23 45
    	
    	//
    	// Seconds
    	//
    	if (s < 10) {
    		setDigit(4,0);
    		setDigit(5,s);
    	} else {
    		setDigit(4,s / 10);
    		setDigit(5,s % 10);
    	}
    	
    	//
    	// Minutes
    	//
    	if (h == 0 && m == 0) {
    		setDigit(2,-1);
    		setDigit(3,-1);
//    		sep2.setOn(false);
    	} else {
	    	if (m < 10) {
	    		setDigit(2, h == 0 ? -1 : 0);
	    		setDigit(3, m);
	    	} else {
	    		setDigit(2,m / 10);
	    		setDigit(3,m % 10);
	    	}
//	    	sep2.setOn(true);
    	}
    	
    	// Hours
    	if (h == 0) {
    		setDigit(0,-1);
    		setDigit(1,-1);
//    		sep1.setOn(false);
    	} else {
	    	if (h < 10) {
	    		setDigit(0,-1);
	    		setDigit(1,h);
	    	} else {
	      		setDigit(0,h / 10);
	    		setDigit(1,h % 10);
	    	}
//	    	sep1.setOn(true);
    	}
    }
	
	private Icon makeDigitIcon(int digit) {
		Icon icon = ImageLoader.loadImage("number"+digit+".png"); //$NON-NLS-1$ //$NON-NLS-2$
		return icon;
	}

	
}

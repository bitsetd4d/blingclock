package blingclock.digits;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class DecimalDigitPanel extends JPanel {
	
	private LEDDigit d1 = new LEDDigit();
	private LEDDigit d2 = new LEDDigit();
	
	public DecimalDigitPanel() {
		setBackground(Color.BLACK);
		init();
	}
	
	private void init() {
		d1.setCharacter("9"); //$NON-NLS-1$
		d2.setCharacter("8"); //$NON-NLS-1$
		setLayout(new GridLayout(2,2));
		add(new JLabel());
		add(new JLabel());
		add(d1);
		add(d2);
	}
	
	public void setShowRegistered(boolean showRegistered) {
		d1.setShowRegistered(showRegistered);
		d2.setShowRegistered(showRegistered);
	}

	public void setDecimal(long d) {
		String c1 = "0"; //$NON-NLS-1$
		String c2 = "0"; //$NON-NLS-1$
		if (d < 10) {
			c2 = String.valueOf(d);
		} else {
			String c = String.valueOf(d);
			c1 = String.valueOf(c.charAt(0));
			c2 = String.valueOf(c.charAt(1));
		}
		d1.setCharacter(c1);
		d2.setCharacter(c2);
		
	}

	public void setBlink(boolean b) {
		d1.setBlink(b);
		d2.setBlink(b);
	}


}

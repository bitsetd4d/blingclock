package countdowntimer.controls;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class FadablePanel extends JPanel implements Fadable {

	public void setAlpha(float visibility) {
		for (Component c : getComponents()) {
			fade(c,visibility);
		}
	}
	
	public static void fade(Component c,float visibility) {
		if (c instanceof Fadable) {
			Fadable f = (Fadable)c;
			f.setAlpha(visibility);
		} else if (c instanceof JCheckBox) {
			JCheckBox j = (JCheckBox)c;
			j.setVisible(visibility > 0.9);
		} else if (c instanceof JRadioButton) {
			JRadioButton j = (JRadioButton)c;
			j.setVisible(visibility > 0.9);
		} else if (c instanceof JLabel && ((JLabel)c).getIcon() != null) {
			c.setVisible(visibility > 0.9);
		} else {
			Color fg = c.getForeground();
			int alpha = (int)(255 * visibility);
			Color newFg = new Color(fg.getRed(),fg.getGreen(),fg.getBlue(),alpha);
			c.setForeground(newFg);
		}	
	}

}

package countdowntimer.mini;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import blingclock.controls.RepeatingButton;


public class MiniControls extends JPanel {
	
	private PanelFeedbackListener listener;
	
	private JButton plusButton = new RepeatingButton();
	private JButton minusButton = new RepeatingButton();

	public MiniControls(PanelFeedbackListener myListener) {
		super();
		this.listener = myListener;
		setBackground(new Color(0,0,0));
		plusButton.setIcon(ImageLoader.loadImage("tiny_plus.png")); //$NON-NLS-1$
		minusButton.setIcon(ImageLoader.loadImage("tiny_minus.png")); //$NON-NLS-1$
		add(plusButton);
		add(minusButton);
		
		setLayout(null);
		plusButton.setLocation(6,3);
		plusButton.setSize(6,6);
		minusButton.setLocation(6,10);
		minusButton.setSize(6,6);
		
		plusButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
			listener.onPlusButtonPressed();
			//doDebug1();
		}});
		minusButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
			listener.onMinusButtonPressed();
		}});	
	
	}
	
}

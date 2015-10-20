package countdowntimer.timer.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class CurrentTimersPanel extends JPanel {
	
	private int myAlpha;
	
	//private JScrollBar verticalScrollbar;
	private JScrollPane scrollPane;
	private CurrentTimersPane timersPane;
	
	public CurrentTimersPanel() {
		super();
		myAlpha = 240;
		createControls();
	}
	
//	@Override
//	protected void paintComponent(Graphics g) {
//		g.setColor(new Color(20,20,20,myAlpha));
//		g.fillRect(0, 0, getWidth(), getHeight());
//		super.paintComponent(g);
//	}

	private void createControls() {
//		verticalScrollbar = new JScrollBar(JScrollBar.VERTICAL);
//		horizontalScrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
		setLayout(new GridLayout(1,1));
		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timersPane = new CurrentTimersPane();
		scrollPane.setViewportView(timersPane);
		
//		GridBagConstraints gbc;
//        gbc = new GridBagConstraints();
//        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.VERTICAL;
//        add(verticalScrollbar, gbc);
//        
//        gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.anchor = GridBagConstraints.NORTHWEST;
//        gbc.weightx = 1.0;
//        gbc.weighty = 1.0;
//        add(pane, gbc);
//        
//        verticalScrollbar.setMaximum(1000);
//        verticalScrollbar.addAdjustmentListener(new AdjustmentListener() { 
//        	public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e) {
//        		int y = verticalScrollbar.getValue();
////        		pane.setVerticalPosition(y);
//        	};
//        });		
	}

	public void hideAll() {		
//		pane.setVisible(false);
//		verticalScrollbar.setVisible(false);
	}

	public void setPositions() {
		
	}

	public void showContents() {
//		pane.setVisible(true);
//		verticalScrollbar.setVisible(true);
	}



}

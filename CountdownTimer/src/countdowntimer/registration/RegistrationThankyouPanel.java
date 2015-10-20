package countdowntimer.registration;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import blingclock.controls.HQJLabel;
import blingclock.visualiser.TimeBarVisualiserPanel;

import countdowntimer.Styling;
import countdowntimer.sound.SoundPlayer;
import countdowntimer.tweaks.LabelAnimator;
import countdowntimer.util.TimedRunner;
import countdowntimer.util.TimedTask;
import countdowntimer.visualiser.SingleTimeBarVisualiserPanel;

public class RegistrationThankyouPanel extends JPanel  {
	
	private Font titleFont = new Font(Styling.FONT, Font.BOLD, 15);
	private Font mainFont = new Font(Styling.FONT, 0, 13);
	
	private LabelAnimator animator = new LabelAnimator();
	
	public RegistrationThankyouPanel() {
		super();
		init();
	}

	public RegistrationThankyouPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public RegistrationThankyouPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public RegistrationThankyouPanel(LayoutManager layout) {
		super(layout);
		init();
	}
	
	
	private void init() {	
    	TimeBarVisualiserPanel.globalDisableMouseMonitoring();  // Panel turns these off
    	SingleTimeBarVisualiserPanel.globalDisableMouseMonitoring();
		setOpaque(false);
		setLayout(new GridLayout(8,1));
		setBackground(new Color(0f,0.1f,0f));
		addTitle(Messages.getString("RegistrationThankyouPanel.0")); //$NON-NLS-1$
		addText(Messages.getString("RegistrationThankyouPanel.1")); //$NON-NLS-1$
		addText(Messages.getString("RegistrationThankyouPanel.2")); //$NON-NLS-1$
		addText(Messages.getString("RegistrationThankyouPanel.3")); //$NON-NLS-1$
		addCentreTitle(Registration.getInstance().getRegisteredName());
		addCentreText(Registration.getInstance().getRegisteredEmail());
		addText(Messages.getString("RegistrationThankyouPanel.4")); //$NON-NLS-1$
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fadeOutPanel();
			}
		});
		animator.resetLabelColors();
		playSound();
	}
	
	private void playSound() {
		URL url = getSoundUrl("alarm-trumpet-fanfare.wav"); //$NON-NLS-1$
		SoundPlayer.playSound(url,0,false);
	}
	
	private URL getSoundUrl(String name) { 
		Class cls = getClass();
		URL url = cls.getResource("/sounds/" + name); //$NON-NLS-1$
		try {
			if (url == null) {
				return new URL("file:sounds" + File.separator + name); //$NON-NLS-1$
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;	
	}
	

	

	private void addCentreText(String text) {
		HQJLabel l1 = new HQJLabel(text);
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		//l1.setForeground(new Color(1f,1f,1f));
		l1.setFont(mainFont);
		l1.setSize(getWidth(),l1.getHeight());
		add(l1);		
		animator.setNormalColor(l1,new Color(1f,1f,1f));
	}

	private void addCentreTitle(String text) {
		HQJLabel l1 = new HQJLabel(text);
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		//l1.setForeground(new Color(1f,1f,1f));
		animator.setNormalColor(l1,new Color(1f,1f,1f));
		l1.setFont(titleFont);
		l1.setSize(getWidth(),l1.getHeight());
		add(l1);		
	}

	private void addTitle(String title) {
		HQJLabel l1 = new HQJLabel(title);
		//l1.setForeground(new Color(1f,1f,1f));
		animator.setNormalColor(l1,new Color(1f,1f,1f));
		l1.setFont(titleFont);
		add(l1);
	}
	
	private void addText(String text) {
		HQJLabel l1 = new HQJLabel(text);
		//l1.setForeground(new Color(0,1f,0));
		animator.setNormalColor(l1,new Color(0f,1f,0f));
		l1.setFont(mainFont);
		add(l1);		
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(0f,1f,0f,0.2f));
		g.fillRect(0, 0, getWidth(), 25);
		g.setColor(new Color(0f,0.1f,0f,0.85f));
		g.fillRect(0, 26, getWidth(), getHeight() - 26);
	}
	
	private void fadeOutPanel() {
		animator.fadeOut();
		TimedTask t = new TimedTask() { @Override public void run() {
			if (seconds > 0.4f) {
				Container parent = getParent();
				if (parent != null) {
					parent.remove(RegistrationThankyouPanel.this);
				   	TimeBarVisualiserPanel.globalEnableMouseMonitoring(); 
			    	SingleTimeBarVisualiserPanel.globalEnableMouseMonitoring();
					parent.repaint();
				}
				stop();
			}
		}};
		TimedRunner.getInstance().run(t);
	}
		 
}

package blingclock.digits;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.Timer;

import blingclock.controls.ButtonHeldTimerCallbacks;
import blingclock.util.GraphicsUtil;
import blingclock.util.MouseTrackingService;


public class DigitsPanelLabelPanel extends JComponent {
	
	private static final String DAYS_LABEL = Messages.getString("DigitsPanelLabelPanel.DaysLabel"); //$NON-NLS-1$
	private static final String HOURS_LABEL = Messages.getString("DigitsPanelLabelPanel.HoursLabel"); //$NON-NLS-1$
	private static final String MINUTES_LABEL = Messages.getString("DigitsPanelLabelPanel.MinutesLabel"); //$NON-NLS-1$
	private static final String SECONDS_LABEL = Messages.getString("DigitsPanelLabelPanel.SecondsLabel"); //$NON-NLS-1$
	
	private int dayx1,dayx2,dayCx;
	private int hourx1,hourx2,hourCx;
	private int minutex1,minutex2,minuteCx;
	private int secx1,secx2,secCx;
	
	private boolean mouseInArea;
	
	private Map<String,Rectangle> areas = new HashMap<String,Rectangle>();
	private String currentMouseOver;
	
	private ButtonHeldTimerCallbacks heldCallbacks;
	private DigitsPanelListener delegate;
	
	private boolean titleVisible;
	private boolean dontShowBecauseNotRegistered;
	private float registeredOnlyCounter = 0f;
	private boolean showingUnregisteredAtStartup = false;
	private boolean allowPlusMinus = false;
	
	private Timer fadingTimer;
	private float widgetAlpha = 0f;
	
	public DigitsPanelLabelPanel(DigitsPanelListener delegate) {
		this.delegate = delegate;
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {
				mouseInArea = true;
				repaint();
				if (!fadingTimer.isRunning()) {
					fadingTimer.start();
				}
			}

			public void mouseExited(MouseEvent e) {
				mouseInArea = false;
				repaint();
				if (!fadingTimer.isRunning()) {
					fadingTimer.start();
				}
			}

			public void mousePressed(MouseEvent e) { heldCallbacks.onMousePressed(); }
			public void mouseReleased(MouseEvent e) { heldCallbacks.onMouseReleased(); }

		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {}
			public void mouseMoved(MouseEvent e) {
				try {
					onMouseMoved(e.getX(),e.getY());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} 
		});
		heldCallbacks = new ButtonHeldTimerCallbacks();
		heldCallbacks.addListener(new SpeedupGlowButtonAdapter() {
			public void onClicked() {
				onMouseClicked();
			}			
		});
		fadingTimer = new Timer(50,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				onFadeWidgets();
			} catch (Exception ex) {}
		}});
		if (!DigitPrefs.getInstance().isRegistered()) {
			showingUnregisteredAtStartup = true;
			showRegisteredOnlyLabels();
		}
	}
	
	public void setAllowPlusMinus(boolean b) {
		allowPlusMinus = b;
	}
	
	protected void onFadeWidgets() {
		float delta = mouseInArea ? 0.1f : -0.05f; 
		if (dontShowBecauseNotRegistered) {
			delta = -0.02f;
		}
		widgetAlpha += delta;
		if (widgetAlpha < 0) {
			widgetAlpha = 0;
			fadingTimer.stop();
		} else if (widgetAlpha > 1) {
			widgetAlpha = 1f;
			fadingTimer.stop();
		}
		repaint();
	}

	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
		repaint();
	}
		
	protected void onMouseClicked() {
		MouseTrackingService.getInstance().mouseHasBeenClicked();
		if (currentMouseOver == null) return;
		if (dontShowBecauseNotRegistered) return;
		if (!allowPlusMinus) return;
		
		VirtualComponent c = new VirtualComponent(currentMouseOver);
		MouseTrackingService.getInstance().mouseFollows(c);
		
System.out.println("CLICCCCCCCCCCCCCCCCCKKKK");
		
		if (currentMouseOver.equals("DAYS+")) { //$NON-NLS-1$
			delegate.onDayPlusClicked();
		} else if (currentMouseOver.equals("DAYS-")) { //$NON-NLS-1$
			delegate.onDayMinusClicked();
		} else if (currentMouseOver.equals("HOURS+")) { //$NON-NLS-1$
			delegate.onHourPlusClicked();
		} else if (currentMouseOver.equals("HOURS-")) { //$NON-NLS-1$
			delegate.onHourMinusClicked();
		} else if (currentMouseOver.equals("MINUTES+")) { //$NON-NLS-1$
			delegate.onMinutePlusClicked();
		} else if (currentMouseOver.equals("MINUTES-")) { //$NON-NLS-1$
			delegate.onMinuteMinusClicked();
		} else if (currentMouseOver.equals("SECONDS+")) { //$NON-NLS-1$
			delegate.onSecondPlusClicked();
		} else if (currentMouseOver.equals("SECONDS-")) { //$NON-NLS-1$
			delegate.onSecondMinusClicked();
		}
		
		
		
	}

	private void showRegisteredOnlyLabels() {
		final Timer timer = new Timer(50,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				if (registeredOnlyCounter < 1) {
					registeredOnlyCounter += 0.0125;
				} else {
					registeredOnlyCounter += 0.05;
				}
				if (registeredOnlyCounter >= 2) {
					registeredOnlyCounter = 0;
					Timer t = (Timer)e.getSource();
					t.stop();
					showingUnregisteredAtStartup = false;
				}
				repaint();
			} catch (Exception ex) {}
		}});
		timer.start();
	}

	protected void onMouseMoved(int x, int y) {
		for (Map.Entry<String,Rectangle> entry : areas.entrySet()) {
			if (entry.getValue().contains(x, y)) {
				onAreaUnderMouse(entry.getKey(),entry.getValue());
				return;
			}
		}
		onAreaUnderMouse(null,null);
	}

	private void onAreaUnderMouse(String key, Rectangle value) {
		currentMouseOver = key;
		repaint();
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		GraphicsUtil.setRenderHints(g2d);
		g.setColor(Color.BLACK);
		Rectangle clip = g.getClipBounds();
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		
		if (!titleVisible) return;
		
		int fs = Math.min(getWidth() / 40, (int) (getHeight() * 0.75));
		
		Font f = new Font(DigitStyling.FONT, Font.PLAIN,fs);
		
		boolean showRollover = false;
		if (!dontShowBecauseNotRegistered && allowPlusMinus) {
			showRollover = mouseInArea || fadingTimer.isRunning();
		}
		
		Color grey = new Color(1.0f,1.0f,1.0f,1-(widgetAlpha/2));
	
		g.setColor(showRollover ? grey : Color.WHITE);
		drawText(g2d,f,DAYS_LABEL,dayCx,false);
		drawText(g2d,f,HOURS_LABEL,hourCx,false);
		drawText(g2d,f,MINUTES_LABEL,minuteCx,false);
		drawText(g2d,f,SECONDS_LABEL,secCx,false);
		
		if (showRollover) {
			Font f2 = new Font(DigitStyling.FONT, Font.BOLD,fs);
			drawEndWidgets(g2d,f2,"DAYS",DAYS_LABEL,dayCx); //$NON-NLS-1$
			drawEndWidgets(g2d,f2,"HOURS",HOURS_LABEL,hourCx); //$NON-NLS-1$
			drawEndWidgets(g2d,f2,"MINUTES",MINUTES_LABEL,minuteCx); //$NON-NLS-1$
			drawEndWidgets(g2d,f2,"SECONDS",SECONDS_LABEL,secCx); //$NON-NLS-1$
		}
		
		if (registeredOnlyCounter > 0) {
			Color fg;
			if (registeredOnlyCounter > 1) {
				fg = new Color(0,1f,0,2.0f - registeredOnlyCounter);
			} else {
				fg = new Color(0,1f,0,registeredOnlyCounter);
			}
			g2d.setColor(fg);
			String msg = ""; //$NON-NLS-1$
			if (showingUnregisteredAtStartup) {
				if (DigitPrefs.getInstance().isTrialMode()) {
					
// Dont want to do this anymore!
//					int trials = Registration.getInstance().getTrialRunsRemaining();
//					if (trials == 1) {
//						msg = Messages.getString("DigitsPanelLabelPanel.Trial1MoreGo"); //$NON-NLS-1$
//					} else {
//						MessageFormat mf = new MessageFormat(Messages.getString("DigitsPanelLabelPanel.TrialXMoreGoes")); //$NON-NLS-1$
//						msg = mf.format(new Object[] { trials });
//					}
//				} else {
//					int x = Registration.getInstance().getDaysTillUnlockAgainApprox();
//					if (x >= 7) {
//						msg = Messages.getString("DigitsPanelLabelPanel.TrialNotAvailable"); //$NON-NLS-1$
//					} else {
//						MessageFormat mf = new MessageFormat(Messages.getString("DigitsPanelLabelPanel.TrialReenableXDays")); //$NON-NLS-1$
//						msg = mf.format(new Object[] { x });
//					}
				}
			} else {
				msg = Messages.getString("DigitsPanelLabelPanel.SorryOnlyRegisteredUsers"); //$NON-NLS-1$
			}
			if (msg.length() > 0) {
				drawText(g2d, f, msg, getWidth() / 2,true);
			}
		}
			
	}
	
	private void drawText(Graphics2D g2d,Font f,String text,float x,boolean shadeBack) {
		if (x == 0) return;
		GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(),text);
		Rectangle2D r = gv.getVisualBounds();
		float x1 = (float)(x - r.getCenterX());
		if (shadeBack) {
			Color c = g2d.getColor();
			g2d.setColor(new Color(0,0,0,0.7f));
			g2d.fill(new Rectangle2D.Double(x1,0,(int)r.getWidth(),r.getHeight()));
			g2d.setColor(c);
		}
		g2d.drawGlyphVector(gv, x1, (float)r.getHeight());	
	}
	
	private void drawEndWidgets(Graphics2D g2d, Font f, String key,String text, float x) {
		if (x == 0) return;
		String plusKey = key + "+"; //$NON-NLS-1$
		String minusKey = key + "-"; //$NON-NLS-1$
		boolean overPlus = plusKey.equals(currentMouseOver);
		boolean overMinus = minusKey.equals(currentMouseOver);
		
		GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(),text);
		GlyphVector gvPlus = f.createGlyphVector(g2d.getFontRenderContext(),"+"); //$NON-NLS-1$
		GlyphVector gvMinus = f.createGlyphVector(g2d.getFontRenderContext(),"-"); //$NON-NLS-1$
		Rectangle2D r = gv.getVisualBounds();
		
		double x1 = x - r.getCenterX() - gvPlus.getVisualBounds().getWidth()*2;
		double x2 = x + r.getCenterX() + gvPlus.getVisualBounds().getWidth();
		
		Color widgetWhite = new Color(1f,1f,1f,widgetAlpha);
		g2d.setColor(overPlus ? Color.GREEN : widgetWhite);
		g2d.drawGlyphVector(gvPlus, (float)(x1), (float)r.getHeight());	
		
		g2d.setColor(overMinus ? Color.GREEN : widgetWhite);
		g2d.drawGlyphVector(gvMinus, (float)(x2), (float)r.getHeight());	
		
		int dw = (int)gvPlus.getVisualBounds().getWidth();
		areas.put(plusKey,new Rectangle((int)x1-dw,0,dw*3,getHeight()));
		areas.put(minusKey,new Rectangle((int)x2-dw,0,dw*3,getHeight()));
	}
	
	public void setDayStart(int x) { dayx1 = x; computeCenters(); }
	public void setDayEnd(int x) { dayx2 = x; computeCenters(); }

	public void setHourStart(int x) { hourx1 = x; computeCenters(); }
	public void setHourEnd(int x) { hourx2 = x; computeCenters(); }

	public void setMinuteStart(int x) { minutex1 = x; computeCenters(); }
	public void setMinuteEnd(int x) { minutex2 = x; computeCenters(); }

	public void setSecondStart(int x) { secx1 = x; computeCenters(); }
	public void setSecondEnd(int x) { secx2 = x; computeCenters(); }
	
	private void computeCenters() { 
		dayCx = (dayx1 + dayx2) / 2;
		hourCx = (hourx1 + hourx2) / 2;
		minuteCx = (minutex1 + minutex2) / 2;
		secCx = (secx1 + secx2) / 2;
	}
	
	private class VirtualComponent extends Component {
		
		private String key;
		
		public VirtualComponent(String key) {
			this.key = key;
		}
		
		@Override
		public Point getLocationOnScreen() {
			Point p = DigitsPanelLabelPanel.this.getLocationOnScreen();
			Rectangle r = areas.get(key);
			if (r == null) return p;			
			p.translate(r.x,r.y);
			return p;
		}
	}


}

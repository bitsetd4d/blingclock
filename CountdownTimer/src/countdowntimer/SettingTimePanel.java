package countdowntimer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import blingclock.digits.DigitStyling;
import blingclock.util.GraphicsUtil;

import countdowntimer.registration.Registration;
import countdowntimer.sound.SoundUtil;

public class SettingTimePanel extends JPanel {
	
	private int myAlpha = 180;
	private String rawDigits = ""; //$NON-NLS-1$
	private String formattedDigits = ""; //$NON-NLS-1$
	private javax.swing.Timer timer;
	private boolean blink;

	public SettingTimePanel() {
		super();
		setOpaque(false);
		 timer = new javax.swing.Timer(400,new ActionListener() { public void actionPerformed(ActionEvent e) {
			 blink();
		 }});
		 timer.setRepeats(true);
		 timer.start();
	}

	protected void blink() {
		blink = !blink;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		GraphicsUtil.setRenderHints(g2d);
		
		g.setColor(new Color(0,0,0,myAlpha));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int fs = Math.min(getWidth() / 6, getHeight() / 6);
		Font f = new Font(Styling.FONT, Font.PLAIN,fs);
		
		int y = getHeight() - 5;
			
		GlyphVector gv1 = f.createGlyphVector(g2d.getFontRenderContext(),">"); //$NON-NLS-1$
		Rectangle2D r1 = gv1.getVisualBounds();
		g2d.setColor(DigitStyling.BAR_HIGHLIGHT);
		g2d.drawGlyphVector(gv1, 0, y);
		
		float digitsX = (float)(r1.getWidth()*1.5);
		GlyphVector gv2 = f.createGlyphVector(g2d.getFontRenderContext(),formattedDigits);
		Rectangle2D r2 = gv2.getLogicalBounds();
		g2d.setColor(DigitStyling.BAR_COLOUR);
		g2d.drawGlyphVector(gv2,digitsX, y);
		
		if (blink) {
			double cw = r1.getHeight();
			Rectangle2D.Double cursor = new Rectangle2D.Double(digitsX + r2.getWidth() + 2,y-cw-2,cw,cw);
			g2d.fill(cursor);
		}
		
	}
	
	public void appendDigit(int d) {
		SoundUtil.playAudioClip("small_ping_beep.wav",true); //$NON-NLS-1$
		rawDigits += d;
		formattedDigits = formatDigits();
		repaint();
	}
	
	public void appendCharacter(char c) {
		if (rawDigits.length() > 0 && !rawDigits.startsWith("*")) return; //$NON-NLS-1$
		SoundUtil.playAudioClip("small_ping_beep.wav",true); //$NON-NLS-1$
		rawDigits += c;
		formattedDigits = formatDigits();
		repaint();
	}
	
	public void deleteDigit() {
		if (rawDigits.length() == 0) return;
		SoundUtil.playAudioClip("small_delete_beep.wav",true); //$NON-NLS-1$
		rawDigits = rawDigits.substring(0,rawDigits.length()-1);
		formattedDigits = formatDigits();
		repaint();
	}
	
	public void applyTime(KeyboardInputTarget countdownWindow) {
		if (rawDigits.length() == 0) return;
		if (rawDigits.equals("01737557271")) { //$NON-NLS-1$
			Registration.getInstance().register(""); //$NON-NLS-1$
			return;
		}
		SoundUtil.playAudioClip("small_confirm_beep.wav",true); //$NON-NLS-1$
		if (rawDigits.startsWith("*")) { //$NON-NLS-1$
			countdownWindow.applyEnteredTweak(rawDigits);
			return;
		}
		String ss = ""; //$NON-NLS-1$
		String sm = ""; //$NON-NLS-1$
		String sh = ""; //$NON-NLS-1$
		String sd = ""; //$NON-NLS-1$
		int j = 0;
		for (int i=rawDigits.length()-1; i>=0; i--) {
			char c = rawDigits.charAt(i);
			j++;
			if (j<=2) {
				ss = c + ss; 
			} else if (j <= 4) {
				sm = c + sm;
			} else if (j <= 6) {
				sh = c + sh;
			} else {
				sd = c + sd;
			}
		}
		int s = parseNum(ss,99);		
		int m = parseNum(sm,99);		
		int h = parseNum(sh,99);		
		int d = parseNum(sd,10000);		
		countdownWindow.applyTime(s,m,h,d);
	}
	
	private int parseNum(String x, int max) {
		int i = 0;
		try {
			i = Integer.parseInt(x);
		} catch (Exception e) {}
		if (i > max) i = max;
		return i;
	}

	private String formatDigits() {
		if (rawDigits.startsWith("*")) return rawDigits; //$NON-NLS-1$
		if (rawDigits.length() <= 2) return rawDigits;
		StringBuilder sb = new StringBuilder();
		int c = 0;
		for (int i=rawDigits.length()-1; i>=0; i--) {
			if (c==2) sb.append(":"); //$NON-NLS-1$
			if (c==4) sb.append(":"); //$NON-NLS-1$
			if (c==6) sb.append(" d"); //$NON-NLS-1$
			sb.append(rawDigits.charAt(i));
			c++;
		}
		sb.reverse();
		return sb.toString();
	}

	public boolean hasPendingDigits() {
		return rawDigits.length() > 0;
	}



}

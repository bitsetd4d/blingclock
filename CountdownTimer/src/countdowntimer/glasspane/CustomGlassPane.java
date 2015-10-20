package countdowntimer.glasspane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.Timer;

import blingclock.controls.GlowButton;
import blingclock.util.GraphicsUtil;

import countdowntimer.registration.Registration;
import countdowntimer.tweaks.TweakRegistry;

public class CustomGlassPane extends JComponent { 
	
	private Map<JComponent,String> hoverTitle = new HashMap<JComponent,String>();
	private Map<JComponent,String> hoverBody = new HashMap<JComponent,String>();
	
	private static boolean hoverDisabled;
	private Timer hoverTimer;
	private Timer hoverHelpAnimateTimer;
	private Timer tweakAnimateTimer;
	private Timer bannerTimer;
	private Point lastMouseLocation = new Point(0,0);
	private int postHoverCount;
	
	private boolean animateTweakButtons;
	private boolean oneTime;
	
	private boolean hover;
	private int animateStage;
	private float animationAmount;
	private int tweakAnimationStage;
	private String title;
	private String body;
	private Point start;
	private Point p1;
	private Point end;
	private boolean popupRight;
	
	private Point lineTop,lineBottom;
	private Rectangle framingRect;
	
	private boolean mouseMoving;
	
	private final int RECT_W = 150;
	
	private static final int TWEAK_ANIMATION_TIME_INTERVAL = 25;
	private static final int ANIMATION_TIME_INTERVAL = 50;
	private static final int ANIMATION_STAGES = 20;
	private static final int POST_HOVER_TICKS = 4 * 4;
	
	private List<GlowButton> tweakButtons = new ArrayList<GlowButton>();
	
	private boolean showNeedsRegistered = false; 
	
	public CustomGlassPane() {
		if (!Registration.getInstance().isRegistered()) {
			startBannerTimer();
		}
	}

	public void setShowNeedsRegistered(boolean showNeedsRegistered) { 
		if (this.showNeedsRegistered != showNeedsRegistered) {
			this.showNeedsRegistered = showNeedsRegistered;
			repaint();
		}
	}

	public static void globalDisableHoverHelp(boolean b) {
		hoverDisabled = b;
	}

	@Override 
	protected void paintComponent(Graphics g) {		
		boolean reg = Registration.getInstance().isRegistered();
		if (!hover && !animateTweakButtons && !showNeedsRegistered && reg) return;
		Graphics2D g2d = GraphicsUtil.prepareGraphics(g);
		if (hover) {
			if (animateStage >= 2) {
				drawBackgroundSquare(g2d);
				drawSpanningLine(g2d,animationAmount*1.5f);
			}
			drawLineoutToSquare(g2d);
			if (animateStage >= 7) {
				float alpha = animationAmount;
				drawTitle(g2d,alpha*2-0.3f);
			}
			if (animateStage >= 10) {
				float alpha = animationAmount;
				drawBody(g2d,alpha*2-0.5f);
			}
		}
		if (animateTweakButtons) {
			drawTweakButtonAnimations(g2d);
		}
		if (showNeedsRegistered) {
			drawNeedsRegistered(g2d);
		}
		if (!reg) {
			drawBlingclockWebsite(g2d);
		}
	} 

	private void drawBackgroundSquare(Graphics2D g2d) {
		Color transBlack = new Color(0,0,0,220);
		g2d.setColor(transBlack);
		g2d.fillRect(framingRect.x,framingRect.y,framingRect.width, framingRect.height);
	}
	
	private void drawLineoutToSquare(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		Stroke oldStroke = g2d.getStroke();
	    float[] dashPattern = { 2, 2, 2, 2 };
	    g2d.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_BUTT,
	                                  BasicStroke.JOIN_MITER, 10,
	                                  dashPattern, 0));
		g2d.drawPolyline(
				new int[] { start.x, p1.x, end.x }, 
				new int[] { start.y, p1.y, end.y },
				3);
		g2d.setColor(Color.WHITE);	
		g2d.drawLine(lineTop.x,lineTop.y, lineBottom.x,lineBottom.y);
		g2d.setStroke(oldStroke);
	}
	
	private void drawSpanningLine(Graphics2D g2d, float amount) {
		if (amount > 1f) amount = 1f;
		g2d.setColor(new Color(1f,1f,1f,amount));
		Stroke oldStroke = g2d.getStroke();
	    float[] dashPattern = { 2, 2, 2, 2 };
	    g2d.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10,
                dashPattern, 0));
	    double d = 1 - Math.pow(amount,0.3);
		int dx = (int)(framingRect.width * d);
		int x;
		if (popupRight) {
			x = framingRect.x + framingRect.width - dx;
		} else {
			x = framingRect.x + dx;
		}
		g2d.drawLine(x, framingRect.y, x, framingRect.y + framingRect.height);
		g2d.setStroke(oldStroke);
	}

	
	private void drawTitle(Graphics2D g2d,float alpha) {
		if (alpha > 1f) alpha = 1f;
		Font f = new Font("Arial",Font.BOLD,13); //$NON-NLS-1$
		g2d.setFont(f);
		g2d.setColor(new Color(1f,1f,1f,alpha));
		g2d.drawString(title,framingRect.x+5,framingRect.y + 14);
	}
	
	private void drawBody(Graphics2D g2d,float alpha) {
		if (alpha > 1f) alpha = 1f;
		Font f = new Font("Arial",Font.ITALIC,12); //$NON-NLS-1$
		g2d.setFont(f);
		g2d.setColor(new Color(0,1f,0,alpha));
		drawString(g2d,body,framingRect.x + 5, framingRect.y+28, RECT_W-5);		
	}
	
	private void drawTweakButtonAnimations(Graphics2D g2d) {
		g2d.setColor(new Color(1f,1f,1f));
		for (GlowButton b : tweakButtons) {
			float alpha = 1f/15f * (tweakAnimationStage % 15);
			g2d.setColor(new Color(1f,1f,1f,1 - alpha));
			if (b.isShowing()) drawAroundButton(b, g2d);
			GlowButton b2 = b.getCompanionButton();
			if (b2 != null && b2.isShowing()) drawAroundButton(b2, g2d);
		}
	}
	
	private void drawAroundButton(GlowButton b,Graphics2D g2d) {
		Point p = getLocationComponentWithinGlassPane(b); 
		double radius = 2.5 * (tweakAnimationStage % 15);
		double cx = p.x + 8 - (radius /2);
		double cy = p.y + 4 - (radius /2);
		Arc2D.Double arc = new Arc2D.Double(cx,cy,radius,radius,0,360,Arc2D.CHORD);
		g2d.draw(arc);
	}
	
	private void drawNeedsRegistered(Graphics2D g2d) {
		if (registeredOnlyShape1 == null) {
			makeRegisteredOnly(g2d);
		}
		g2d.setColor(new Color(1f,1f,1f,0.8f));
		g2d.fill(registeredOnlyShape1);
		g2d.fill(registeredOnlyShape2);
	}
	
	private Shape registeredOnlyShape1;
	private Shape registeredOnlyShape2;
	
	private void makeRegisteredOnly(Graphics2D g2d) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<7; i++) {
			sb.append(Messages.getString("CustomGlassPane.RegUsersOnly")); //$NON-NLS-1$
		}
	    Font font = new Font("Courier", Font.PLAIN, 30); //$NON-NLS-1$
	    FontRenderContext frc = g2d.getFontRenderContext();
	    GlyphVector gv = font.createGlyphVector(frc, sb.toString());
	    AffineTransform at = AffineTransform.getTranslateInstance(10,10);	    
	    double r =  Math.PI * 0.15;
	    at.rotate(r);
	    registeredOnlyShape1 = at.createTransformedShape(gv.getOutline());
	    at.translate(0,500);
	    registeredOnlyShape2 = at.createTransformedShape(gv.getOutline());
	}
	
	private int bannerStage;
	private void drawBlingclockWebsite(Graphics2D g2d) {
		if (bannerStage > 2) return; 
		String msg1 = Messages.getString("CustomGlassPane.Banner1"); //$NON-NLS-1$
		String msg2 = Messages.getString("CustomGlassPane.Banner2"); //$NON-NLS-1$
		Font f = new Font("Arial",Font.PLAIN,13); //$NON-NLS-1$
		g2d.setFont(f);
		g2d.setColor(new Color(1f,1f,1f,1f));
		g2d.drawString(bannerStage < 2 ? msg1 : msg2, 10,getHeight() - 11);
	}

	
	private void startBannerTimer() {
		bannerTimer = new Timer(30*1000, new ActionListener() { public void actionPerformed(ActionEvent e) {
			onBannerTimerFired();
		}});
		bannerTimer.start();
	}

	protected void onBannerTimerFired() {
		bannerStage++;
		if (bannerStage == 8) {
			bannerStage = 0;
		}
		//System.out.println("B:"+bannerStage);
		repaint();
	}

	
//	private void example(Graphics g) {
//		
//	
//			    Graphics2D g2d = (Graphics2D) g;
//			    //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//			    String s = "Registered Users Only   Registered Users Only   Registered Users Only";
//
//			    Font font = new Font("Courier", Font.PLAIN, 25);
//			    g2d.translate(0, 0);
//			    FontRenderContext frc = g2d.getFontRenderContext();
//			    GlyphVector gv = font.createGlyphVector(frc, s);
//			    AffineTransform at = AffineTransform.getTranslateInstance(10,10); //p.getX(), p.getY());
//			    
//			    int length = gv.getNumGlyphs();
//			    double r =  Math.PI * 0.15;
//			    at.rotate(r);
//			    for (int i = 0; i < length; i++) {
//			      Point2D p = gv.getGlyphPosition(i);
//			      //at.rotate((double) i / (double) (length - 1) * Math.PI / 3);
//			     
//
//			      Shape glyph = gv.getGlyphOutline(i);
//			      Shape transformedGlyph = at.createTransformedShape(glyph);
//			      g2d.fill(transformedGlyph);
//			    }
//			  }

	

	@Override 
	public boolean contains(int x, int y) { 
		return false; 
	}

	public void addHover(final JComponent c,final String title,String body) {
		if (c == null) return;
		hoverTitle.put(c,title);
		hoverBody.put(c,body);
		if (c instanceof GlowButton) {
			GlowButton b = (GlowButton)c;
			GlowButton b2 = b.getCompanionButton();
			if (b2 != null && !hoverTitle.containsKey(b2)) {
				hoverTitle.put(b2,title);
				hoverBody.put(b2,body);
			}
		}
	} 
	
	public void startMouseTracking() {
		hoverTimer = new Timer(250,new ActionListener() { public void actionPerformed(ActionEvent e) {
			try {
				onMouseTrackTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
		hoverTimer.start();

		hoverHelpAnimateTimer = new Timer(ANIMATION_TIME_INTERVAL,new ActionListener() { public void actionPerformed(ActionEvent e) {
			try {
				onHoverAnimateTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
		
		tweakAnimateTimer = new Timer(TWEAK_ANIMATION_TIME_INTERVAL,new ActionListener() { public void actionPerformed(ActionEvent e) {
			try {
				onTweakAnimateTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
	}
	
	private void startHoverAnimationTimer() {
		hoverHelpAnimateTimer.start();
		animateStage = 0;
	}
	
	private void stopHoverAnimationTimer() {
		hoverHelpAnimateTimer.stop();
	}
	
	public void startTweakAnimationsOnce() {
		if (oneTime) return;
		oneTime = true; 
		startTweakAnimations();
	}
	
	public void startTweakAnimations() {
		animateTweakButtons = true;
		tweakAnimationStage = 0;
		tweakAnimateTimer.start();		
	}
	
	public void stopTweakAnimations() {
		animateTweakButtons = false;
		tweakAnimateTimer.stop();
		repaint();
	}
	
	protected void onHoverAnimateTimerFired() {
		animateStage++;
		animationAmount = animateStage / (float)ANIMATION_STAGES;
		repaint();
		if (animateStage == ANIMATION_STAGES) {
			stopHoverAnimationTimer();
		}
	}
	
	protected void onTweakAnimateTimerFired() {
		tweakAnimationStage++;
		repaint();
		if (oneTime && tweakAnimationStage == 15) {
			oneTime = false;
			stopTweakAnimations();
		}
	}

	protected void onMouseTrackTimerFired() {
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point mouseLocation = pi.getLocation();
		if (!lastMouseLocation.equals(mouseLocation)) {
			lastMouseLocation = mouseLocation;
			mouseMoving = true;
			if (hover) {
				hover = false;
				stopHoverAnimationTimer();
				repaint();
			}
			return;
		} else {
			postHoverCount++;
		}
		// Not moving any more - if we were before, check if we're over a component
		if (mouseMoving) {
			postHoverCount = 0;
			checkIfMouseOverHoverComponent(mouseLocation.x,mouseLocation.y); 
		} else {
			if (postHoverCount > POST_HOVER_TICKS) {
				stopHover();
			}
		}
		lastMouseLocation = mouseLocation;	
		mouseMoving = false;
	}

	private void checkIfMouseOverHoverComponent(int x,int y) {
		for (JComponent c : hoverTitle.keySet()) {
			if (pointOverComponent(x,y,c)) {
				if (!hover) {
					//if (TweakRegistry.getInstance().getValue(TweakRegistry.HOVER_HELP) == "ENABLED") {  //$NON-NLS-1$
					if (TweakRegistry.getInstance().isHoverHelpEnabled() && !hoverDisabled) { 
						hover = true;
						setupHover(c);
						startHoverAnimationTimer();
						postHoverCount = 0;
						repaint();
					}
				}
				return;
			}
		}
		if (hover) {
			stopHover();
		}
		
	}
	
	private void stopHover() { 
		hover = false;
		stopHoverAnimationTimer();
		repaint();
	}

	private void setupHover(JComponent c) {
		Point location = getLocationComponentWithinGlassPane(c);
		int dEnd = getWidth() - (location.x + c.getWidth());
		int dBottom = getHeight() - (location.y + c.getHeight());
		popupRight = dEnd > location.x; 		// Is there more space on right than left
		boolean popupBelow = dBottom > location.y;  	// Is there more room below
		calculateStartPoint(c, location);
		int dy = popupBelow ? 50 : -50;
		if (popupRight) {
			p1 = start.getLocation();
			p1.translate(15,0);
			end = p1.getLocation();
			end.translate(40, dy);
			framingRect = new Rectangle(end.x,end.y-40,RECT_W,80);
		} else {
			p1 = start.getLocation();
			p1.translate(-15,0);
			end = p1.getLocation();
			end.translate(-40, dy);
			framingRect = new Rectangle(end.x-RECT_W,end.y-40,RECT_W,80);
		}
		
		lineTop = new Point(end.x,end.y-40);
		lineBottom = new Point(end.x,end.y+40);
		title = hoverTitle.get(c);
		body = hoverBody.get(c);
	}

	private void calculateStartPoint(JComponent c, Point location) {
		start = location;
		if (popupRight) {
			start.translate(c.getWidth() + 2, c.getHeight()/2 - 3);
		} else {
			start.translate(-2,c.getHeight()/2 - 3);
		}
	}

	private boolean pointOverComponent(int x, int y, JComponent comp) {
		if (!comp.isShowing()) return false;
		Point onScreen = comp.getLocationOnScreen();
		int w = comp.getWidth();
		int h = comp.getHeight();
		if (x > onScreen.x && x <= onScreen.x + w && y > onScreen.y && y < onScreen.y + h) {
			return true;
		}
		return false;
	}
	
	private Point getLocationComponentWithinGlassPane(JComponent c) {
		Point componentLocation = c.getLocationOnScreen();
		Point myLocation = getLocationOnScreen();
		int dx = componentLocation.x - myLocation.x;
		int dy = componentLocation.y - myLocation.y;
		return new Point(dx,dy);
	}
	
	// better one here -> http://stackoverflow.com/questions/239537/how-to-output-a-string-on-multiple-lines-using-graphics
	public void drawString(Graphics g, String s, int x, int y, int width) {
	        // FontMetrics gives us information about the width,
	        // height, etc. of the current Graphics object's Font.
	        FontMetrics fm = g.getFontMetrics();
	        int lineHeight = fm.getHeight();

	        int curX = x;
	        int curY = y;

	        String[] words = s.split(" "); //$NON-NLS-1$
	        for (String word : words) {
	                int wordWidth = fm.stringWidth(word + " "); //$NON-NLS-1$
	                // If text exceeds the width, then move to next line.
	                if (curX + wordWidth >= x + width) {
	                        curY += lineHeight;
	                        curX = x;
	                }

	                g.drawString(word, curX, curY);
	                // Move over to the right for next word.
	                curX += wordWidth;
	        }
	}

	public void addTweakButton(GlowButton tweakButton) {
		tweakButtons.add(tweakButton);
	}

}
